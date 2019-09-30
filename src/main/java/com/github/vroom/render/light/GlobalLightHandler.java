package com.github.vroom.render.light;

import com.github.vroom.Vroom;
import com.github.vroom.render.light.controller.LightController;

public class GlobalLightHandler {

    private Vroom vroom;

    private LightManager lightManager;

    private LightController controller;

    public GlobalLightHandler setVroom(Vroom vroom) {
        this.vroom = vroom;
        lightManager = vroom.getLightManager();
        return this;
    }

    public void setLightController(LightController controller) {
        this.controller = controller;
    }

    public void init() {
        if (controller != null) {
            controller.init(vroom, lightManager);
        }
    }

    public void update() {
        if (controller != null) {
            controller.update(vroom, lightManager);
        }
    }
}
