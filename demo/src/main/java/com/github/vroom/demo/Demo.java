package com.github.vroom.demo;

import com.github.vroom.Vroom;
import com.github.vroom.input.keyboard.KeyCombo;
import com.github.vroom.input.keyboard.KeyListener;
import com.github.vroom.input.mouse.MouseListener;
import com.github.vroom.render.Window;
import com.github.vroom.render.light.GlobalLightHandler;
import com.github.vroom.render.light.LightManager;
import com.github.vroom.render.light.PointLight;
import com.github.vroom.render.light.controller.DayNightController;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.obj.ObjManager;
import com.github.vroom.render.object.RenderObject;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var objManager = createObjManager();
        var lightManager = createLightManager();
        var globalLightHandler = createLightHandler();

        var vroom = new Vroom(new Window("Demo", 800, 600, true, false), objManager, lightManager, globalLightHandler);

        renderCubes(objManager, vroom);

        registerKeyboardListener(vroom);
        registerMouseListener(vroom);

        vroom.run();
    }

    private static GlobalLightHandler createLightHandler() {
        var globalLightHandler = new GlobalLightHandler();
        globalLightHandler.setLightController(new DayNightController(Duration.ofMinutes(1L)));
        return globalLightHandler;
    }

    private static LightManager createLightManager() {
        var lightColor = new Vector3f(1, 1, 1);
        var lightPosition = new Vector3f(2, 2, 5);

        float lightIntensity = 2f;

        var pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        var att = new PointLight.Attenuation(0.7f, 0.7f, 0.7f);

        pointLight.setAttenuation(att);

        var lightManager = new LightManager();

        lightManager.setAmbientLight(new Vector3f(0.1f, 0.1f, 0.1f));

        for (int i = 0; i < 53 / 5; i++) {
            var clone = new PointLight(pointLight);
            clone.setPosition(new Vector3f(lightPosition));
            lightManager.addLight(clone);
            lightPosition.z += 5;
        }

        return lightManager;
    }

    private static ObjManager<MeshFile> createObjManager() {
        var objManager = new ObjManager<MeshFile>();
        objManager.queueObj(MeshFile.CUBE).waitForObjects();
        return objManager;
    }

    private static void renderCubes(ObjManager<MeshFile> objManager, Vroom vroom) {
        Mesh mesh = objManager.get(MeshFile.CUBE);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 53; y++) {
                var renderObject = new RenderObject(mesh);
                renderObject.setScale(0.5F);
                renderObject.setPosition(x, 0, y);
                renderObject.setRotation(0, 0, 0);
                vroom.addRenderObject(renderObject);
            }
        }
    }

    private static void registerKeyboardListener(Vroom vroom) {
        vroom.getKeyboardInputManager().addListener(new KeyCombo(GLFW_KEY_A), new KeyListener() {
            @Override
            public void keyPressed() {
                LOGGER.info("'A' key pressed!");
            }

            @Override
            public void keyReleased() {
                LOGGER.info("'A' key released!");
            }

            @Override
            public void keyRepeated() {
                LOGGER.info("'A' key repeated!");
            }
        });
    }

    private static void registerMouseListener(Vroom vroom) {
        vroom.getMouseInputMethod().addListener(GLFW_MOUSE_BUTTON_LEFT, new MouseListener() {
            @Override
            public void mousePressed() {
                LOGGER.info("Left button pressed!");
            }

            @Override
            public void mouseReleased() {
                LOGGER.info("Left button released!");
            }
        });
    }

}
