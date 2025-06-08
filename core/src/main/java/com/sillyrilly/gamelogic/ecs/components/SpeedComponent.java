package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class SpeedComponent implements Component {
    public float speed = 100f; // px/s

    public SpeedComponent(float speed) {
        this.speed = speed;
    }
}
