package com.github.vroom.render.object;

import org.joml.Vector3f;

public interface AABB {

    void setPosition(float x, float y, float z);

    boolean intersects(float x, float y, float z);

    boolean intersects(Vector3f vector3f);

    boolean intersects(AABB aabb);

    AABB copy();

}
