package com.github.vroom.render.obj;

import com.github.vroom.render.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ObjManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjManager.class);

    private final Map<String, Mesh> objFiles;
    private final ExecutorService executorService;
    private final List<CompletableFuture<Optional<Mesh>>> processingFutures;

    public ObjManager() {
        this.objFiles = Collections.synchronizedMap(new HashMap<>());
        this.executorService = Executors.newCachedThreadPool();
        this.processingFutures = new ArrayList<>();
    }

    public ObjManager queueObj(String fileName) {
        processingFutures.add(CompletableFuture.supplyAsync(() -> {
            try {
                var mesh = OBJLoader.loadMesh(fileName);
                objFiles.put(fileName, mesh);
                return Optional.of(mesh);
            } catch (IOException e) {
                LOGGER.error("Error loading object file!", e);
                return Optional.empty();
            }
        }, executorService));
        return this;
    }

    public Optional<Mesh> getObj(String fileName) {
        return Optional.ofNullable(objFiles.get(fileName));
    }

    public Mesh getOrThrowObj(String fileName) {
        return Optional.ofNullable(objFiles.get(fileName)).orElseThrow(() -> new RuntimeException("Model \"" + fileName + "\" not found or loaded!"));
    }

    public void waitForObjects() {
        processingFutures.stream()
                .map(CompletableFuture::join)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(Mesh::createMesh);
        processingFutures.clear();
    }

    public void cleanup() {
        executorService.shutdownNow();
    }

}
