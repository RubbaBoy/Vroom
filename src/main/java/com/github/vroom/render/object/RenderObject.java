package com.github.vroom.render.object;

import com.github.vroom.render.mesh.Mesh;
import org.joml.Vector3f;

public final class RenderObject {

    private float scale;

    private final Mesh mesh;

    private final Vector3f position;

    private final Vector3f rotation;

    public RenderObject(Mesh mesh) {
        this.scale = 1;
        this.mesh = mesh;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
    }

    public float getScale() {
        return scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

}
