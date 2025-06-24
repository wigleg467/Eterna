package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;
import com.sillyrilly.managers.InputManager;

import static com.sillyrilly.gamelogic.ecs.utils.GameState.heaven;
import static com.sillyrilly.gamelogic.ecs.utils.GameState.hell;
import static com.sillyrilly.util.Const.PPM;
import static com.sillyrilly.util.GameConfig.PLAYER_SPEED;

public class MovementSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);

    private InputManager inputManager;

    private Entity player;

    private boolean initHell = false;
    private boolean initHeaven = false;

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
        if (hell && !initHell) {
            body.body.setTransform(17100 / PPM, 72 / PPM, body.body.getAngle());

            initHell = true;
        } else if (heaven && !initHeaven) {
            body.body.setTransform(16880 / PPM, 9475 / PPM, body.body.getAngle());

            initHeaven = true;
        }

        body.body.setLinearVelocity(movement);
    }
}
