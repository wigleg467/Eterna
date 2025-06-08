package com.sillyrilly.gamelogic.ecs.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.gamelogic.ecs.components.SpeedComponent;
import com.sillyrilly.gamelogic.ecs.components.VelocityComponent;
import com.sillyrilly.managers.InputManager;


public class InputSystem extends EntitySystem {
    private ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    private ImmutableArray<Entity> controlledEntities;

    @Override
    public void addedToEngine(Engine engine) {
        controlledEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        InputManager.update();
        Vector2 movement = InputManager.getMovementDirection();

        for (Entity entity : controlledEntities) {
            VelocityComponent vel = vm.get(entity);
            SpeedComponent speed = sm.get(entity);
            if (speed != null) {
                vel.velocity.set(movement).scl(speed.speed);
            } else {
                vel.velocity.set(movement).scl(100f);
            }
        }
    }
}
