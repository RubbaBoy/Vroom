package com.github.vroom.items;

import com.github.vroom.loaders.assimp.StaticMeshesLoader;
import org.joml.Vector4f;
import com.github.vroom.graph.Material;
import com.github.vroom.graph.mesh.Mesh;
import com.github.vroom.graph.Texture;

public class SkyBox extends GameItem {

    public SkyBox(String objModel, String textureFile) {
        helper(objModel, new Material(new Texture(textureFile), 0.0f));
    }

    public SkyBox(String objModel, Vector4f color) {
        helper(objModel, new Material(color, 0));
    }

    private void helper(String objModel, Material material) {
        var skyBoxMesh = StaticMeshesLoader.load(objModel, "", true);
        setMultiMesh(skyBoxMesh);
        skyBoxMesh.setMaterial(material);
        skyBoxMesh.createTextures();
        setPosition(0, 0, 0);
    }
}
