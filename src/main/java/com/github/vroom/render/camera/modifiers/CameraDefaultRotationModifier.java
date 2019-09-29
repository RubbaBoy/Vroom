package com.github.vroom.render.camera.modifiers;

import com.github.vroom.Vroom;
import com.github.vroom.render.camera.Camera;
import org.joml.Vector2f;

public final class CameraDefaultRotationModifier implements CameraModifier {

    private static final float MOUSE_SENSITIVITY = 0.001f;

    @Override
    public void apply(Vroom vroom, Camera camera) {
        var mouseInputMethod = vroom.getMouseInputMethod();

        if (mouseInputMethod.isRightPressed()) {
            Vector2f rotVec = mouseInputMethod.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }
}
