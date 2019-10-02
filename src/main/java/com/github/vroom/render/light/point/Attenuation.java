package com.github.vroom.render.light.point;

public final class Attenuation {

    private float constant;

    private float linear;

    private float exponent;

    public Attenuation(float constant, float linear, float exponent) {
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public float getConstant() {
        return constant;
    }

    public float getLinear() {
        return linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
}
