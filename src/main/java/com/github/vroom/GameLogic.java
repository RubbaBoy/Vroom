package com.github.vroom;

import com.github.vroom.input.keyboard.KeyboardInput;
import com.github.vroom.input.mouse.MouseInput;

public interface GameLogic {

    void init(Window window);

    void input(MouseInput mouseInput, KeyboardInput keyboardInput);

    void update(float interval, MouseInput mouseInput, Window window);

    void render(Window window);

    void cleanup();
}