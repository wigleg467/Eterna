package com.sillyrilly;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import java.util.EnumMap;

public class ScreenManager {
    private static ScreenManager instance;
    private Game game;
    private final EnumMap<ScreenEnum, Screen> screens = new EnumMap<>(ScreenEnum.class);

    private ScreenManager() {}

    public static ScreenManager getInstance() {
        if (instance == null) instance = new ScreenManager();
        return instance;
    }

    public void init(Game game) {
        this.game = game;
    }

    public void setScreen(ScreenEnum type) {
        Screen screen = screens.get(type);

        if (screen == null) {
            screen = type.create(); // Lazy loading
            screens.put(type, screen);
        }

        game.setScreen(screen);
    }

    public void dispose() {
        for (Screen screen : screens.values()) {
            screen.dispose();
        }
        screens.clear();
    }
}
