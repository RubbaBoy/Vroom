package com.github.vroom.render.object;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AABBBox implements AABB {

    private static final Logger LOGGER = LoggerFactory.getLogger(AABBBox.class);

    private float x;

    private float y;

    private float z;

    private float minX;

    private float minY;

    private float minZ;

    private float maxX;

    private float maxY;

    private float maxZ;

    public AABBBox(AABBBox aabbBox) {
        this.x = aabbBox.x;
        this.y = aabbBox.y;
        this.z = aabbBox.z;
        this.minX = aabbBox.minX;
        this.minY = aabbBox.minY;
        this.minZ = aabbBox.minZ;
        this.maxX = aabbBox.maxX;
        this.maxY = aabbBox.maxY;
        this.maxZ = aabbBox.maxZ;
    }

    public AABBBox(float x, float y, float z, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * Creates an {@link AABBBox} from the given (minX, minY, minZ) and (length, width, height) coordinates. This
     * features (x, y, z) coordinates for models defined and placed directly.
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @param minX The minimum X relative coordinate of the box
     * @param minY The minimum Y relative coordinate of the box
     * @param minZ The minimum Z relative coordinate of the box
     * @param length The length (derived from minX)
     * @param width The width (derived from minZ)
     * @param height The height (derived from minY)
     * @return The resulting {@link AABBBox}
     */
    public static AABBBox fromRelative(float x, float y, float z, float minX, float minY, float minZ, float length, float width, float height) {
        return new AABBBox(x, y, z, minX, minY, minZ, minX + length, minY + height, minZ + width);
    }

    /**
     * Creates an {@link AABBBox} from the given (minX, minY, minZ) and (length, width, height) coordinates.
     * @param minX The minimum X relative coordinate of the box
     * @param minY The minimum Y relative coordinate of the box
     * @param minZ The minimum Z relative coordinate of the box
     * @param length The length (derived from minX)
     * @param width The width (derived from minZ)
     * @param height The height (derived from minY)
     * @return The resulting {@link AABBBox}
     */
    public static AABBBox fromRelative(float minX, float minY, float minZ, float length, float width, float height) {
        return new AABBBox(0, 0, 0, minX, minY, minZ, minX + length, minY + height, minZ + width);
    }

    public AABBBox cloneWithPosition(float x, float y, float z) {
        var aabbClone = new AABBBox(this);
        aabbClone.minX = x;
        aabbClone.minY = y;
        aabbClone.minZ = z;
        return aabbClone;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean intersect(float x, float y, float z) {
        x -= this.x;
        y -= this.y;
        z -= this.z;
        return (x >= minX && x <= maxX) &&
                (y >= minY && y <= maxY) &&
                (z >= minZ && z <= maxZ);
    }

    @Override
    public boolean intersect(Vector3f vector3f) {
        return intersect(vector3f.x, vector3f.y, vector3f.z);
    }

    @Override
    public boolean intersect(AABB aabb) {
        if (!(aabb instanceof AABBBox)) {
            throw new UnsupportedOperationException("AABBBox#intersect not available for " + aabb.getClass().getSimpleName());
        }

        var aabbBox = (AABBBox) aabb;
        return (minX <= aabbBox.maxX && maxX >= aabbBox.minX) &&
                (minY <= aabbBox.maxY && maxY >= aabbBox.minY) &&
                (minZ <= aabbBox.maxZ && maxZ >= aabbBox.minZ);
    }

    @Override
    public AABB copy() {
        return new AABBBox(this);
    }
}
