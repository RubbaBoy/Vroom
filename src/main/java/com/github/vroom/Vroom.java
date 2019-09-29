package com.github.vroom;

import com.github.vroom.input.keyboard.KeyboardInputManager;
import com.github.vroom.input.mouse.MouseInputMethod;
import com.github.vroom.render.Mesh;
import com.github.vroom.render.Renderer;
import com.github.vroom.render.Texture;
import com.github.vroom.render.Window;
import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.camera.CameraTransformationManager;
import com.github.vroom.render.camera.modifiers.CameraDefaultMovementModifier;
import com.github.vroom.render.camera.modifiers.CameraDefaultRotationModifier;
import com.github.vroom.render.obj.OBJLoader;
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

    private static final int TARGET_FPS = 60;

    private static final int TARGET_UPS = 30;

    private Vector3f cameraInc = new Vector3f(0, 0, 0);

    private final Window window;

    private final Camera camera;

    private final CameraTransformationManager cameraTransformationManager;

    private final Renderer renderer;

    private final MouseInputMethod mouseInputMethod;

    private final KeyboardInputManager keyboardInputManager;

    private final List<RenderObject> renderObjects;

    public Vroom(Window window) {
        this.window = window;
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

            Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
            mesh.setTexture(new Texture("textures/grassblock.png"));
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 53; y++) {
                    var renderObject = new RenderObject(mesh);
                    renderObject.setScale(0.5F);
                    renderObject.setPosition(x, -1, y);
                    renderObject.setRotation(0, 0, 0);
                    addRenderObject(renderObject);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Exception while creating Renderer!", e);
        }

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
        cameraTransformationManager.update();
        // Update camera position
//        camera.movePosition(cameraInc.x * CAMERA_POS_STEP,
//                cameraInc.y * CAMERA_POS_STEP,
//                cameraInc.z * CAMERA_POS_STEP);
    }

    private void render() {
        if (window.isResized()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        renderer.render(window, camera, renderObjects);
        window.render();
    }

    private void cleanup() {
        renderer.cleanup();
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
}
