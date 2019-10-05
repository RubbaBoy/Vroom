package com.github.vroom.graph.collision.aabb;

import com.github.vroom.graph.collision.Collision;
import com.github.vroom.graph.mesh.Mesh;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;

public final class AABBGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AABBGenerator.class);

    public static final Function<Mesh, Collision[]> GENERATE_COLLISIONS = mesh ->
            new Collision[] { AABBGenerator.generateAABB(mesh) };

    private AABBGenerator() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static Collision generateAABB(Mesh mesh) {
        var positions = mesh.getPositions();

        float minX, minY, minZ, maxX, maxY, maxZ;
        minX = minY = minZ = maxX = maxY = maxZ = Float.MIN_VALUE;

        for (int i = 0; i < positions.length; i++) {
            var curr = positions[i];

            switch (i % 3) {
                case 0:
                    minX = min(minX, curr);
                    maxX = max(maxX, curr);
                    break;
                case 1:
                    minY = min(minY, curr);
                    maxY = max(maxY, curr);
                    break;
                case 2:
                    minZ = min(minZ, curr);
                    maxZ = max(maxZ, curr);
                    break;
            }
        }

        return AABB.fromMaxMin(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
