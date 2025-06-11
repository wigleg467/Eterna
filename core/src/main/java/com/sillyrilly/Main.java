package com.sillyrilly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
        AudioManager.getInstance().dispose();
        ScreenManager.getInstance().dispose();
        Gdx.app.log("DISPOSE", "Game disposed");
        Gdx.app.exit();
    }

    private void initialize() {
        CameraManager.getInstance().initialize(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT);
        ScreenManager.getInstance().initialize(this);
        AudioManager.getInstance().load();
        Gdx.input.setInputProcessor(InputManager.getInstance());

        // Завантаження ресурсів
        // AssetManagerWrapper.getInstance().loadAssets();
        // GameStateManager.getInstance().setState(GameStateManager.GameState.RUNNING);

    }
}
