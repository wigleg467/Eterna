package com.sillyrilly;

import com.badlogic.gdx.Game;
import com.sillyrilly.managers.DialogueManager;
import com.sillyrilly.managers.*;
import com.sillyrilly.util.Const;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    @Override
    public void create() {
        AudioManager.initialize();
        AssetsManager.initialize();
        CameraManager.initialize(Const.VIRTUAL_WIDTH, Const.VIRTUAL_HEIGHT);
        FontManager.initialize();
        InputManager.initialize();
        ScreenManager.initialize(this);
        DialogueManager.initialize();
    }

    @Override
    public void resize(int width, int height) {
        CameraManager.instance.resize(width, height);
    }

    @Override
    public void dispose() {
        AudioManager.instance.dispose();
        AssetsManager.instance.dispose();
        FontManager.instance.dispose();
        ScreenManager.instance.dispose();
    }
}
