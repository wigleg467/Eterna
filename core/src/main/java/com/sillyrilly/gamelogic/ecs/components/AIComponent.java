package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;

public class AIComponent implements Component {
    public StateMachine<Entity, EnemyState> stateMachine;
}
