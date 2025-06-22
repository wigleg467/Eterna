package com.sillyrilly.managers;

public class GameStateManager {
    public static GameStateManager instance;

    public enum GameState {
        RUNNING, PAUSED, INVENTORY, DIALOG
    }

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
