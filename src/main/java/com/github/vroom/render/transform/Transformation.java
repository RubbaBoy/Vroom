package com.github.vroom.render.transform;

import com.github.vroom.render.camera.Camera;
import com.github.vroom.render.object.RenderObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Transformation {

    private final Matrix4f viewMatrix;

    private final Matrix4f modelMatrix;

    private final Matrix4f lightViewMatrix;

    private final Matrix4f modelLightMatrix;

    private final Matrix4f modelViewMatrix;

    private final Matrix4f modelLightViewMatrix;

    private final Matrix4f orthoProjMatrix;

    private final Matrix4f projectionMatrix;

    public Transformation() {
        this.viewMatrix = new Matrix4f();
        this.modelMatrix = new Matrix4f();
        this.lightViewMatrix = new Matrix4f();
        this.modelLightMatrix = new Matrix4f();
        this.modelViewMatrix = new Matrix4f();
        this.modelLightViewMatrix = new Matrix4f();
        this.orthoProjMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
    }

    public Matrix4f getLightViewMatrix() {
        return lightViewMatrix;
    }

    public Matrix4f getOrthoProjectionMatrix() {
        return orthoProjMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f buildModelViewMatrix(RenderObject renderObject, Matrix4f matrix) {
        Vector3f rotation = renderObject.getRotation();
        modelMatrix.identity().translate(renderObject.getPosition())
                .rotateX((float)Math.toRadians(-rotation.x))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateZ((float)Math.toRadians(-rotation.z))
                .scale(renderObject.getScale());
        return modelViewMatrix.set(matrix).mul(modelMatrix);
    }

    public Matrix4f buildModelLightViewMatrix(RenderObject renderObject, Matrix4f matrix) {
        Vector3f rotation = renderObject.getRotation();
        modelLightMatrix.identity().translate(renderObject.getPosition())
                .rotateX((float)Math.toRadians(-rotation.x))
                .rotateY((float)Math.toRadians(-rotation.y))
                .rotateZ((float)Math.toRadians(-rotation.z))
                .scale(renderObject.getScale());
        return modelLightViewMatrix.set(matrix).mul(modelLightMatrix);
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }

    public Matrix4f updateOrthoProjectionMatrix(float left, float right, float bottom, float top, float zNear,
                                                float zFar) {
        return orthoProjMatrix.identity().setOrtho(left, right, bottom, top, zNear, zFar);
    }

    public Matrix4f updateProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        return projectionMatrix.identity().perspective(fov, aspectRatio, zNear, zFar);
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        return updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), viewMatrix);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        return matrix.identity()
                .rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .translate(-position.x, -position.y, -position.z);
    }

}
