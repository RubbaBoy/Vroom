package com.github.vroom.demo.tutorial;

import com.github.vroom.graph.Material;
import com.github.vroom.graph.collision.Collision;
import com.github.vroom.graph.collision.aabb.AABBGenerator;
import com.github.vroom.graph.mesh.CollisionMesh;
import com.github.vroom.graph.mesh.FiledMesh;
import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.graph.mesh.TexturedMesh;

import java.util.function.Function;

public enum MeshFile implements FiledMesh, TexturedMesh, CollisionMesh {

    CUBE("/models/cube.obj", "/textures/grassblock.png", new Material(0.1F),
            AABBGenerator.GENERATE_COLLISIONS),
    PLAYER("/models/player.obj", "/textures", new Material(0.6F),
            AABBGenerator.GENERATE_COLLISIONS);

    private final String relativePath;

    private final String texturePath;

    private final Material material;

    private final Function<Mesh, Collision[]> collisionComputation;

    private final Collision[][] collisions;

    MeshFile(String relativePath, String texturePath, Material material, Function<Mesh, Collision[]> collisionComputation) {
        this(relativePath, texturePath, material, collisionComputation, new Collision[0]);
    }

    MeshFile(String relativePath, String texturePath, Material material, Collision[]... collisions) {
        this(relativePath, texturePath, material, null, collisions);
    }

    MeshFile(String relativePath, String texturePath, Material material, Function<Mesh, Collision[]> collisionComputation,
             Collision[]... collisions) {
        this.relativePath = relativePath;
        this.texturePath = texturePath;
        this.material = material;
        this.collisionComputation = collisionComputation;
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
    public Function<Mesh, Collision[]> getCollisionComputation() {
        return collisionComputation;
    }

}
