package com.github.vroom.player;

import com.github.vroom.Vroom;
import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.mesh.MultiMesh;
import com.github.vroom.render.object.RenderObject;

/**
 * The Player class is a class that a {@link Camera} is bound to, and controls the associated player model, with any
 * other related models, such as accessories.
 */
public class Player {

    private Vroom vroom;

    private RenderObject bodyRender;

    private Camera boundCamera;

    private MultiMesh multiMesh;

    public Player(Vroom vroom, MultiMesh multiMesh) {
        this.vroom = vroom;
        this.multiMesh = multiMesh;

        bodyRender = new RenderObject(multiMesh);
        bodyRender.setScale(0.1F);
        bodyRender.setPosition(0, 0, 1);
        bodyRender.setRotation(0, 0, 0);
    }

    public RenderObject getBodyRender() {
        return bodyRender;
    }

    public Camera getBoundCamera() {
        return boundCamera;
    }
}
