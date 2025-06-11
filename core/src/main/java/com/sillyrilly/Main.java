package com.sillyrilly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.sillyrilly.managers.*;
import com.sillyrilly.screens.MenuScreen;
import com.sillyrilly.util.GameConfig;

import java.awt.*;

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
        AudioManager.getInstance().dispose();
        ScreenManager.getInstance().dispose();
        Gdx.app.log("DISPOSE", "Game disposed");
        Gdx.app.exit();

        // AssetManagerWrapper.getInstance().dispose();
    }

    private void initialize() {
        CameraManager.getInstance().initialize(GameConfig.VIRTUAL_WIDTH, GameConfig.VIRTUAL_HEIGHT);
        ScreenManager.getInstance().initialize(this);
        AudioManager.getInstance().load();

        // Завантаження ресурсів
        // AssetManagerWrapper.getInstance().loadAssets();
        // GameStateManager.getInstance().setState(GameStateManager.GameState.RUNNING);
    }
}
