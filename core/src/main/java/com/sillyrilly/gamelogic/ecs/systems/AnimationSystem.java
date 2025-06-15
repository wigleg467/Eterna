package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.FacingComponent;
import com.sillyrilly.managers.InputManager;

public class AnimationSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);

    private ImmutableArray<Entity> entities;
    private InputManager inputManager;

    public AnimationSystem() {
//        this.inputManager = InputManager.getInstance();

    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AnimationComponent.class, FacingComponent.class).get());
        this.inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            AnimationComponent anim = am.get(entity);
            FacingComponent facing = fm.get(entity);

            BodyComponent body = bc.get(entity);
            Vector2 movement = body.body.getLinearVelocity();

            if (movement.x > 0.1f)
                facing.facingRight = true;
            else if (movement.x < -0.1f)
                facing.facingRight = false;

            AnimationComponent.State inputState = InputManager.getInstance().getState();

            // Якщо анімація атаки ще не завершена — не змінюємо стан
            if (anim.currentState == AnimationComponent.State.ATTACK) {
                anim.stateTime += deltaTime;

                Animation<TextureAtlas.AtlasRegion> attackAnim = anim.animations.get(AnimationComponent.State.ATTACK);
                if (attackAnim.isAnimationFinished(anim.stateTime)) {
                    // Атака завершена — повертаємось до поточного введеного стану (наприклад, WALK або IDLE)
                    anim.currentState = inputState;
                    anim.stateTime = 0f;
                }
            } else {
                // Якщо не атака — просто оновлюємо стан і таймер
                if (anim.currentState != inputState) {
                    anim.currentState = inputState;
                    anim.stateTime = 0f;
                } else {
                    anim.stateTime += deltaTime;
                }
            }

            Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
            TextureAtlas.AtlasRegion frame = currentAnim.getKeyFrame(anim.stateTime, true);

            if (facing != null && !facing.facingRight && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (facing != null && facing.facingRight && frame.isFlipX()) {
                frame.flip(true, false);
            }

            anim.currentFrame = frame;
        }
    }
}
