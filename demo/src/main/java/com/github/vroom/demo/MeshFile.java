package com.github.vroom.demo;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.CollisionMesh;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.TexturedMesh;
import com.github.vroom.render.object.Collision;

public enum MeshFile implements FiledMesh, TexturedMesh, CollisionMesh {

    CUBE("/models/cube.obj", "/textures/grassblock.png", new Material(0.5F), true),
    PLAYER("/models/player.obj", "/textures", new Material(0.6F), true);

    private final String relativePath;

    private final String texturePath;

    private final Material material;

    private final Collision[][] collisions;

    private boolean autoComputeCollision;

    MeshFile(String relativePath, String texturePath, Material material, boolean autoComputeCollision) {
        this(relativePath, texturePath, material, autoComputeCollision, new Collision[0]);
    }

    MeshFile(String relativePath, String texturePath, Material material, Collision[]... collisions) {
        this(relativePath, texturePath, material, false, collisions);
    }

    MeshFile(String relativePath, String texturePath, Material material, boolean autoComputeCollision,
             Collision[]... collisions) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
        this.material = material;
        this.autoComputeCollision = autoComputeCollision;
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
    public Collision[][] getCollision() {
        return collisions;
    }

    @Override
    public boolean autoComputeCollision() {
        return autoComputeCollision;
    }

}
