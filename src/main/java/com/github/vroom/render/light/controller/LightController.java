package com.github.vroom.render.light.controller;

import com.github.vroom.Vroom;
import com.github.vroom.render.light.LightManager;

public interface LightController {

    void init(Vroom vroom, LightManager lightManager);

    void update(Vroom vroom, LightManager lightManager);

}
