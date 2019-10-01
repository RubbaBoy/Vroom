package com.github.vroom.render.camera.modifiers;

import com.github.vroom.Vroom;
import com.github.vroom.render.camera.Camera;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public final class CameraDefaultMovementModifier implements CameraModifier {

    private static final float CAMERA_POS_STEP = 0.2f;

    private final Vector3f cameraInc;

    public CameraDefaultMovementModifier(Vroom vroom) {
        this.cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void apply(Vroom vroom, Camera camera) {
        cameraInc.set(0, 0, 0);

        var keyboardInputManager = vroom.getKeyboardInputManager();

        if (keyboardInputManager.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (keyboardInputManager.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }

        if (keyboardInputManager.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (keyboardInputManager.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }

        if (keyboardInputManager.isKeyPressed(GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        } else if (keyboardInputManager.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            cameraInc.y = -1;
        }

        if (cameraInc.x != 0 || cameraInc.y != 0 || cameraInc.z != 0) {
            camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
                    cameraInc.z * CAMERA_POS_STEP);
        }
    }
}
