package com.github.vroom.input.keyboard;

import com.github.vroom.Window;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;

public class KeyboardInput {

    private final Map<KeyCombo, List<KeyboardListener>> listenerMap;

    private long windowHandle;

    private GLFWKeyCallback keyCallback;

    public KeyboardInput() {
        this.listenerMap = new HashMap<>();
    }

    public void init(long windowHandle) {
        glfwSetKeyCallback(this.windowHandle = windowHandle, keyCallback = GLFWKeyCallback.create((windowId, key, scancode, action, mods) -> {
            var listeners = listenerMap.get(new KeyCombo(key, mods));

            if (listeners == null) {
                return;
            }

            switch (action) {
                case GLFW_PRESS:
                    listeners.forEach(KeyboardListener::keyPressed);
                    break;
                case GLFW_RELEASE:
                    listeners.forEach(KeyboardListener::keyReleased);
                    break;
                case GLFW_REPEAT:
                    listeners.forEach(KeyboardListener::keyRepeated);
                    break;
            }
        }));
    }

    public void addListener(int key, int modifiers, KeyboardListener keyboardListener) {
        addListener(new KeyCombo(key, modifiers), keyboardListener);
    }

    public void addListener(KeyCombo keyCombo, KeyboardListener keyboardListener) {
        listenerMap.computeIfAbsent(keyCombo, $ -> new ArrayList<>()).add(keyboardListener);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public void cleanup() {
        if (keyCallback != null) {
            keyCallback.free();
        }
    }
}
