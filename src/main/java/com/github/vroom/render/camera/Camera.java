package com.github.vroom.render.camera;

import com.github.vroom.Vroom;
import com.github.vroom.render.object.RenderObject;
import org.joml.Vector3f;

public final class Camera {

    private final Vroom vroom;

    private final Vector3f position;

    private final Vector3f rotation;

    public Camera(Vroom vroom) {
        this(vroom, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    public Camera(Vroom vroom, Vector3f position, Vector3f rotation) {
        this.vroom = vroom;
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        var prevPosition = new Vector3f(position);

        if (offsetZ != 0) {
            double radians = Math.toRadians(rotation.y());
            position.add((float) Math.sin(radians) * -1.0f * offsetZ, 0, (float) Math.cos(radians) * offsetZ);
        }

        if (offsetX != 0) {
            double radians = Math.toRadians(rotation.y() - 90);
            position.add((float) Math.sin(radians) * -1.0f * offsetX, 0, (float) Math.cos(radians) * offsetX);
        }

        position.y += offsetY;

        if (vroom.getRenderObjects().parallelStream().filter(RenderObject::hasCollision)
                .anyMatch(rObj -> rObj.collidesWith(position))) {
            setPosition(prevPosition);
        }
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.add(offsetX, offsetY, offsetZ);
    }
}
