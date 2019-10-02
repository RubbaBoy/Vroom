package com.github.vroom.terrain;

import com.github.vroom.render.Texture;
import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.Mesh;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static com.github.vroom.utility.Utility.listToArray;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public final class HeightMapMesh {

    private static final int MAX_COLOUR = 255 * 255 * 255;

    private static final float STARTX = -0.5f;

    private static final float STARTZ = -0.5f;

    private final float minY;

    private final float maxY;

    private final Mesh mesh;

    public HeightMapMesh(float minY, float maxY, String heightMapFile, String textureFile, int textInc) {
        this.minY = minY;
        this.maxY = maxY;

        ByteBuffer buf;

        int width;
        int height;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);

            IntBuffer channels = stack.mallocInt(1);

            if ((buf = stbi_load(heightMapFile, w, h, channels, 4)) == null) {
                throw new RuntimeException("Image file [" + heightMapFile  + "] not loaded: " + stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        var texture = new Texture(textureFile);

        float incx = getXLength() / (width - 1);
        float incz = getZLength() / (height - 1);

        var positions = new ArrayList<Float>();
        var textCoords = new ArrayList<Float>();
        var indices = new ArrayList<Integer>();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Create vertex for current position
                positions.add(STARTX + col * incx); // x
                positions.add(getHeight(col, row, width, buf)); //y
                positions.add(STARTZ + row * incz); //z

                // Set texture coordinates
                textCoords.add((float) textInc * (float) col / (float) width);
                textCoords.add((float) textInc * (float) row / (float) height);

                // Create indices
                if (col < width - 1 && row < height - 1) {
                    int leftTop = row * width + col;
                    int leftBottom = (row + 1) * width + col;
                    int rightBottom = (row + 1) * width + col + 1;
                    int rightTop = row * width + col + 1;

                    indices.add(leftTop);
                    indices.add(leftBottom);
                    indices.add(rightTop);

                    indices.add(rightTop);
                    indices.add(leftBottom);
                    indices.add(rightBottom);
                }
            }
        }

        float[] posArr = listToArray(positions);
        float[] textCoordsArr = listToArray(textCoords);
        float[] normalsArr = calcNormals(posArr, width, height);

        int[] indicesArr = indices.stream().mapToInt(Integer::intValue).toArray();

        mesh = new Mesh(posArr, textCoordsArr, normalsArr, indicesArr, true);
        mesh.setMaterial(new Material(texture, 0.0f));

        stbi_image_free(buf);
    }

    public Mesh getMesh() {
        return mesh;
    }

    public static float getXLength() {
        return Math.abs(-STARTX * 2);
    }

    public static float getZLength() {
        return Math.abs(-STARTZ * 2);
    }

    private float[] calcNormals(float[] posArr, int width, int height) {
        var v0 = new Vector3f();
        var v1 = new Vector3f();
        var v2 = new Vector3f();
        var v3 = new Vector3f();
        var v4 = new Vector3f();
        var v12 = new Vector3f();
        var v23 = new Vector3f();
        var v34 = new Vector3f();
        var v41 = new Vector3f();

        var normals = new ArrayList<Float>();
        var normal = new Vector3f();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (row > 0 && row < height - 1 && col > 0 && col < width - 1) {
                    int i0 = row * width * 3 + col * 3;
                    v0.x = posArr[i0];
                    v0.y = posArr[i0 + 1];
                    v0.z = posArr[i0 + 2];

                    int i1 = row * width * 3 + (col - 1) * 3;
                    v1.x = posArr[i1];
                    v1.y = posArr[i1 + 1];
                    v1.z = posArr[i1 + 2];
                    v1 = v1.sub(v0);

                    int i2 = (row + 1) * width * 3 + col * 3;
                    v2.x = posArr[i2];
                    v2.y = posArr[i2 + 1];
                    v2.z = posArr[i2 + 2];
                    v2 = v2.sub(v0);

                    int i3 = (row) * width * 3 + (col + 1) * 3;
                    v3.x = posArr[i3];
                    v3.y = posArr[i3 + 1];
                    v3.z = posArr[i3 + 2];
                    v3 = v3.sub(v0);

                    int i4 = (row - 1) * width * 3 + col * 3;
                    v4.x = posArr[i4];
                    v4.y = posArr[i4 + 1];
                    v4.z = posArr[i4 + 2];
                    v4 = v4.sub(v0);

                    v1.cross(v2, v12);
                    v12.normalize();

                    v2.cross(v3, v23);
                    v23.normalize();

                    v3.cross(v4, v34);
                    v34.normalize();

                    v4.cross(v1, v41);
                    v41.normalize();

                    normal = v12.add(v23).add(v34).add(v41);
                    normal.normalize();
                } else {
                    normal.x = 0;
                    normal.y = 1;
                    normal.z = 0;
                }

                normal.normalize();

                normals.add(normal.x);
                normals.add(normal.y);
                normals.add(normal.z);
            }
        }

        return listToArray(normals);
    }

    private float getHeight(int x, int z, int width, ByteBuffer buffer) {
        int position = x * 4 + z * 4 * width;

        byte r = buffer.get(position);
        byte g = buffer.get(position + 1);
        byte b = buffer.get(position + 2);
        byte a = buffer.get(position + 3);

        int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);

        return minY + Math.abs(maxY - minY) * (argb / (float) MAX_COLOUR);
    }

}
