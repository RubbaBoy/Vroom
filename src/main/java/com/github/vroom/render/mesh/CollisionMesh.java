package com.github.vroom.render.mesh;

import com.github.vroom.render.object.Collision;

public interface CollisionMesh {

    Collision[][] getCollision();

    boolean autoComputeCollision();
}
