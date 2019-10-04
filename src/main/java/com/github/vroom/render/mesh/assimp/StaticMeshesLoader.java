package com.github.vroom.render.mesh.assimp;

import com.github.vroom.render.Texture;
import com.github.vroom.render.light.Material;
import com.github.vroom.render.mesh.Mesh;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static com.github.vroom.utility.Utility.floatListToArray;
import static com.github.vroom.utility.Utility.integerListToArray;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_AMBIENT;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_SPECULAR;
import static org.lwjgl.assimp.Assimp.aiGetErrorString;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

public final class StaticMeshesLoader {

    public static Mesh[] load(String resourcePath, String texturesDir, boolean autoGenAABB) {
        return load(resourcePath, texturesDir, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate |
                aiProcess_FixInfacingNormals, autoGenAABB);
    }

    public static Mesh[] load(String resourcePath, String texturesDir, int flags, boolean autoGenCollision) {
        try (AIScene aiScene = aiImportFile(resourcePath, flags)) {
            if (aiScene == null) {
                throw new RuntimeException("Error loading model: " + aiGetErrorString());
            }

            int numMaterials = aiScene.mNumMaterials();
            PointerBuffer aiMaterials = aiScene.mMaterials();
            List<Material> materials = new ArrayList<>();

            for (int i = 0; i < numMaterials; i++) {
                AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
                processMaterial(aiMaterial, materials, texturesDir);
            }

            int numMeshes = aiScene.mNumMeshes();
            PointerBuffer aiMeshes = aiScene.mMeshes();
            Mesh[] meshes = new Mesh[numMeshes];

            for (int i = 0; i < numMeshes; i++) {
                AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
                Mesh mesh = processMesh(aiMesh, materials, autoGenCollision);
                meshes[i] = mesh;
            }

            return meshes;
       }
    }

    private static void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) {
        Texture texture = null;

        try (AIString path = AIString.calloc()) {
            Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null,
                    null, null, null, null);

            String textPath = path.dataString();

            if (textPath.length() > 0) {
                String textureFile = "";

                if (texturesDir != null && texturesDir.length() > 0) {
                    textureFile += texturesDir + File.separator;
                }

                textureFile += textPath;
                texture = TextureCache.getTexture(textureFile);
            }
        }

        Vector4f ambient = Material.DEFAULT_COLOR;

        AIColor4D colour = AIColor4D.create();

        int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            ambient = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f diffuse = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            diffuse = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Vector4f specular = Material.DEFAULT_COLOR;
        result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, aiTextureType_NONE, 0, colour);
        if (result == 0) {
            specular = new Vector4f(colour.r(), colour.g(), colour.b(), colour.a());
        }

        Material material = new Material(ambient, diffuse, specular, texture, 1.0f);
        materials.add(material);
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials, boolean autoGenCollision) {
        var vertices = new ArrayList<Float>();
        var textures = new ArrayList<Float>();
        var normals = new ArrayList<Float>();
        var indices = new ArrayList<Integer>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        Mesh mesh = new Mesh(floatListToArray(vertices), floatListToArray(textures), floatListToArray(normals),
                integerListToArray(indices), false, autoGenCollision);
        Material material;

        int materialIdx = aiMesh.mMaterialIndex();

        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }

        mesh.setMaterial(material);

        return mesh;
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();

        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();

            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();

        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();

            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);

        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;

        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();

            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();

        AIFace.Buffer aiFaces = aiMesh.mFaces();

        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);

            IntBuffer buffer = aiFace.mIndices();

            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
}
