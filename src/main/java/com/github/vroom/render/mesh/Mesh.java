package com.github.vroom.render.mesh;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.object.AABBGenerator;
import com.github.vroom.render.object.Collision;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL15.glActiveTexture;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBindTexture;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glDrawElements;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.memFree;

public final class Mesh {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mesh.class);

    private static final Vector3f DEFAULT_COLOR = new Vector3f(1.0f, 1.0f, 1.0f);

    private final float[] positions;

    private final float[] textCoords;

    private final float[] normals;

    private final int[] indices;

    private boolean wireframe;

    private int vaoId;

    private int vertexCount;

    private Vector3f color;

    private Material material;

    private List<Integer> vboIdList;

    private Collision[] bounds = new Collision[0];

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, boolean autoComputeCollision) {
        this(positions, textCoords, normals, indices, false, autoComputeCollision);
    }

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices, boolean createMesh,
                boolean autoComputeCollision) {
        this.positions = positions;
        this.textCoords = textCoords;
        this.normals = normals;
        this.indices = indices;

        if (createMesh) {
            createMesh();
        }

        if (autoComputeCollision) {
            this.bounds = new Collision[] { AABBGenerator.generateAABB(this) };
        }
    }

    public void createMesh() {
        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indicesBuffer = null;
        FloatBuffer vecNormalsBuffer = null;

        try {
            color = DEFAULT_COLOR;
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
            textCoordsBuffer.put(textCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

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
                memFree(posBuffer);
            }

            if (textCoordsBuffer != null) {
                memFree(textCoordsBuffer);
            }

            if (indicesBuffer != null) {
                memFree(indicesBuffer);
            }

            if (vecNormalsBuffer != null) {
                memFree(vecNormalsBuffer);
            }
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        var texture = material.getTexture();

        if (texture != null) {
            texture.cleanup();
        }

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public void render() {
        var texture = material.getTexture();

        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);

            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }

        // Draw the mesh
        glBindVertexArray(getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Vector3f getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public float[] getPositions() {
        return positions;
    }

    public Collision[] getBounds() {
        return bounds;
    }

    public Collision[] getCopiedBounds() {
        return getCopiedBounds(1);
    }

    public Collision[] getCopiedBounds(float scale) {
        return Arrays.stream(bounds).map(Collision::copy).peek(aabb -> aabb.scale(scale)).toArray(Collision[]::new);
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setMaterial(Material material) {
        this.material = material;
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
}

