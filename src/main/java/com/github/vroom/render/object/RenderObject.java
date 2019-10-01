package com.github.vroom.render.object;

import com.github.vroom.render.mesh.Mesh;
import org.joml.Vector3f;

public final class RenderObject {

    private float scale;

    private final Mesh mesh;

    private final Vector3f position;

    private final Vector3f rotation;

    private boolean collision;

    private AABB[] bounds;

    public RenderObject(Mesh mesh) {
        this(mesh, mesh.getBounds().length > 0);
    }

    public RenderObject(Mesh mesh, boolean collision) {
        this.scale = 1;
        this.mesh = mesh;
        this.collision = collision;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.bounds = mesh.getCopiedBounds();
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

    public boolean hasCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean collideWith(Vector3f colliding) {
        for (var bound : bounds) {
            if (bound.intersect(colliding)) {
                return true;
            }
        }

        return false;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
        for (AABB bound : bounds) {
            bound.setPosition(x, y, z);
        }
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

}
