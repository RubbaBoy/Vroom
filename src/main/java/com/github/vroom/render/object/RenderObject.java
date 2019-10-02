package com.github.vroom.render.object;

import com.github.vroom.render.mesh.MultiMesh;
import org.joml.Vector3f;

public final class RenderObject {

    private float scale;

    private final MultiMesh multiMesh;

    private final Vector3f position;

    private final Vector3f rotation;

    private final AABB[][] bounds;

    private boolean collision;

    public RenderObject(MultiMesh multiMesh) {
        this(multiMesh, multiMesh.getBounds().length > 0);
    }

    public RenderObject(MultiMesh multiMesh, boolean collision) {
        this.scale = 1;
        this.multiMesh = multiMesh;
        this.collision = collision;
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.bounds = multiMesh.getCopiedBounds();
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
        this.scale = scale;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean collidesWith(Vector3f colliding) {
        // This method is not done with streams, with the fear being that any overhead could cause performance
        // degradations, as this method will be invoked a LOT.
        for (AABB[] bound2D : bounds) {
            for (var bound : bound2D) {
                if (bound.intersects(colliding)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);

        for (AABB[] bound2D : bounds) {
            for (var bound : bound2D) {
                bound.setPosition(x, y, z);
            }
        }
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

}
