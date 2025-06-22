package com.sillyrilly.gamelogic.ecs.utils;

public class GameState {
    public boolean talkedToLumberjack = false;
    public boolean defeatedForestMonsters = false;

    public boolean talkedToNun = false;
    public boolean defeatedCemeteryMonsters = false;

    public boolean gotBlessing = false;

    public static final GameState instance = new GameState();
}
