package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class AttackComponent implements Component {
    public float attackCooldown = 1.0f; // час між атаками
    public float timeSinceLastAttack = 0f;
    public int damage = 10;
}
