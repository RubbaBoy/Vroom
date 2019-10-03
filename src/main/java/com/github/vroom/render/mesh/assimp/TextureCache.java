package com.github.vroom.render.mesh.assimp;

import com.github.vroom.render.Texture;

import java.util.HashMap;
import java.util.Map;

public final class TextureCache {

    private static final Map<String, Texture> TEXTURE_MAP = new HashMap<>();

    private TextureCache() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    public static Texture getTexture(String path) {
        return TEXTURE_MAP.computeIfAbsent(path, Texture::new);
    }
}
