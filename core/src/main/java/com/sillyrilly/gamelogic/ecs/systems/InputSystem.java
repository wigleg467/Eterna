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
    private ComponentMapper<SpeedComponent> sm = ComponentMapper.getFor(SpeedComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);

private InputManager inputManager;

    private ImmutableArray<Entity> controlledEntities;

    @Override
    public void addedToEngine(Engine engine) {
        controlledEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get());
    inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        InputManager.getInstance().update();
        Vector2 movement = InputManager.getInstance().getMovementDirection();

        for (Entity entity : controlledEntities) {
            VelocityComponent vel = vm.get(entity);
            SpeedComponent speed = sm.get(entity);
            AnimationComponent anim = am.get(entity);
            FacingComponent facing = fm.get(entity);

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
