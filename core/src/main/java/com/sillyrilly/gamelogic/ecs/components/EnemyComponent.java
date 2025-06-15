package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.types.EnemyType;

public class EnemyComponent implements Component {
    public EnemyType enemyType;

    public EnemyComponent(EnemyType type) {
        this.enemyType = type;
    }
}
