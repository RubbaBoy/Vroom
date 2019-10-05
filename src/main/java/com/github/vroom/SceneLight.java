package com.github.vroom;

import com.github.vroom.graph.light.PointLight;
import com.github.vroom.graph.light.SpotLight;
import com.github.vroom.graph.light.direction.DirectionalLight;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneLight {

    private Vector3f ambientLight;

    private Vector3f skyBoxLight;

    private List<PointLight> pointLightList = new ArrayList<>();

    private List<SpotLight> spotLightList = new ArrayList<>();

    private DirectionalLight directionalLight;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public List<PointLight> getPointLights() {
        return pointLightList;
    }

    public List<SpotLight> getSpotLights() {
        return spotLightList;
    }

    public void addLight(PointLight pointLight) {
        pointLightList.add(pointLight);
    }

    public void addLight(SpotLight spotLight) {
        spotLightList.add(spotLight);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public Vector3f getSkyBoxLight() {
        return skyBoxLight;
    }

    public void setSkyBoxLight(Vector3f skyBoxLight) {
        this.skyBoxLight = skyBoxLight;
    }
}