package com.sillyrilly.gamelogic.ecs.systems;


import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.managers.InputManager;


public class InputSystem extends EntitySystem {
    private final ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    private InputManager inputManager;

    private ImmutableArray<Entity> controlledEntities;

    @Override
    public void addedToEngine(Engine engine) {
        controlledEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get());
        inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update();
        Vector2 movement = inputManager.getMovement();

        for (Entity entity : controlledEntities) {
            VelocityComponent vel = vm.get(entity);
            SpeedComponent speed = sm.get(entity);
            AnimationComponent anim = am.get(entity);

            if (speed != null) {
                vel.velocity.set(movement).scl(speed.speed);
            } else {
                vel.velocity.set(movement).scl(100f);
            }

            if (anim.currentState == AnimationComponent.State.ATTACK) {
                anim.stateTime += Gdx.graphics.getDeltaTime();

                Animation<TextureAtlas.AtlasRegion> attackAnim = anim.animations.get(AnimationComponent.State.ATTACK);
                if (attackAnim.isAnimationFinished(anim.stateTime)) {
                    anim.currentState = inputManager.getState();
                    anim.stateTime = inputManager.getStateTime();
                }
                return;
            } else {
                anim.currentState = inputManager.getState();
                anim.stateTime = inputManager.getStateTime();
            }
        }
    }
}
