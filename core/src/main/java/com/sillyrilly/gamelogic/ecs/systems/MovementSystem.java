package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.FacingComponent;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.gamelogic.ecs.components.VelocityComponent;
import com.sillyrilly.managers.InputManager;
import com.sillyrilly.managers.CollisionManager;

public class MovementSystem extends EntitySystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);

    private InputManager inputManager;


    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get());
        inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        Vector2 inputDir = inputManager.getMovementDirection();

        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            FacingComponent facing = fm.get(entity);

            Vector2 attemptedPos = new Vector2(pos.position).mulAdd(vel.velocity, deltaTime);

            if (inputDir.x > 0) {
                facing.facingRight = true;
            } else if (inputDir.x < 0) {
                facing.facingRight = false;
            }

            if (!CollisionManager.getInstance().isBlocked(attemptedPos.x, attemptedPos.y)) {
                pos.position.set(attemptedPos);
            }
        }
    }
}
