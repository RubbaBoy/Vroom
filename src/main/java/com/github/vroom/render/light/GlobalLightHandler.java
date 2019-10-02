package com.github.vroom.render.light;

import com.github.vroom.render.light.controller.LightController;

public final class GlobalLightHandler {

    private final LightManager lightManager;

    private final LightController lightController;

    public GlobalLightHandler(LightManager lightManager, LightController lightController) {
        this.lightManager = lightManager;
        this.lightController = lightController;
    }

    public void init() {
        lightController.init(lightManager);
    }

    public void update() {
        lightController.update(lightManager);
    }
}
