package com.github.vroom.render.light.controller;

import com.github.vroom.Vroom;
import com.github.vroom.render.light.DirectionalLight;
import com.github.vroom.render.light.LightManager;
import org.joml.Vector3f;

public class DayNightController implements LightController {

    private float lightAngle;

    private DirectionalLight directionalLight;

    @Override
    public void init(Vroom vroom, LightManager lightManager) {
        var lightPosition = new Vector3f(0, 0, 0);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, 1f);
        lightManager.setDirectionalLight(directionalLight);
    }

    @Override
    public void update(Vroom vroom, LightManager lightManager) {
        lightAngle += 0.0002f;

        float factor;

        if (lightAngle >= -90 && lightAngle <= 90) {
            factor = (float) Math.sin(Math.toRadians(lightAngle + 90));
        } else {
            factor = 0;

            if (lightAngle >= 135) {
                lightAngle = -135;
            }
        }

        directionalLight.setIntensity(factor);

        double angRad = Math.toRadians(lightAngle);

        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }
}
