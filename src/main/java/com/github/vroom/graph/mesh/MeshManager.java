package com.github.vroom.graph.mesh;

import com.github.vroom.graph.Texture;
import com.github.vroom.loaders.assimp.StaticMeshesLoader;
import com.github.vroom.utility.ClasspathUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public class MeshManager<E extends Enum<E> & FiledMesh & TexturedMesh> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeshManager.class);

    private final List<Callable<MultiMesh>> processingCallables;

    private final ConcurrentMap<E, MultiMesh> meshMap;

    public MeshManager() {
        this.meshMap = new ConcurrentHashMap<>();
        this.processingCallables = new ArrayList<>();
    }

    public MeshManager<E> queueObj(E e) {
        processingCallables.add(() -> {
            var objPath = ClasspathUtility.getAbsolutePath(e.getRelativePath());
            var texturePath = ClasspathUtility.getAbsolutePath(e.getTexturePath());

            MultiMesh multiMesh;

            if (e instanceof CollisionMesh) {
                var collisionMesh = (CollisionMesh) e;
                var collisionComputation = collisionMesh.getCollisionComputation();

                multiMesh = StaticMeshesLoader.load(objPath, texturePath, collisionComputation, false);

//                if (collisionComputation == null) {
//                    multiMesh.setBounds(collisionMesh.getCollision());
//                }
            } else {
                multiMesh = StaticMeshesLoader.load(objPath, texturePath, false);
            }

            return meshMap.compute(e, (key, value) -> multiMesh);
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
