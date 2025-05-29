package com.sillyrilly;

import com.badlogic.gdx.Screen;

public enum ScreenEnum {
    MENU,
    GAME,
    SETTINGS;

    public Screen create() {
        switch (this) {
            case MENU: return new MenuScreen();
            case GAME: return new GameScreen();
            case SETTINGS: return new SettingsScreen();
            default: throw new IllegalArgumentException("Unknown screen: " + this);
        }
    }
}
