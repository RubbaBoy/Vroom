package com.github.vroom.render.light.controller;

import com.github.vroom.Vroom;
import com.github.vroom.render.light.DirectionalLight;
import com.github.vroom.render.light.LightManager;
import org.joml.Vector3f;

import java.time.Duration;

public class DayNightController implements LightController {

    private final float increment;

    private float lightAngle;

    private DirectionalLight directionalLight;

    /**
     * Instantiates a new {@link DayNightController}.
     *
     * @param dayNightCycle The {@link Duration} that represents the length of a whole day-night cycle.
     */
    public DayNightController(Duration dayNightCycle) {
        this.increment = 360f / dayNightCycle.getSeconds() / Vroom.TARGET_FPS;
    }

    @Override
    public void init(Vroom vroom, LightManager lightManager) {
        var lightColor = new Vector3f(1, 1, 1);
        var lightPosition = new Vector3f(0, 0, 0);

        directionalLight = new DirectionalLight(lightColor, lightPosition, 1f);

        lightManager.setDirectionalLight(directionalLight);
    }

    @Override
    public void update(Vroom vroom, LightManager lightManager) {
        lightAngle += increment;

        if (lightAngle >= 360f) {
            lightAngle = 0f;
        }

        float factor;

        if (lightAngle <= 90 || lightAngle >= 270) {
            factor = (float) Math.sin(Math.toRadians(lightAngle + 90));
        } else {
            factor = 0;
        }

        directionalLight.setIntensity(factor);

        double angRad = Math.toRadians(lightAngle);
        var direction = directionalLight.getDirection();
        direction.set((float) Math.sin(angRad), (float) Math.cos(angRad), direction.z());
    }
}
