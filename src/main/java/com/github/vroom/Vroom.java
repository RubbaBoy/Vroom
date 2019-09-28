package com.github.vroom;

import com.github.vroom.input.keyboard.KeyboardInputManager;
import com.github.vroom.input.mouse.MouseInputMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL11.glViewport;

public final class Vroom {

    private static final Logger LOGGER = LoggerFactory.getLogger(Vroom.class);

    private static final int TARGET_FPS = 60;

    private static final int TARGET_UPS = 30;

    private final Window window;

    private final MouseInputMethod mouseInputMethod;

    private final KeyboardInputManager keyboardInputManager;

    public Vroom(Window window) {
        this.window = window;
        this.mouseInputMethod = new MouseInputMethod();
        this.keyboardInputManager = new KeyboardInputManager();
    }

    private void init() {
        window.init();
        keyboardInputManager.init(window);
        mouseInputMethod.init(window);
    }

    public void run() {
        init();
        gameLoop();
    }

    private void gameLoop() {
        double secsPerUpdate = 1d / TARGET_UPS;
        long previous = System.currentTimeMillis();
        double steps = 0;

        while (!window.windowShouldClose()) {
            long loopStartTime = System.currentTimeMillis();
            long elapsed = loopStartTime - previous;

            previous = loopStartTime;
            steps += elapsed;

            handleInput();

            while (steps >= secsPerUpdate) {
                update();
                steps -= secsPerUpdate;
            }

            render();

            if (window.vSync()) {
                sync(loopStartTime);
            }
        }
    }

    private void handleInput() {
        mouseInputMethod.input();
    }

    private void update() {

    }

    private void render() {
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        window.render();
    }

    private void cleanup() {
        keyboardInputManager.cleanup();
    }

    private void sync(long loopStartTime) {
        float loopSlot = 1f / TARGET_FPS;
        float endTime = loopSlot + loopStartTime;

        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
                // Do nothing.
            }
        }
    }

    public Window getWindow() {
        return window;
    }

    public KeyboardInputManager getKeyboardInputManager() {
        return keyboardInputManager;
    }

    public MouseInputMethod getMouseInputMethod() {
        return mouseInputMethod;
    }
}
