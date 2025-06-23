package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationTopComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;
import com.sillyrilly.managers.InputManager;

import static com.sillyrilly.util.GameConfig.PLAYER_SPEED;

public class MovementSystem extends EntitySystem {
    private final ComponentMapper<AnimationTopComponent> ac = ComponentMapper.getFor(AnimationTopComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);

    private InputManager inputManager;

    private Entity player;

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        inputManager = InputManager.instance;
    }

    @Override
    public void update(float deltaTime) {
        Vector2 movement = new Vector2(inputManager.getMovement());// робимо копію
        movement.scl(PLAYER_SPEED);

        BodyComponent body = bc.get(player);
        if (ac.get(player).currentState != AnimationTopComponent.TopState.ATTACK)
            body.body.setLinearVelocity(movement);
    }
}
