package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraTargetComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;
import com.sillyrilly.managers.InputManager;

public class MovementSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> ac = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<CameraTargetComponent> ctc = ComponentMapper.getFor(CameraTargetComponent.class);

    private InputManager inputManager;

    private ImmutableArray<Entity> controlledEntities;

    @Override
    public void addedToEngine(Engine engine) {
        controlledEntities = engine.getEntitiesFor(Family.all(BodyComponent.class, PlayerComponent.class).get());
        inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        Vector2 movement = new Vector2(inputManager.getMovement());// робимо копію
        movement.scl(7f);
        for (Entity entity : controlledEntities) {
            if (ctc.has(entity)) {
                BodyComponent body = bc.get(entity);
                if (ac.get(entity).currentState != AnimationComponent.State.ATTACK)
                    body.getBody().setLinearVelocity(movement);
                break;
            }
        }
    }
}
