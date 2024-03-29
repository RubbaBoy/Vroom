package com.github.vroom.graph.mesh;

import com.github.vroom.graph.Texture;
import com.github.vroom.loaders.assimp.StaticMeshesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

public class MeshManager<E extends Enum<E> & FiledMesh & TexturedMesh> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeshManager.class);

    private final Path cacheDirectory;

    private final List<Callable<MultiMesh>> processingCallables;

    private final ConcurrentMap<E, MultiMesh> meshMap;

    public MeshManager(Path cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
        this.meshMap = new ConcurrentHashMap<>();
        this.processingCallables = new ArrayList<>();
    }

    public MeshManager<E> queue(E e) {
        processingCallables.add(() -> {
            try {
                var resourcePath = cacheDirectory.resolve(e.getRelativeUrl()).toString();
                var texturePath = cacheDirectory.resolve(e.getTextureUrl()).toString();

                LOGGER.info("Queueing mesh [resource={}, texture={}]", resourcePath, texturePath);

                MultiMesh multiMesh;

                if (e instanceof CollisionMesh) {
                    var collisionMesh = (CollisionMesh) e;
                    var collisionComputation = collisionMesh.getCollisionComputation();

                    multiMesh = StaticMeshesLoader.load(resourcePath, texturePath, collisionComputation, false);
                } else {
                    multiMesh = StaticMeshesLoader.load(resourcePath, texturePath, false);
                }

                return meshMap.compute(e, (key, value) -> multiMesh);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new IllegalStateException(exception);
            }
        });

        return this;
    }

    public MultiMesh get(E e) {
        return meshMap.get(e);
    }

    public void flush() {
        ForkJoinPool.commonPool().invokeAll(processingCallables);
        processingCallables.clear();
    }

    public void create() {
        meshMap.forEach((e, multiMesh) -> {
            if (multiMesh != null) {
                var meshes = multiMesh.getMeshes();

                if (meshes.length == 1 && meshes[0].getMaterial().getTexture() == null) {
                    multiMesh.setMaterial(e.getMaterial().setTexture(
                            new Texture(cacheDirectory.resolve(e.getTextureUrl()).toString())));
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
