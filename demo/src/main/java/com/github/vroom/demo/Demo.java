package com.github.vroom.demo;

import com.github.vroom.Vroom;
import com.github.vroom.input.keyboard.KeyCombo;
import com.github.vroom.input.keyboard.KeyListener;
import com.github.vroom.input.mouse.MouseListener;
import com.github.vroom.render.Window;
import com.github.vroom.render.mesh.Mesh;
import com.github.vroom.render.obj.ObjManager;
import com.github.vroom.render.object.RenderObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var objManager = new ObjManager<MeshFile>();
        objManager.queueObj(MeshFile.CUBE).waitForObjects();

        var vroom = new Vroom(new Window("Demo", 800, 600, false, false), objManager);

        Mesh mesh = objManager.get(MeshFile.CUBE);

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 53; y++) {
                var renderObject = new RenderObject(mesh);
                renderObject.setScale(0.5F);
                renderObject.setPosition(x, x % 2 == 0 ? -1 : 0, y);
                renderObject.setRotation(0, 0, 0);
                vroom.addRenderObject(renderObject);
            }
        }

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

        vroom.run();
    }

}
