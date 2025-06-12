package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class LevelComponent implements Component {
    public int level;

    public LevelComponent(int level) {
        this.level = level;
    }
}
