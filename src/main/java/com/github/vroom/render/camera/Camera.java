package com.github.vroom.render.camera;

import com.github.vroom.Vroom;
import com.github.vroom.render.object.RenderObject;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Camera {

    private static final Logger LOGGER = LoggerFactory.getLogger(Camera.class);

    private Vroom vroom;

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
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        var prevPosition = new Vector3f(position);
        if (offsetZ != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }

        if (offsetX != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }

        position.y += offsetY;

        if (vroom.getRenderObjects().parallelStream().filter(RenderObject::hasCollision).anyMatch(rObj -> rObj.collideWith(position))) {
            setPosition(prevPosition);
        }
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}
