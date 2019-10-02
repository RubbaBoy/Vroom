package com.github.vroom.render.mesh;

import com.github.vroom.render.light.Material;
import com.github.vroom.render.object.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class MultiMesh {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiMesh.class);

    private final Mesh[] meshes;

    private String identifier;

    private AABB[][] bounds;

    public MultiMesh(Mesh[] meshes) {
        this(meshes, null);
    }

    public MultiMesh(Mesh[] meshes, String identifier) {
        if (identifier == null) {
            identifier = "MM-" + hashCode();
        }

        this.meshes = meshes;
        this.identifier = identifier;
        this.bounds = Arrays.stream(meshes).map(Mesh::getBounds).toArray(AABB[][]::new);
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

    public void setBounds(AABB[][] bounds) {
        if (bounds.length != meshes.length) {
            LOGGER.error("AABB[][] length ({}) does not match the amount of meshes present ({}) in {}", bounds.length, meshes.length, identifier);
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

    public AABB[][] getBounds() {
        return bounds;
    }

    public AABB[][] getCopiedBounds() {
        var copied = new AABB[meshes.length][];
        for (int i = 0; i < meshes.length; i++) {
            copied[i] = meshes[i].getCopiedBounds();
        }
        return copied;
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
