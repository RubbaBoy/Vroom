package com.github.vroom.demo;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.AABBMesh;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.TexturedMesh;
import com.github.vroom.render.object.Collision;

public enum MeshFile implements FiledMesh, TexturedMesh, AABBMesh {

    CUBE("/models/cube.obj", "/textures/grassblock.png", new Material(0.5F), true),
    PLAYER("/models/player.obj", "/textures", new Material(0.6F), true);

    private final String relativePath;

    private final String texturePath;

    private final Material material;

    private final Collision[][] collisions;

    private boolean autoComputeAABB;

    MeshFile(String relativePath, String texturePath, Material material, boolean autoComputeAABB) {
        this(relativePath, texturePath, material, autoComputeAABB, new Collision[0]);
    }

    MeshFile(String relativePath, String texturePath, Material material, Collision[]... collisions) {
        this(relativePath, texturePath, material, false, collisions);
    }

    MeshFile(String relativePath, String texturePath, Material material, boolean autoComputeAABB, Collision[]... collisions) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
        this.material = material;
        this.autoComputeAABB = autoComputeAABB;
        this.collisions = collisions;
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
    public Collision[][] getAABBs() {
        return collisions;
    }

    @Override
    public boolean autoComputeAABB() {
        return autoComputeAABB;
    }

}
