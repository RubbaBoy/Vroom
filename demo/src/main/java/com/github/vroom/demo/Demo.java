package com.github.vroom.demo;

import com.github.vroom.Vroom;
import com.github.vroom.Window;
import com.github.vroom.input.keyboard.KeyCombo;
import com.github.vroom.input.keyboard.KeyListener;
import com.github.vroom.input.mouse.MouseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Demo {

    private static final Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        var vroom = new Vroom(new Window("Demo", 800, 600, false));

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
