package com.github.vroom.render.camera;

import com.github.vroom.Vroom;
import com.github.vroom.render.camera.modifiers.CameraModifier;

import java.util.ArrayList;
import java.util.List;

public class CameraTransformationManager {

    private final Vroom vroom;

    private final Camera camera;

    private final List<CameraModifier> modifiers;

    public CameraTransformationManager(Vroom vroom, Camera camera) {
        this.vroom = vroom;
        this.camera = camera;
        this.modifiers = new ArrayList<>();
    }

    public CameraModifier addModifier(CameraModifier modifier) {
        modifiers.add(modifier);
        return modifier;
    }

    public void update() {
        modifiers.forEach(modifier -> modifier.apply(vroom, camera));
    }

}
