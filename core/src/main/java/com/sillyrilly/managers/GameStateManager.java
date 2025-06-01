package com.sillyrilly.managers;

public class GameStateManager {
    public enum GameState {
        RUNNING, PAUSED, INVENTORY, DIALOG
    }

    private static GameStateManager instance;

    private GameState currentState = GameState.RUNNING;

    private GameStateManager() {
    }

    public static GameStateManager getInstance() {
        if (instance == null) instance = new GameStateManager();
        return instance;
    }

    public void setState(GameState state) {
        currentState = state;
    }

    public GameState getState() {
        return currentState;
    }

    public boolean is(GameState state) {
        return currentState == state;
    }
}
