package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class HealComponent implements Component {
    public float hp;
    public boolean isAlive = true;

    public HealComponent(float hp) {
        this.hp = hp;
    }

    public boolean takeDamage(float damage) {
        hp -= damage;
        if (hp <= 0) {
            isAlive = false;
        }
        return isAlive;
    }
}
