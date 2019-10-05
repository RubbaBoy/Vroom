package com.github.vroom.graph.collision;

import org.joml.Vector3f;

public interface Collision {

    void setPosition(float x, float y, float z);

    boolean intersects(float x, float y, float z);

    boolean intersects(Vector3f vector3f);

    boolean intersects(Collision collision);

    Collision copy();

    /**
     * Multiplies all original max and min values by the given value.
     *
     * @param scale The value to multiply by
     */
    void scale(float scale);
}
