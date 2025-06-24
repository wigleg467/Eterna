package com.sillyrilly.gamelogic.ecs.utils;

import java.util.HashMap;
import java.util.Map;

public class GameStats {
    private final Map<String, Integer> kills = new HashMap<>();

    public void addKill(String enemyName) {
        kills.put(enemyName, kills.getOrDefault(enemyName, 0) + 1);
    }

    public String formatStats() {
        if (kills.isEmpty()) return "Ти не вбив жодного ворога 😢";
        StringBuilder sb = new StringBuilder("Ти вбив:\n");
        for (Map.Entry<String, Integer> entry : kills.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    public void reset() {
        kills.clear();
    }
}
