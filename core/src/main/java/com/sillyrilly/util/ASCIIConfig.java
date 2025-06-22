package com.sillyrilly.util;

public class ASCIIConfig {
    public final static int ANTI_ALIASING = 1;

    public final static int SCREEN_WIDTH = 160 * ANTI_ALIASING;
    public final static int SCREEN_HEIGHT = 45 * ANTI_ALIASING;

    public final static int CELL_W = 1280 / SCREEN_WIDTH / ANTI_ALIASING;   // ширина символу у пікселях
    public final static int CELL_H = 720 / SCREEN_HEIGHT / ANTI_ALIASING;  // висота символу у пікселях

    public final static float FOV = (float) Math.PI / 9 * 5;
    public final static float ROT_SPEED = 1.5f;  // radians per second
    public static float maxDepth = 1f;

    public static float footstepCooldown = 0.9f;    // затримка між кроками (у секундах)
    public static float footstepTimer = 0f;         // таймер до наступного кроку

    public final static int[][] MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {3, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 4},
        {1, 0, 2, 1, 1, 1, 0, 1, 0, 1, 1, 1, 2, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
        {1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 2, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };

    public final static int EMPTY = 0;
    public final static int WALL = 1;
    public final static int DOOR = 2;
    public final static int DOOR_BACK = 3;
    public final static int DOOR_FORWARD = 4;

    public final static int MAP_WIDTH = MAP[0].length;
    public final static int MAP_HEIGHT = MAP.length;

    public final static String CHARACTER_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 .｡°·,:;!?()[]{}<>♢Ꚛ+-—_=*/⁄\\\"'#@%^&|~█▓▒░";
}
