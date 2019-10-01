package com.github.vroom.terrain;

import com.github.vroom.render.object.RenderObject;

public class Terrain {

    private final RenderObject[] renderObjects;

    public Terrain(int blocksPerRow, float scale, float minY, float maxY, String heightMap, String textureFile, int textInc) {
        renderObjects = new RenderObject[blocksPerRow * blocksPerRow];
        var heightMapMesh = new HeightMapMesh(minY, maxY, heightMap, textureFile, textInc);
        for (int row = 0; row < blocksPerRow; row++) {
            for (int col = 0; col < blocksPerRow; col++) {
                float xDisplacement = (col - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getXLength();
                float zDisplacement = (row - ((float) blocksPerRow - 1) / (float) 2) * scale * HeightMapMesh.getZLength();

                var terrainBlock = new RenderObject(heightMapMesh.getMesh());
                terrainBlock.setScale(scale);
                terrainBlock.setPosition(xDisplacement, 0, zDisplacement);
                renderObjects[row * blocksPerRow + col] = terrainBlock;
            }
        }
    }

    public RenderObject[] getRenderObjects() {
        return renderObjects;
    }
}
