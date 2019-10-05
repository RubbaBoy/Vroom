package com.github.vroom.graph;

import com.github.vroom.Scene;
import com.github.vroom.items.GameItem;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Camera {

    private static final Logger LOGGER = LoggerFactory.getLogger(Camera.class);

    private final Vector3f position;

    private final Vector3f rotation;

    private Matrix4f viewMatrix;

    public Camera() {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        viewMatrix = new Matrix4f();
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Vector3f getPosition() {
        return position;
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

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Matrix4f updateViewMatrix() {
        return Transformation.updateGenericViewMatrix(position, rotation, viewMatrix);
    }

    public void movePosition(Scene scene, float offsetX, float offsetY, float offsetZ) {
        var prevPosition = new Vector3f(position);

        if (offsetZ != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }

        if (offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }

        position.y += offsetY;

        if (scene.getGameItems().parallelStream().filter(GameItem::hasCollision)
                .anyMatch(rObj -> rObj.collidesWith(position))) {
            setPosition(prevPosition);
        }
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }
}