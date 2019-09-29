package com.github.vroom.render.transform;

import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.object.RenderObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Transformation {

    private final Matrix4f projectionMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f viewMatrix;

    public Transformation() {
        this.projectionMatrix = new Matrix4f();
        this.modelViewMatrix = new Matrix4f();
        this.viewMatrix = new Matrix4f();
    }

    public final Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;

        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);

        return projectionMatrix;
    }

    public Matrix4f getModelViewMatrix(RenderObject RenderObjects, Matrix4f viewMatrix) {
        Vector3f rotation = RenderObjects.getRotation();

        modelViewMatrix.set(viewMatrix).translate(RenderObjects.getPosition())
                .rotateX((float) Math.toRadians(-rotation.x))
                .rotateY((float) Math.toRadians(-rotation.y))
                .rotateZ((float) Math.toRadians(-rotation.z))
                .scale(RenderObjects.getScale());

        return modelViewMatrix;
    }

    public Matrix4f getViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));

        // Then do the translation
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        return viewMatrix;
    }

}
