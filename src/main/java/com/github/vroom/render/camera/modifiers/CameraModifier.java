package com.github.vroom.render.camera.modifiers;

import com.github.vroom.Vroom;
import com.github.vroom.render.camera.Camera;

public interface CameraModifier {

    void apply(Vroom vroom, Camera camera);

}
