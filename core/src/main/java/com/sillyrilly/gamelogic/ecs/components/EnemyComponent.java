package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;

import java.util.LinkedList;
import java.util.Queue;

public class EnemyComponent implements Component {
    public EnemyType enemyType;
    public Queue<Vector2> path = new LinkedList<>();

    public EnemyComponent(EnemyType type) {
        this.enemyType = type;
    }
}
