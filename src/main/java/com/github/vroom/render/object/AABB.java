package com.github.vroom.render.object;

import org.joml.Vector3f;

public final class AABB implements Collision {

    private float x;

    private float y;

    private float z;

    private float minX;

    private float minY;

    private float minZ;

    private float maxX;

    private float maxY;

    private float maxZ;

    private float scale = 1;

    public AABB(AABB aabb) {
        this(aabb.x, aabb.y, aabb.z, aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY,
                aabb.maxZ);
    }

    public AABB(float x, float y, float z, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
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
     * Creates an {@link AABB} from the given (minX, minY, minZ) and (length, width, height) coordinates. This
     * features (x, y, z) coordinates for models defined and placed directly.
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @param minX The minimum X relative coordinate of the box
     * @param minY The minimum Y relative coordinate of the box
     * @param minZ The minimum Z relative coordinate of the box
     * @param length The length (derived from minX)
     * @param width The width (derived from minZ)
     * @param height The height (derived from minY)
     * @return The resulting {@link AABB}
     */
    public static AABB fromRelative(float x, float y, float z, float minX, float minY, float minZ, float length,
                                    float width, float height) {
        return new AABB(x, y, z, minX, minY, minZ, minX + length, minY + height, minZ + width);
    }

    /**
     * Creates an {@link AABB} from the given (minX, minY, minZ) and (length, width, height) coordinates.
     *
     * @param minX The minimum X relative coordinate of the box
     * @param minY The minimum Y relative coordinate of the box
     * @param minZ The minimum Z relative coordinate of the box
     * @param length The length (derived from minX)
     * @param width The width (derived from minZ)
     * @param height The height (derived from minY)
     * @return The resulting {@link AABB}
     */
    public static AABB fromRelative(float minX, float minY, float minZ, float length, float width, float height) {
        return new AABB(0, 0, 0, minX, minY, minZ, minX + length, minY + height, minZ + width);
    }

    public static AABB fromMaxMin(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new AABB(0, 0, 0, minX, minY, minZ, maxX, maxY, maxZ);
    }

    public AABB cloneWithPosition(float x, float y, float z) {
        var aabbClone = new AABB(this);

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
    public boolean intersects(float x, float y, float z) {
        x -= this.x;
        y -= this.y;
        z -= this.z;

        return (x >= minX && x <= maxX) && (y >= minY && y <= maxY) && (z >= minZ && z <= maxZ);
    }

    @Override
    public boolean intersects(Vector3f vector3f) {
        return intersects(vector3f.x, vector3f.y, vector3f.z);
    }

    @Override
    public boolean intersects(Collision collision) {
        if (!(collision instanceof AABB)) {
            throw new UnsupportedOperationException("AABBBox#intersect not available for " +
                    collision.getClass().getSimpleName());
        }

        var aabbBox = (AABB) collision;

        return (minX <= aabbBox.maxX && maxX >= aabbBox.minX) && (minY <= aabbBox.maxY && maxY >= aabbBox.minY) &&
                (minZ <= aabbBox.maxZ && maxZ >= aabbBox.minZ);
    }

    @Override
    public Collision copy() {
        return new AABB(this);
    }

    @Override
    public void scale(float scale) {
        var multiplying = (1f / this.scale) * scale;

        if (multiplying == 1) {
            return;
        }

        minX *= multiplying;
        minY *= multiplying;
        minZ *= multiplying;
        maxX *= multiplying;
        maxY *= multiplying;
        maxZ *= multiplying;

        this.scale = scale;
    }
}
