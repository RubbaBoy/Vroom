package com.github.vroom.graph.mesh;

import com.github.vroom.graph.collision.Collision;

import java.util.function.Function;

public interface CollisionMesh {

    Collision[][] getCollision();

    Function<Mesh, Collision[]> getCollisionComputation();
}
