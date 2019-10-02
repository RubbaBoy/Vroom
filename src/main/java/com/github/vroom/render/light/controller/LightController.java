package com.github.vroom.render.light.controller;

import com.github.vroom.render.light.LightManager;

public interface LightController {

    void init(LightManager lightManager);

    void update(LightManager lightManager);

}
