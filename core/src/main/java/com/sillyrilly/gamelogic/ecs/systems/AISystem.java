package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.sillyrilly.gamelogic.ecs.components.AIComponent;

public class AISystem extends EntitySystem {
    private final ComponentMapper<AIComponent> am = ComponentMapper.getFor(AIComponent.class);

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AIComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : entities) {
            AIComponent ai = am.get(e);
            ai.stateMachine.update();
        }
    }
}
