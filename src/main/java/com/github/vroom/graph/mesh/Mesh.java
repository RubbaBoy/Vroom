package com.github.vroom.graph.mesh;

import com.github.vroom.graph.Material;
import com.github.vroom.graph.Texture;
import com.github.vroom.graph.collision.Collision;
import com.github.vroom.items.GameItem;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mesh.class);

    public static final int MAX_WEIGHTS = 4;

    private static final Collision[] NO_BOUNDS = new Collision[0];

    public static final Function<Mesh, Collision[]> GEN_COLLISIONS = $ -> NO_BOUNDS;

    // Used only in constructor
    private final float[] positions;
    private final float[] textCoords;
    private final float[] normals;
    private final int[] indices;
    private final int[] jointIndices;
    private final float[] weights;

    protected int vaoId;

    protected List<Integer> vboIdList;

    private final boolean createMesh;

    private float boundingRadius;

    private boolean wireframe;

    private int vertexCount;

    private Material material;

    private Collision[] bounds = NO_BOUNDS;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, boolean createMesh) {
        this(positions, textCoords, normals, indices, createMesh, GEN_COLLISIONS);
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, boolean createMesh, Function<Mesh, Collision[]> generateCollisions) {
        this(positions, textCoords, normals, indices, createEmptyIntArray(MAX_WEIGHTS * positions.length / 3, 0), createEmptyFloatArray(MAX_WEIGHTS * positions.length / 3, 0), createMesh, generateCollisions);
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights, boolean createMesh) {
        this(positions, textCoords, normals, indices, jointIndices, weights, createMesh, GEN_COLLISIONS);
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, int[] jointIndices, float[] weights, boolean createMesh, Function<Mesh, Collision[]> generateCollisions) {
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;
        this.jointIndices = jointIndices;
        this.weights = weights;
        this.createMesh = createMesh;

        if (createMesh) {
            createMesh();
        }

        this.bounds = generateCollisions.apply(this);
    }

    public void createMesh() {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        FloatBuffer weightsBuffer = null;
        IntBuffer jointIndicesBuffer = null;
        IntBuffer indicesBuffer = null;

        try {
            calculateBoundingRadius(positions);

            vertexCount = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            if ( textCoordsBuffer.capacity() > 0 ) {
                textCoordsBuffer.put(textCoords).flip();
            } else {
                // Create empty structure. Two coordinates for each 3 position coordinates
                textCoordsBuffer = MemoryUtil.memAllocFloat((positions.length * 3) / 2);
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            if ( vecNormalsBuffer.capacity() > 0 ) {
                vecNormalsBuffer.put(normals).flip();
            } else {
                vecNormalsBuffer = MemoryUtil.memAllocFloat(positions.length);
            }
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Weights
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            weightsBuffer = MemoryUtil.memAllocFloat(weights.length);
            weightsBuffer.put(weights).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, weightsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

            // Joint indices
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            jointIndicesBuffer = MemoryUtil.memAllocInt(jointIndices.length);
            jointIndicesBuffer.put(jointIndices).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, jointIndicesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer);
            }
            if (vecNormalsBuffer != null) {
                MemoryUtil.memFree(vecNormalsBuffer);
            }
            if (weightsBuffer != null) {
                MemoryUtil.memFree(weightsBuffer);
            }
            if (jointIndicesBuffer != null) {
                MemoryUtil.memFree(jointIndicesBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    private void calculateBoundingRadius(float[] positions) {
        boundingRadius = 0;
        for (float pos : positions) {
            boundingRadius = Math.max(Math.abs(pos), boundingRadius);
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public float[] getPositions() {
        return positions;
    }

    public final int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public float getBoundingRadius() {
        return boundingRadius;
    }

    public void setBoundingRadius(float boundingRadius) {
        this.boundingRadius = boundingRadius;
    }

    public Collision[] getBounds() {
        return bounds;
    }

    public Collision[] getCopiedBounds() {
        return getCopiedBounds(1);
    }

    public Collision[] getCopiedBounds(float scale) {
        return Arrays.stream(bounds).map(Collision::copy).peek(collision -> collision.scale(scale)).toArray(Collision[]::new);
    }

    public void setBounds(Collision[] bounds) {
        this.bounds = bounds;
    }

    public boolean isWireframe() {
        return wireframe;
    }

    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }

    protected void initRender() {
        Texture texture = material != null ? material.getTexture() : null;
        if (texture != null) {
            // Activate first texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        Texture normalMap = material != null ? material.getNormalMap() : null;
        if (normalMap != null) {
            // Activate second texture bank
            glActiveTexture(GL_TEXTURE1);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, normalMap.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
    }

    protected void endRender() {
        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void render() {
        initRender();

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        endRender();
    }

    public void renderList(List<GameItem> gameItems, Consumer<GameItem> consumer) {
        initRender();

        for (GameItem gameItem : gameItems) {
            if (gameItem.isInsideFrustum()) {
                // Set up data requiered by gameItem
                consumer.accept(gameItem);
                // Render this game item
                glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
            }
        }

        endRender();
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the texture
        Texture texture = material.getTexture();
        if (texture != null) {
            texture.cleanup();
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void deleteBuffers() {
        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    protected static float[] createEmptyFloatArray(int length, float defaultValue) {
        float[] result = new float[length];
        Arrays.fill(result, defaultValue);
        return result;
    }

    protected static int[] createEmptyIntArray(int length, int defaultValue) {
        int[] result = new int[length];
        Arrays.fill(result, defaultValue);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mesh mesh = (Mesh) o;
        return vaoId == mesh.vaoId &&
                createMesh == mesh.createMesh &&
                Float.compare(mesh.boundingRadius, boundingRadius) == 0 &&
                wireframe == mesh.wireframe &&
                vertexCount == mesh.vertexCount &&
                Arrays.equals(positions, mesh.positions) &&
                Arrays.equals(textCoords, mesh.textCoords) &&
                Arrays.equals(normals, mesh.normals) &&
                Arrays.equals(indices, mesh.indices) &&
                Arrays.equals(jointIndices, mesh.jointIndices) &&
                Arrays.equals(weights, mesh.weights) &&
                vboIdList.equals(mesh.vboIdList) &&
                material.equals(mesh.material) &&
                Arrays.equals(bounds, mesh.bounds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vaoId, vboIdList, createMesh, boundingRadius, wireframe, vertexCount, material);
        result = 31 * result + Arrays.hashCode(positions);
        result = 31 * result + Arrays.hashCode(textCoords);
        result = 31 * result + Arrays.hashCode(normals);
        result = 31 * result + Arrays.hashCode(indices);
        result = 31 * result + Arrays.hashCode(jointIndices);
        result = 31 * result + Arrays.hashCode(weights);
        result = 31 * result + Arrays.hashCode(bounds);
        return result;
    }
}
