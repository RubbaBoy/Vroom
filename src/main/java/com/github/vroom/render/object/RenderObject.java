package com.github.vroom.render.object;

import com.github.vroom.render.mesh.MultiMesh;
import org.joml.Vector3f;

import java.util.Arrays;

public final class RenderObject {

    private final MultiMesh multiMesh;

    private final Vector3f position;

    private final Vector3f rotation;

    private Collision[][] bounds;

    private boolean collision;

    private float scale;

    public RenderObject(MultiMesh multiMesh) {
        this(multiMesh, multiMesh.getBounds().length > 0);
    }

    public RenderObject(MultiMesh multiMesh, boolean collision) {
        this.multiMesh = multiMesh;
        this.collision = collision;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.bounds = multiMesh.getCopiedBounds(scale);
        this.scale = 1;
    }

    public float getScale() {
        return scale;
    }

    public MultiMesh getMultiMesh() {
        return multiMesh;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public boolean hasCollision() {
        return collision;
    }

    public void setScale(float scale) {
        if (this.scale != scale) {
            this.bounds = multiMesh.getCopiedBounds(scale);
            this.scale = scale;
        }
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean collidesWith(Vector3f colliding) {
        return Arrays.stream(bounds).flatMap(Arrays::stream).anyMatch(bound -> bound.intersects(colliding));
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        Arrays.stream(bounds).flatMap(Arrays::stream).forEach(bound -> bound.setPosition(x, y, z));
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

}
