package com.github.vroom;

import com.github.vroom.input.keyboard.KeyboardInput;
import com.github.vroom.input.mouse.MouseInput;

public class GameEngine implements Runnable {

    public static final int TARGET_FPS = 60;

    public static final int TARGET_UPS = 60;

    private final Window window;

    private final Timer timer;

    private final GameLogic gameLogic;

    private final MouseInput mouseInput;

    private final KeyboardInput keyboardInput;

    private double lastFps;

    private int fps;

    private String windowTitle;

    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, GameLogic gameLogic) {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, GameLogic gameLogic) {
        this.windowTitle = windowTitle;
        window = new Window(windowTitle, width, height, vSync, opts);
        mouseInput = new MouseInput();
        keyboardInput = new KeyboardInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() {
        window.init();
        timer.init();
        keyboardInput.init(window.getHandle());
        mouseInput.init(window);
        gameLogic.init(window);
        lastFps = timer.getTime();
        fps = 0;
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;

        while (!window.windowShouldClose()) {
            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if (window.isvSync()) {
                sync();
            }
        }
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    private void sync() {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
            }
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(mouseInput, keyboardInput);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput, window);
    }

    protected void render() {
        if (window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1) {
            lastFps = timer.getLastLoopTime();
            window.setWindowTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
        gameLogic.render(window);
        window.update();
    }

}
