package com.github.vroom.input.keyboard;

import com.github.vroom.Vroom;
import com.github.vroom.render.Window;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public final class KeyboardInputManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardInputManager.class);

    private final Map<KeyCombo, List<KeyListener>> listenerMap;

    private GLFWKeyCallback keyCallback;
    private Vroom vroom;

    public KeyboardInputManager(Vroom vroom) {
        this.vroom = vroom;
        this.listenerMap = new HashMap<>();
    }

    public void addListener(int key, int modifiers, KeyListener keyListener) {
        addListener(new KeyCombo(key, modifiers), keyListener);
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
        if (keyCallback != null) keyCallback.free();
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(vroom.getWindow().getHandle(), key) == GLFW_PRESS;
    }
}
