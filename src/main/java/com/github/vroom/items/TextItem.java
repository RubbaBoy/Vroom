package com.github.vroom.items;

import com.github.vroom.graph.mesh.MultiMesh;
import com.github.vroom.utility.Utils;
import com.github.vroom.graph.FontTexture;
import com.github.vroom.graph.Material;
import com.github.vroom.graph.mesh.Mesh;

import java.util.ArrayList;
import java.util.List;

public class TextItem extends GameItem {

    private static final float ZPOS = 0.0f;

    private static final int VERTICES_PER_QUAD = 4;

    private final FontTexture fontTexture;

    private String text;

    public TextItem(String text, FontTexture fontTexture) {
        super();
        this.text = text;
        this.fontTexture = fontTexture;
        setMultiMesh(buildMesh());
    }

    private MultiMesh buildMesh() {
        List<Float> positions = new ArrayList<>();
        List<Float> textCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] normals = new float[0];

        char[] characters = text.toCharArray();

        int numChars = characters.length;

        float startx = 0;

        for(int i=0; i<numChars; i++) {
            FontTexture.CharInfo charInfo = fontTexture.getCharInfo(characters[i]);

            // Build a character tile composed by two triangles

            // Left Top vertex
            positions.add(startx); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD);

            // Left Bottom vertex
            positions.add(startx); // x
            positions.add((float) fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) charInfo.getStartX() / (float) fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 1);

            // Right Bottom vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add((float) fontTexture.getHeight()); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth()) / (float) fontTexture.getWidth());
            textCoords.add(1.0f);
            indices.add(i * VERTICES_PER_QUAD + 2);

            // Right Top vertex
            positions.add(startx + charInfo.getWidth()); // x
            positions.add(0.0f); //y
            positions.add(ZPOS); //z
            textCoords.add((float) (charInfo.getStartX() + charInfo.getWidth())/ (float) fontTexture.getWidth());
            textCoords.add(0.0f);
            indices.add(i * VERTICES_PER_QUAD + 3);

            // Add indices por left top and bottom right vertices
            indices.add(i * VERTICES_PER_QUAD);
            indices.add(i * VERTICES_PER_QUAD + 2);

            startx += charInfo.getWidth();
        }

        float[] posArr = Utils.listToArray(positions);
        float[] textCoordsArr = Utils.listToArray(textCoords);

        int[] indicesArr = indices.stream().mapToInt(Integer::intValue).toArray();

        Mesh mesh = new Mesh(posArr, textCoordsArr, normals, indicesArr, true);

        mesh.setMaterial(new Material(fontTexture.getTexture()));

        return new MultiMesh(mesh);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        getMultiMesh().deleteBuffers();
        setMultiMesh(buildMesh());
    }
}