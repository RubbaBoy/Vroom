package com.github.vroom.input.mouse;

import com.github.vroom.Window;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

public class MouseInput {

    private static final Logger LOGGER = LoggerFactory.getLogger(MouseInput.class);

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    private final Map<Integer, List<MouseListener>> listenerMap;

    private boolean inWindow;

    private boolean leftButtonPressed;

    private boolean rightButtonPressed;

    public MouseInput() {
        this.previousPos = new Vector2d(-1, -1);
        this.currentPos = new Vector2d(0, 0);
        this.displVec = new Vector2f();
        this.listenerMap = new HashMap<>();
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.set(xpos, ypos);
        });

        glfwSetCursorEnterCallback(window.getHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });

        glfwSetMouseButtonCallback(window.getHandle(), (windowHandle, button, action, mode) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                leftButtonPressed = action == GLFW_PRESS;
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                rightButtonPressed = action == GLFW_PRESS;
            }

            var mouseListener = listenerMap.get(button);

            if (mouseListener == null) {
                return;
            }

            switch (action) {
                case GLFW_PRESS:
                    mouseListener.forEach(MouseListener::mousePressed);
                    break;
                case GLFW_RELEASE:
                    mouseListener.forEach(MouseListener::mouseReleased);
                    break;
            }
        });
    }

    public void addListener(int button, MouseListener mouseListener) {
        listenerMap.computeIfAbsent(button, $ -> new ArrayList<>(1)).add(mouseListener);
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public void input(Window window) {
        displVec.set(0);

        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;

            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;

            if (rotateX) {
                displVec.y = (float) deltax;
            }

            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }

        previousPos.set(currentPos);
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
