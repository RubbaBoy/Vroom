package com.github.vroom.render.mesh;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.object.Collision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class MultiMesh {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiMesh.class);

    private final String identifier;

    private final Mesh[] meshes;

    private Collision[][] bounds;

    public MultiMesh(Mesh... meshes) {
        this(null, meshes);
    }

    public MultiMesh(String identifier, Mesh... meshes) {
        if (identifier == null) {
            identifier = "MM-" + hashCode();
        }

        this.meshes = meshes;
        this.identifier = identifier;
        this.bounds = Arrays.stream(meshes).map(Mesh::getBounds).toArray(Collision[][]::new);
    }

    public Mesh[] getMeshes() {
        return meshes;
    }

    public void createMeshes() {
        for (var mesh : meshes) {
            mesh.createMesh();
        }
    }

    public void setMaterial(Material material) {
        for (var mesh : meshes) {
            mesh.setMaterial(material);
        }
    }

    public void setBounds(Collision[][] bounds) {
        if (bounds.length != meshes.length) {
            LOGGER.error("AABB[][] length ({}) does not match the amount of meshes present ({}) in {}", bounds.length,
                    meshes.length, identifier);
            return;
        }

        this.bounds = bounds;

        for (int i = 0; i < meshes.length; i++) {
            meshes[i].setBounds(bounds[i]);
        }
    }

    private String getIdentifier() {
        return identifier;
    }

    public Collision[][] getBounds() {
        return bounds;
    }

    public Collision[][] getCopiedBounds() {
        return getCopiedBounds(1);
    }

    public Collision[][] getCopiedBounds(float scale) {
        return Arrays.stream(meshes).map(mesh -> mesh.getCopiedBounds(scale)).toArray(Collision[][]::new);
    }

    public void createTextures() {
        for (var mesh : meshes) {
            mesh.getMaterial().getTexture().createTexture();
        }
    }

    @Override
    public String toString() {
        return getIdentifier();
    }
}
