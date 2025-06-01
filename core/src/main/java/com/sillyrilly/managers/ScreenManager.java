package com.sillyrilly.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.screens.GameScreen;
import com.sillyrilly.screens.MenuScreen;

import java.util.EnumMap;

import com.sillyrilly.screens.SettingsScreen;

public class ScreenManager implements Disposable {
    public enum ScreenType {
        MENU, GAME, SETTINGS
    }

    private static ScreenManager instance;

    private Game game;
    private final EnumMap<ScreenType, Screen> screenMap = new EnumMap<>(ScreenType.class);

    private ScreenManager() {
    }

    public static ScreenManager getInstance() {
        if (instance == null) instance = new ScreenManager();
        return instance;
    }

    public void initialize(Game game) {
        this.game = game;
        setScreen(ScreenManager.ScreenType.MENU);
    }

    public void setScreen(ScreenType type) {
        if (!screenMap.containsKey(type)) {
            screenMap.put(type, createScreen(type));
        }
        game.setScreen(screenMap.get(type));
    }

    public void dispose() {
        for (Screen screen : screenMap.values()) screen.dispose();
        screenMap.clear();
    }

    private Screen createScreen(ScreenType type) {
        return switch (type) {
            case MENU -> new MenuScreen();
            case GAME -> new GameScreen();
            case SETTINGS -> new SettingsScreen();
        };
    }

}
