package com.github.vroom.render.light;

import com.github.vroom.render.light.point.PointLight;
import com.github.vroom.render.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public final class LightManager {

    // Match up with "MAX_POINT_LIGHTS" in fragment.fs
    public static final int MAX_POINT_LIGHTS = 50;

    // Match up with "MAX_SPOT_LIGHTS" in fragment.fs
    public static final int MAX_SPOT_LIGHTS = 50;

    private final List<PointLight> pointLights;

    private final List<SpotLight> spotLights;

    private float specularPower;

    private Vector3f ambientLight;

    private DirectionalLight directionalLight;

    public LightManager() {
        this.pointLights = new ArrayList<>();
        this.spotLights = new ArrayList<>();
        this.specularPower = 10f;
        this.ambientLight = new Vector3f(0, 0, 0);
    }

    public void addLight(PointLight pointLight) {
        if (pointLights.size() >= MAX_POINT_LIGHTS) {
            throw new IndexOutOfBoundsException("You cannot have more than " + MAX_POINT_LIGHTS + " point lights!");
        }

        pointLights.add(pointLight);
    }

    public void addLight(SpotLight spotLight) {
        if (pointLights.size() >= MAX_SPOT_LIGHTS) {
            throw new IndexOutOfBoundsException("You cannot have more than " + MAX_SPOT_LIGHTS + " spot lights!");
        }

        spotLights.add(spotLight);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public void setAmbientLight(Vector3f vector3f) {
        ambientLight = vector3f;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }

    public void render(ShaderProgram shaderProgram, Matrix4f viewMatrix) {
        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", 10f);

        for (int i = 0; i < pointLights.size(); i++) {
            var currPointLight = new PointLight(pointLights.get(i));

            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);

            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            shaderProgram.setUniform("pointLights", currPointLight, i);
        }

        for (int i = 0; i < spotLights.size(); i++) {
            var currSpotLight = new SpotLight(spotLights.get(i));

            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);

            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            shaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        if (directionalLight == null) {
            return;
        }

        DirectionalLight currDirLight = new DirectionalLight(directionalLight);
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        shaderProgram.setUniform("directionalLight", currDirLight);
    }
}
