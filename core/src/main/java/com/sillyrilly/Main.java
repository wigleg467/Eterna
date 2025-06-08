package com.sillyrilly;

import com.badlogic.gdx.Game;
import com.sillyrilly.managers.*;
import com.sillyrilly.util.GameConfig;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    @Override
    public void create() {
        initialize();
    }

    @Override
    public void resize(int width, int height) {
        CameraManager.getInstance().resize(width, height);
    }

    @Override
    public void dispose() {
       // AssetManagerWrapper.getInstance().dispose();
      //  AudioManager.getInstance().dispose();
        ScreenManager.getInstance().dispose();
    }

    private void initialize() {
        // Завантаження ресурсів
        // AssetManagerWrapper.getInstance().loadAssets();

        // AudioManager.getInstance().load();
        // GameStateManager.getInstance().setState(GameStateManager.GameState.RUNNING);

        CameraManager.getInstance().initialize(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT);
        ScreenManager.getInstance().initialize(this);
    }
}
