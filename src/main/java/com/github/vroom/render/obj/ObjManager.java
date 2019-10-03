package com.github.vroom.render.obj;

import com.github.vroom.render.Texture;
import com.github.vroom.render.mesh.AABBMesh;
import com.github.vroom.render.mesh.FiledMesh;
import com.github.vroom.render.mesh.MultiMesh;
import com.github.vroom.render.mesh.TexturedMesh;
import com.github.vroom.render.mesh.assimp.StaticMeshesLoader;
import com.github.vroom.utility.ClasspathUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public final class ObjManager<E extends Enum<E> & FiledMesh & TexturedMesh> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjManager.class);

    private final List<Callable<MultiMesh>> processingCallables;

    private final ConcurrentMap<E, MultiMesh> meshMap;

    public ObjManager() {
        this.meshMap = new ConcurrentHashMap<>();
        this.processingCallables = new ArrayList<>();
    }

    public ObjManager<E> queueObj(E e) {
        processingCallables.add(() -> {
            var objPath = ClasspathUtility.getAbsolutePath(e.getRelativePath());
            var texturePath = ClasspathUtility.getAbsolutePath(e.getTexturePath());
            var hasMesh = e instanceof AABBMesh;
            var meshes = StaticMeshesLoader.load(objPath, texturePath, hasMesh && ((AABBMesh) e).autoComputeAABB());

            if (hasMesh && !((AABBMesh) e).autoComputeAABB()) {
                var aabbs = ((AABBMesh) e).getAABBs();

                if (aabbs.length != meshes.length) {
                    LOGGER.error("AABB[][] length ({}) does not match the amount of meshes present ({}) in {}",
                            aabbs.length, meshes.length, e.getRelativePath());
                    return null; // Should the Callable type be Optional<MultiMesh>?
                }

                for (int i = 0; i < aabbs.length; i++) {
                    meshes[i].setBounds(aabbs[i]);
                }
            }

            return meshMap.compute(e, ($1, $2) -> new MultiMesh(meshes));
        });

        return this;
    }

    public MultiMesh get(E e) {
        return meshMap.get(e);
    }

    public void waitForObjects() {
        ForkJoinPool.commonPool().invokeAll(processingCallables);
        processingCallables.clear();
    }

    public void createMeshes() {
        meshMap.forEach((e, multiMesh) -> {
            if (multiMesh != null) {
                var meshes = multiMesh.getMeshes();

                if (meshes.length == 1 && meshes[0].getMaterial().getTexture() == null) {
                    multiMesh.setMaterial(e.getMaterial()
                            .setTexture(new Texture(ClasspathUtility.getAbsolutePath(e.getTexturePath()))));
                }

                multiMesh.createTextures();
                multiMesh.createMeshes();
            }
        });
    }

    public void cleanup() {
        meshMap.clear();
    }
}
