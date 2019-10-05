package com.github.vroom.demo.tutorial;

import com.github.vroom.graph.mesh.MeshManager;
import com.github.vroom.input.keyboard.KeyboardInput;
import com.github.vroom.input.mouse.MouseInput;
import com.github.vroom.items.SkyBox;
import org.joml.Vector2f;
import org.joml.Vector3f;
import com.github.vroom.GameLogic;
import com.github.vroom.Scene;
import com.github.vroom.SceneLight;
import com.github.vroom.Window;
import com.github.vroom.graph.Camera;
import com.github.vroom.graph.Renderer;
import com.github.vroom.graph.light.PointLight;
import com.github.vroom.graph.light.direction.DirectionalLight;
import com.github.vroom.graph.weather.Fog;
import com.github.vroom.items.GameItem;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class DummyGame implements GameLogic {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyGame.class);

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private Scene scene;

    private MeshManager<MeshFile> meshManager;

    private static final float CAMERA_POS_STEP = 0.40f;

    private float angleInc;

    private float lightAngle;

    private boolean firstTime;

    private boolean sceneChanged;

    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 90;
        firstTime = true;
    }

    @Override
    public void init(Window window) {
        renderer.init(window);

        scene = new Scene();

        (meshManager = new MeshManager<>())
                .queueObj(MeshFile.CUBE)
                .queueObj(MeshFile.PLAYER)
                .waitForObjects();

        meshManager.createMeshes();

        var cubes = new ArrayList<GameItem>();

        var mesh = meshManager.get(MeshFile.CUBE);
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 53; y++) {
                var renderObject = new GameItem(mesh);
                renderObject.setScale(0.5F);
                renderObject.setPosition(x, 0, y);
                cubes.add(renderObject);
            }
        }

        var player = new GameItem(meshManager.get(MeshFile.PLAYER));
        player.setPosition(0, 1, 0);
        player.setScale(0.1f);
        scene.addGameItem(player);

        scene.setGameItems(cubes.toArray(GameItem[]::new));

        // Shadows
        scene.setRenderShadows(true);

        // Fog
        Vector3f fogColor = new Vector3f(0.5f, 0.5f, 0.5f);
        scene.setFog(new Fog(false, fogColor, 0.02f));

        // Setup SkyBox
        var skyBox = new SkyBox("models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(100.0f);
        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

        camera.setPosition(4, 2, 4);
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(0.5f, 0.5f, 0.5f));

        // Directional Light
        float directionLightIntensity = 0.0f;
        Vector3f lightDirection = new Vector3f(0, 0, 0);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, directionLightIntensity);
        //sceneLight.setDirectionalLight(directionalLight);

//        Vector3f pointLightColor = new Vector3f(0.0f, 1.0f, 0.0f);
//        PointLight.Attenuation attenuation = new PointLight.Attenuation(1, 0.0f, 0);
//        PointLight pointLight = new PointLight(pointLightColor, pasointLightPos, lightIntensity, attenuation);
//        sceneLight.setPointLightList( new PointLight[] {pointLight});

        var lightPosition = new Vector3f(2, 2, 5);

        float pointLightIntensity = 1f;
        var pointLight = new PointLight(new Vector3f(1, 1, 1), lightPosition, pointLightIntensity);
        pointLight.setAttenuation(new PointLight.Attenuation(0.2f, 0.2f, 0.2f));

        for (int i = 0; i < 53 / 5; i++) {
            var clone = new PointLight(pointLight);
            clone.setPosition(new Vector3f(lightPosition));
            sceneLight.addLight(clone);
            lightPosition.z += 5;
        }
    }

    @Override
    public void input(MouseInput mouseInput, KeyboardInput keyboardInput) {
        sceneChanged = false;

        cameraInc.set(0, 0, 0);

        if (keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            sceneChanged = true;
            cameraInc.z = -1;
        } else if (keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            sceneChanged = true;
            cameraInc.z = 1;
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            sceneChanged = true;
            cameraInc.x = -1;
        } else if (keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            sceneChanged = true;
            cameraInc.x = 1;
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            sceneChanged = true;
            cameraInc.y = -1;
        } else if (keyboardInput.isKeyPressed(GLFW_KEY_SPACE)) {
            sceneChanged = true;
            cameraInc.y = 1;
        }

        if (keyboardInput.isKeyPressed(GLFW_KEY_LEFT)) {
            sceneChanged = true;
            angleInc -= 0.05f;
        } else if (keyboardInput.isKeyPressed(GLFW_KEY_RIGHT)) {
            sceneChanged = true;
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            sceneChanged = true;
        }

        // Update camera position
        camera.movePosition(scene, cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        lightAngle += angleInc;

        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }

        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));

        var directionalLight = scene.getSceneLight().getDirectionalLight();
        if (directionalLight != null) {
            Vector3f lightDirection = directionalLight.getDirection();

            lightDirection.set(0, yValue, zValue);
            lightDirection.normalize();
        }

        // Update view matrix
        camera.updateViewMatrix();
    }

    @Override
    public void render(Window window) {
        if (firstTime) {
            sceneChanged = true;
            firstTime = false;
        }

        renderer.render(window, camera, scene, sceneChanged);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }
}
