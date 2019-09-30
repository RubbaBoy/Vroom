package com.github.vroom;

import com.github.vroom.input.keyboard.KeyboardInputManager;
import com.github.vroom.input.mouse.MouseInputMethod;
import com.github.vroom.render.Renderer;
import com.github.vroom.render.Window;
import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.camera.CameraTransformationManager;
import com.github.vroom.render.camera.modifiers.CameraDefaultMovementModifier;
import com.github.vroom.render.camera.modifiers.CameraDefaultRotationModifier;
import com.github.vroom.render.light.DirectionalLight;
import com.github.vroom.render.light.GlobalLightHandler;
import com.github.vroom.render.light.LightManager;
import com.github.vroom.render.obj.ObjManager;
import com.github.vroom.render.object.RenderObject;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glViewport;

public final class Vroom {

    private static final Logger LOGGER = LoggerFactory.getLogger(Vroom.class);

    public static final int TARGET_FPS = 60;

    public static final int TARGET_UPS = 30;

    private float lightAngle;

    private final Window window;

    private final Camera camera;

    private final Renderer renderer;

    private final ObjManager<?> objManager;

    private final MouseInputMethod mouseInputMethod;

    private final KeyboardInputManager keyboardInputManager;

    private final CameraTransformationManager cameraTransformationManager;

    private final LightManager lightManager;

    private final GlobalLightHandler globalLightHandler;

    private final List<RenderObject> renderObjects;

    public Vroom(Window window, ObjManager<?> objManager, LightManager lightManager, GlobalLightHandler globalLightHandler) {
        this.window = window;
        this.objManager = objManager;
        this.lightManager = lightManager;
        this.globalLightHandler = globalLightHandler.setVroom(this);
        this.camera = new Camera();
        this.cameraTransformationManager = new CameraTransformationManager(this, camera);
        this.renderer = new Renderer();
        this.mouseInputMethod = new MouseInputMethod();
        this.keyboardInputManager = new KeyboardInputManager(this);
        this.renderObjects = new ArrayList<>();
    }

    private void init() {
        window.init();

        cameraTransformationManager.addModifier(new CameraDefaultRotationModifier());
        cameraTransformationManager.addModifier(new CameraDefaultMovementModifier(this));

        try {
            renderer.init();
        } catch (IOException e) {
            LOGGER.error("Exception while creating Renderer!", e);
        }

        objManager.createMeshes();
        globalLightHandler.init();

        keyboardInputManager.init(window);
        mouseInputMethod.init(window);
    }

    public void run() {
        try {
            init();
            gameLoop();
        } finally {
            cleanup();
        }
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
        cameraTransformationManager.update();
        globalLightHandler.update();
    }

    private void render() {
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        renderer.render(window, camera, renderObjects, lightManager);
        window.render();
    }

    private void cleanup() {
        renderer.cleanup();
        keyboardInputManager.cleanup();
        objManager.cleanup();
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

    public void addRenderObject(RenderObject renderObject) {
        renderObjects.add(renderObject);
    }

    public void removeRenderObject(RenderObject renderObject) {
        renderObjects.remove(renderObject);
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

    public LightManager getLightManager() {
        return lightManager;
    }

    public GlobalLightHandler getGlobalLightHandler() {
        return globalLightHandler;
    }
}
