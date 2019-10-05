package com.github.vroom;

import com.github.vroom.graph.mesh.InstancedMesh;
import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.graph.particles.IParticleEmitter;
import com.github.vroom.graph.weather.Fog;
import com.github.vroom.items.GameItem;
import com.github.vroom.items.SkyBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private final List<GameItem> gameItems;

    private final Map<Mesh, List<GameItem>> meshMap;

    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

    private SkyBox skyBox;

    private SceneLight sceneLight;

    private Fog fog;

    private boolean renderShadows;

    private IParticleEmitter[] particleEmitters;

    public Scene() {
        gameItems = new ArrayList<>();
        meshMap = new HashMap<>();
        instancedMeshMap = new HashMap<>();
        fog = Fog.NOFOG;
        renderShadows = true;
    }

    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getGameInstancedMeshes() {
        return instancedMeshMap;
    }

    public boolean isRenderShadows() {
        return renderShadows;
    }

    /**
     * This sets the game items that will never be removed, such as terrain and sky box and stuff.
     *
     * @param gameItems The game items to add
     */
    public void setGameItems(GameItem[] gameItems) {
        // Create a map of meshes to speed up rendering
        Arrays.stream(gameItems).forEach(this::addGameItem);
    }

    public void addGameItem(GameItem gameItem) {
        gameItems.add(gameItem);
        var meshes = gameItem.getMultiMesh().getMeshes();

        for (Mesh mesh : meshes) {
            boolean instancedMesh = mesh instanceof InstancedMesh;

            List<GameItem> list;

            if (instancedMesh) {
                list = instancedMeshMap.computeIfAbsent((InstancedMesh) mesh, $ -> new ArrayList<>());
            } else {
                list = meshMap.computeIfAbsent(mesh, $ -> new ArrayList<>());
            }

            list.add(gameItem);
        }
    }

    public void removeGameItem(GameItem gameItem) {
        gameItems.remove(gameItem);
        Arrays.stream(gameItem.getMultiMesh().getMeshes()).forEach(mesh -> {
            (mesh instanceof InstancedMesh ? instancedMeshMap : meshMap).get(mesh).remove(gameItem);
            mesh.cleanup();
        });
    }

    public void cleanup() {
        gameItems.forEach(GameItem::cleanup);

        if (particleEmitters != null) {
            for (IParticleEmitter particleEmitter : particleEmitters) {
                particleEmitter.cleanup();
            }
        }
    }

    public List<GameItem> getGameItems() {
        return gameItems;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setRenderShadows(boolean renderShadows) {
        this.renderShadows = renderShadows;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public SceneLight getSceneLight() {
        return sceneLight;
    }

    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }

    /**
     * @return the fog
     */
    public Fog getFog() {
        return fog;
    }

    /**
     * @param fog the fog to set
     */
    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public IParticleEmitter[] getParticleEmitters() {
        return particleEmitters;
    }

    public void setParticleEmitters(IParticleEmitter[] particleEmitters) {
        this.particleEmitters = particleEmitters;
    }

}
