package com.github.vroom.render.light.directional;

import org.joml.Vector3f;

public final class DirectionalLight {

    private float intensity;

    private float shadowPosMult;

    private Vector3f color;

    private Vector3f direction;

    private OrthoCoords orthoCords;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
        this.shadowPosMult = 1;
        this.orthoCords = new OrthoCoords();
    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColor()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public float getIntensity() {
        return intensity;
    }

    public float getShadowPosMult() {
        return shadowPosMult;
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public OrthoCoords getOrthoCoords(){
        return orthoCords;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setShadowPosMult(float shadowPosMult) {
        this.shadowPosMult = shadowPosMult;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setOrthoCords(float left, float right, float bottom, float top, float near, float far) {
        orthoCords.left = left;
        orthoCords.right = right;
        orthoCords.bottom = bottom;
        orthoCords.top = top;
        orthoCords.near = near;
        orthoCords.far = far;
    }
}