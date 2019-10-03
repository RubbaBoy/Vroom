package com.github.vroom.render.mesh;

import com.github.vroom.render.object.Collision;

public interface AABBMesh {

    Collision[][] getAABBs();

    boolean autoComputeAABB();
}
