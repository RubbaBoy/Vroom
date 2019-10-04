package com.github.vroom.demo;

import com.github.vroom.Vroom;
import com.github.vroom.input.keyboard.KeyCombo;
import com.github.vroom.input.keyboard.KeyListener;
import com.github.vroom.input.mouse.MouseListener;
import com.github.vroom.player.Player;
import com.github.vroom.render.Window;
import com.github.vroom.render.light.GlobalLightHandler;
import com.github.vroom.render.light.LightManager;
import com.github.vroom.render.light.controller.DayNightController;
import com.github.vroom.render.light.point.Attenuation;
import com.github.vroom.render.light.point.PointLight;
import com.github.vroom.render.mesh.MultiMesh;
import com.github.vroom.render.obj.ObjManager;
import com.github.vroom.render.object.RenderObject;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public final class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var objManager = createObjManager();
        var lightManager = createLightManager();
        var globalLightHandler = createLightHandler(lightManager);

        var vroom = new Vroom(new Window("Demo", 800, 600, true, false), objManager,
                lightManager, globalLightHandler);

        renderCubes(objManager, vroom);
        createPlayer(vroom, objManager);

        registerKeyboardListener(vroom);
        registerMouseListener(vroom);

        vroom.run();
    }

    private static Player createPlayer(Vroom vroom, ObjManager<MeshFile> objManager) {
        MultiMesh mesh = objManager.get(MeshFile.PLAYER);
        var player = new Player(vroom, mesh); // objManager.get(MeshFile.PLAYER)
        vroom.addRenderObject(player.getBodyRender());
        return player;
    }

    private static GlobalLightHandler createLightHandler(LightManager lightManager) {
        return new GlobalLightHandler(lightManager, new DayNightController(Duration.ofMinutes(1L)));
    }

    private static LightManager createLightManager() {
        var lightColor = new Vector3f(1, 1, 1);
        var lightPosition = new Vector3f(2, 2, 5);

        float lightIntensity = 2f;

        var pointLight = new PointLight(lightColor, lightPosition, lightIntensity);
        pointLight.setAttenuation(new Attenuation(0.7f, 0.7f, 0.7f));

        var lightManager = new LightManager();
        lightManager.getAmbientLight().set(0.1f);

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
        objManager.queueObj(MeshFile.CUBE)
                .queueObj(MeshFile.PLAYER)
                .waitForObjects();
        return objManager;
    }

    private static void renderCubes(ObjManager<MeshFile> objManager, Vroom vroom) {
        MultiMesh mesh = objManager.get(MeshFile.CUBE);

        LOGGER.info("MESH = {}", mesh);

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
        var renderer = vroom.getRenderer();
        vroom.getKeyboardInputManager().addListener(new KeyCombo(GLFW_KEY_X), new KeyListener() {
            @Override
            public void keyPressed() {
                renderer.setWireframe(!renderer.isWireframe());
            }

            @Override
            public void keyReleased() {

            }

            @Override
            public void keyRepeated() {

            }
        });
    }

    private static void registerMouseListener(Vroom vroom) {
        vroom.getMouseInputMethod().addListener(GLFW_MOUSE_BUTTON_LEFT, new MouseListener() {
            @Override
            public void mousePressed() {

            }

            @Override
            public void mouseReleased() {

            }
        });
    }

}
