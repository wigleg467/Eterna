package com.sillyrilly.gamelogic.ecs.utils;

public class GameState {
    public static GameState instance = new GameState();
    public GameStats stats = new GameStats();
    public static boolean talkedToLumberjack = false;
    public static boolean defeatedForestMonsters = false;

    public static boolean talkedToNun = false;
    public static boolean defeatedCemeteryMonsters = false;

    public static boolean gotBlessing = false;

    public static boolean defeatedHellGatesMonsters = false;

    public static boolean hell = false;

    public static boolean heaven = false;

    public static boolean gameOver = false;

    public void reset() {
        stats.reset();
        defeatedCemeteryMonsters = false;
        defeatedForestMonsters = false;
        defeatedHellGatesMonsters = false;
        gotBlessing = false;
        talkedToNun = false;
        talkedToLumberjack = false;
        hell = false;
        heaven = false;
    }
}
