package com.github.vroom.render.object;

import org.joml.Vector3f;

public interface AABB {

    void setPosition(float x, float y, float z);

    boolean intersect(float x, float y, float z);

    boolean intersect(Vector3f vector3f);

    boolean intersect(AABB aabb);

    AABB copy();
}
