package com.github.vroom.demo.tutorial;

import com.github.vroom.GameEngine;
import com.github.vroom.GameLogic;
import com.github.vroom.Window;

public class Main {

    public static void main(String[] args) {
        try {
            GameLogic gameLogic = new DummyGame();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = false;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = true;
            opts.frustumCulling = false;
            GameEngine gameEng = new GameEngine("GAME", false, opts, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}
