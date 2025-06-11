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
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<FacingComponent> fc = ComponentMapper.getFor(FacingComponent.class);

    private InputManager inputManager;

    private ImmutableArray<Entity> controlledEntities;

    @Override
    public void addedToEngine(Engine engine) {
        controlledEntities = engine.getEntitiesFor(Family.all(BodyComponent.class, AnimationComponent.class, FacingComponent.class).get());
        inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update();
        Vector2 movement = inputManager.getMovement();

        for (Entity entity : controlledEntities) {
            BodyComponent body = bc.get(entity);
            AnimationComponent anim = am.get(entity);
            FacingComponent facing = fc.get(entity);

            body.getBody().setLinearVelocity(movement.scl(7f));;

            if (movement.x > 0) {
                facing.facingRight = true;
            } else if (movement.x < 0) {
                facing.facingRight = false;
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
