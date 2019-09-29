package com.github.vroom.demo;

import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.TexturedMesh;

public enum MeshFile implements FiledMesh, TexturedMesh {

    CUBE("/models/cube.obj", "textures/grassblock.png");

    private final String relativePath;

    private final String texturePath;

    MeshFile(String relativePath, String texturePath) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public String getTexturePath() {
        return texturePath;
    }
}
