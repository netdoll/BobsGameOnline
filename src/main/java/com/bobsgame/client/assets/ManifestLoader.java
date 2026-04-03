package com.bobsgame.client.assets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.net.URL;

public class ManifestLoader {
    private static final Gson gson = new Gson();

    public void load(String manifestUrl) {
        try {
            URL url = new URL(manifestUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonObject manifest = gson.fromJson(reader, JsonObject.class);
            
            System.out.println("[ManifestLoader] Loading manifest v" + manifest.get("version").getAsString());
            
            // Map textures, audio, etc. to internal LibGDX loaders
            JsonObject assets = manifest.getAsJsonObject("assets");
            JsonObject textures = assets.getAsJsonObject("textures");
            
            for (String key : textures.keySet()) {
                String path = textures.get(key).getAsString();
                // trigger load in AssetManager
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
