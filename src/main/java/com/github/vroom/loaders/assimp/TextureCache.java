package com.github.vroom.loaders.assimp;

import com.github.vroom.graph.Texture;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {

    private static TextureCache INSTANCE;

    private Map<String, Texture> texturesMap;

    private TextureCache() {
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public Texture getTexture(String path)  {
        return texturesMap.computeIfAbsent(path, $ -> new Texture(path));
    }
}
