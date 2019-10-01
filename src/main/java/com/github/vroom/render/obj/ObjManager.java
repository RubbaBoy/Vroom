package com.github.vroom.render.obj;

import com.github.vroom.render.Texture;
import com.github.vroom.render.mesh.AABBMesh;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.mesh.TexturedMesh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public final class ObjManager<E extends Enum<E> & FiledMesh & TexturedMesh> {

    private final List<Callable<Mesh>> processingCallables;

    private final ConcurrentMap<E, Mesh> meshMap;

    public ObjManager() {
        this.meshMap = new ConcurrentHashMap<>();
        this.processingCallables = new ArrayList<>();
    }

    public ObjManager<E> queueObj(E e) {
        processingCallables.add(() -> {
            var mesh = ObjLoader.loadMesh(e.getRelativePath());
            if (e instanceof AABBMesh) {
                mesh.setBounds(((AABBMesh) e).getAABBs());
            }
            meshMap.put(e, mesh);
            return mesh;
        });

        return this;
    }

    public Mesh get(E e) {
        return meshMap.get(e);
    }

    public void waitForObjects() {
        ForkJoinPool.commonPool().invokeAll(processingCallables);
        processingCallables.clear();
    }

    public void createMeshes() {
        meshMap.forEach((e, mesh) -> {
            mesh.createMesh();
            mesh.setMaterial(e.getMaterial().setTexture(new Texture(e.getTexturePath())));
        });
    }

    public void cleanup() {
        meshMap.clear();
    }
}
