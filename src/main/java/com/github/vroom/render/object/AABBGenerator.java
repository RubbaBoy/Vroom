package com.github.vroom.render.object;

import com.github.vroom.render.mesh.Mesh;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AABBGenerator {

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
