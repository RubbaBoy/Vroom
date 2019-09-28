package com.github.vroom.input.keyboard;

import com.github.vroom.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class KeyboardInputManager {

    private final Map<KeyCombo, List<KeyListener>> listenerMap;

    private GLFWKeyCallback keyCallback;

    public KeyboardInputManager() {
        this.listenerMap = new HashMap<>();
    }

    public void addListener(KeyCombo keyCombo, KeyListener keyListener) {
        listenerMap.computeIfAbsent(keyCombo, $ -> new ArrayList<>()).add(keyListener);
    }

    public void init(Window window) {
        glfwSetKeyCallback(window.getHandle(), keyCallback = GLFWKeyCallback.create((windowId, key, scancode, action, mods) -> {
            var listeners = listenerMap.get(new KeyCombo(key, mods));

            if (listeners == null) {
                return;
            }

            switch (action) {
                case GLFW_PRESS:
                    listeners.forEach(KeyListener::keyPressed);
                    break;
                case GLFW_RELEASE:
                    listeners.forEach(KeyListener::keyReleased);
                    break;
                case GLFW_REPEAT:
                    listeners.forEach(KeyListener::keyRepeated);
                    break;
            }
        }));
    }

    public void cleanup() {
        keyCallback.free();
    }
}
