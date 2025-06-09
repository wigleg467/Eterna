package com.sillyrilly.managers;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

public class AssetManagerWrapper implements Disposable {
    private static AssetManagerWrapper instance;

    private final AssetManager assetManager = new AssetManager();

    private AssetManagerWrapper() {
    }

    public static AssetManagerWrapper getInstance() {
        if (instance == null) instance = new AssetManagerWrapper();
        return instance;
    }

    public void loadAssets() {
        //   assetManager.load("fonts/default.fnt", BitmapFont.class);
        //   assetManager.load("images/player.png", Texture.class);
        assetManager.finishLoading();
    }

    public <T> T get(String path, Class<T> type) {
        return assetManager.get(path, type);
    }

    public boolean update() {
        return assetManager.update();
    }

    public void dispose() {
        assetManager.dispose();
    }
}
