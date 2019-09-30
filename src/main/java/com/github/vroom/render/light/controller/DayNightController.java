package com.github.vroom.render.light.controller;

import com.github.vroom.Vroom;
import com.github.vroom.render.light.DirectionalLight;
import com.github.vroom.render.light.LightManager;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DayNightController implements LightController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DayNightController.class);

    private final float durationOfDay = 5_000; // In mills

    private final float maxTimeOfDay = 60_000;

    private float currentTimeOfDay = 30_000; // In updates

    private DirectionalLight directionalLight;

    @Override
    public void init(Vroom vroom, LightManager lightManager) {
        var lightPosition = new Vector3f(0, 0, 0);
        directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, 1f);
        lightManager.setDirectionalLight(directionalLight);
    }

    private int multiply = 1;

    @Override
    public void update(Vroom vroom, LightManager lightManager) {
        var iterationsPerSecond = 1f / Vroom.TARGET_UPS;
        var updatesPerS = maxTimeOfDay / durationOfDay; // Amount of updates required to cycle day
        currentTimeOfDay += (updatesPerS * iterationsPerSecond) * multiply;

        if (currentTimeOfDay <= 0 || currentTimeOfDay >= maxTimeOfDay) {
//            multiply *= -1;
            currentTimeOfDay = 0;
        }

        updateLight();
    }

    private void updateLight() {
        var lightAngle = (currentTimeOfDay / maxTimeOfDay) * 360f;

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
