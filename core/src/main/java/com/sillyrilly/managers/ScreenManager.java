package com.sillyrilly.managers;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.screens.ASCIIScreen;
import com.sillyrilly.screens.GameScreen;
import com.sillyrilly.screens.MenuScreen;

import java.util.EnumMap;

public class ScreenManager implements Disposable {
    public static ScreenManager instance;
    public static SpriteBatch batch;
    public static ShapeRenderer shapeRenderer;
    private static Game game;
    private static boolean isInitialized = false;
    private final EnumMap<ScreenType, Screen> screenMap = new EnumMap<>(ScreenType.class);

    private ScreenManager() {
    }

    public static void initialize(Game game) {
        if (!isInitialized) {
            batch = new SpriteBatch();
            instance = new ScreenManager();
            shapeRenderer = new ShapeRenderer();

            ScreenManager.game = game;

            instance.setMenuScreen();

            isInitialized = true;
        }
    }

    public void setScreen(ScreenType type) {
        if (!screenMap.containsKey(type)) screenMap.put(type, ScreenType.createScreen(type));
        game.setScreen(screenMap.get(type));
        ScreenType.setCurrentScreenType(type);
    }

    public void setMenuScreen() {
        if (!screenMap.containsKey(ScreenType.MENU))
            screenMap.put(ScreenType.MENU, ScreenType.createScreen(ScreenType.MENU));

        game.setScreen(screenMap.get(ScreenType.MENU));
    }

    public void dispose() {
        for (Screen screen : screenMap.values()) screen.dispose();
        screenMap.clear();
        batch.dispose();
        shapeRenderer.dispose();
    }

    public enum ScreenType {
        MENU, GAME, ASCII;

        public static ScreenType current = GAME;

        public static Screen createScreen(ScreenType type) {
            return switch (type) {
                case MENU -> new MenuScreen();
                case GAME -> new GameScreen();
                case ASCII -> new ASCIIScreen();
            };
        }

        public static void setCurrentScreenType(ScreenType type) {
            current = type;
        }
    }
}
