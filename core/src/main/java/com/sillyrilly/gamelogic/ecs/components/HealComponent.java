package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class HealComponent implements Component {
    public float hp;
    public boolean isAlive = true;
    public float hitTimer = 0f;
    public boolean wasHit;

    public HealComponent(float hp) {
        this.hp = hp;
    }

    public boolean takeDamage(float damage) {
        hp -= damage;
        wasHit = true;
        if (hp <= 0) {
            isAlive = false;
        }
        return isAlive;
    }
}
