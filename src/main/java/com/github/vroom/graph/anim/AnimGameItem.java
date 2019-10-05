package com.github.vroom.graph.anim;

import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.graph.mesh.MultiMesh;
import com.github.vroom.items.GameItem;

import java.util.Map;
import java.util.Optional;

public class AnimGameItem extends GameItem {

    private Map<String, Animation> animations;

    private Animation currentAnimation;

    public AnimGameItem(MultiMesh multiMesh, Map<String, Animation> animations) {
        super(multiMesh);
        this.animations = animations;
        Optional<Map.Entry<String, Animation>> entry = animations.entrySet().stream().findFirst();
        currentAnimation = entry.isPresent() ? entry.get().getValue() : null;
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }
}
