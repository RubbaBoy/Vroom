package com.github.vroom.demo;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.AABBMesh;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.TexturedMesh;
import com.github.vroom.render.object.AABB;
import com.github.vroom.render.object.AABBBox;

public enum MeshFile implements FiledMesh, TexturedMesh, AABBMesh {

    CUBE("/models/cube.obj", "textures/grassblock.png", new Material(0.5F),
            new AABB[]{AABBBox.fromRelative(0, 0, 0, 1, 1, 1)}),

    PLAYER("/models/player.obj", "/textures", new Material(0.6F),
            new AABB[]{AABBBox.fromRelative(0, 8, 0, 0, 0, 0, 4, 4, 4)},
            new AABB[]{AABBBox.fromRelative(0, 0, 0, 0, 0, 0, 3, 3, 8)});

    private final String relativePath;

    private final String texturePath;

    private final Material material;

    private final AABB[][] aabbs;

    MeshFile(String relativePath, String texturePath, Material material, AABB[]... aabbs) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
        this.material = material;
        this.aabbs = aabbs;
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

    @Override
    public AABB[][] getAABBs() {
        return aabbs;
    }
}
