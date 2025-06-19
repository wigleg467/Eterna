package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Pathfinding {
    public static List<Vector2> findPath(int[][] grid, int startX, int startY, int goalX, int goalY) {
        // Тут може бути твоя реалізація A*, або бібліотека, або простий BFS для початку
        // Для прикладу — просто лінійна траєкторія:
        List<Vector2> path = new ArrayList<>();
        path.add(new Vector2(goalX, goalY)); // Просто йде в ціль
        return path;
    }
}
