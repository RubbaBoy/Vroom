package com.github.vroom.demo;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.TexturedMesh;

public enum MeshFile implements FiledMesh, TexturedMesh {

    CUBE("/models/cube.obj", "textures/grassblock.png", new Material(1.0F));

    private final String relativePath;

    private final String texturePath;
    private Material material;

    MeshFile(String relativePath, String texturePath, Material material) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
        this.material = material;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public String getTexturePath() {
        return texturePath;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

}
