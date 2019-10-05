package com.github.vroom.items;

import com.github.vroom.graph.collision.Collision;
import com.github.vroom.graph.mesh.MultiMesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.github.vroom.graph.mesh.Mesh;

import java.util.Arrays;
import java.util.Objects;

public class GameItem {

    private MultiMesh multiMesh;

    private boolean selected;

    private final Vector3f position;

    private float scale;

    private final Quaternionf rotation;

    private int textPos;

    private boolean disableFrustumCulling;

    private boolean insideFrustum;

    private boolean collision = true;

    private Collision[][] bounds;

    public GameItem() {
        selected = false;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Quaternionf();
        textPos = 0;
        insideFrustum = true;
        disableFrustumCulling = false;
        bounds = new Collision[0][];
    }

    public GameItem(Mesh mesh) {
        this(new MultiMesh(mesh));
    }

    public GameItem(MultiMesh multiMesh) {
        this();
        this.multiMesh = multiMesh;
        this.bounds = multiMesh.getCopiedBounds(scale);
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getTextPos() {
        return textPos;
    }

    public boolean isSelected() {
        return selected;
    }

    public final void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        Arrays.stream(bounds).flatMap(Arrays::stream).forEach(bound -> bound.setPosition(x, y, z));
    }

    public float getScale() {
        return scale;
    }

    public final void setScale(float scale) {
        if (this.scale != scale) {
            this.bounds = multiMesh.getCopiedBounds(scale);
            this.scale = scale;
        }
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public final void setRotation(Quaternionf q) {
        this.rotation.set(q);
    }

    public void setMultiMesh(MultiMesh multiMesh) {
        this.multiMesh = multiMesh;
    }

    public MultiMesh getMultiMesh() {
        return multiMesh;
    }

    public void cleanup() {
        multiMesh.cleanup();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
    }

    public boolean isInsideFrustum() {
        return insideFrustum;
    }

    public void setInsideFrustum(boolean insideFrustum) {
        this.insideFrustum = insideFrustum;
    }

    public boolean isDisableFrustumCulling() {
        return disableFrustumCulling;
    }

    public void setDisableFrustumCulling(boolean disableFrustumCulling) {
        this.disableFrustumCulling = disableFrustumCulling;
    }

    public boolean hasCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean collidesWith(Vector3f colliding) {
        return Arrays.stream(bounds).flatMap(Arrays::stream).anyMatch(bound -> bound.intersects(colliding));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameItem gameItem = (GameItem) o;
        return selected == gameItem.selected &&
                Float.compare(gameItem.scale, scale) == 0 &&
                textPos == gameItem.textPos &&
                disableFrustumCulling == gameItem.disableFrustumCulling &&
                insideFrustum == gameItem.insideFrustum &&
                multiMesh.equals(gameItem.multiMesh) &&
                position.equals(gameItem.position) &&
                rotation.equals(gameItem.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(multiMesh, selected, position, scale, rotation, textPos, disableFrustumCulling, insideFrustum);
    }
}
