package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.managers.InputManager;

public class AnimationSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<AnimationButtomComponent> abc = ComponentMapper.getFor(AnimationButtomComponent.class);
    private final ComponentMapper<AnimationTopComponent> atc = ComponentMapper.getFor(AnimationTopComponent.class);

    private ImmutableArray<Entity> entities;
    private InputManager inputManager;

    public AnimationSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(FacingComponent.class, BodyComponent.class)
                .one(AnimationComponent.class, AnimationTopComponent.class, AnimationButtomComponent.class)
                .get());
        this.inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            FacingComponent facing = fm.get(entity);
            BodyComponent body = bc.get(entity);
            Vector2 movement = body.body.getLinearVelocity();

            if (movement.x > 0.1f)
                facing.facingRight = true;
            else if (movement.x < -0.1f)
                facing.facingRight = false;



            // --------- PLAYER ---------
            if (entity.getComponent(PlayerComponent.class) != null) {
                AnimationButtomComponent bottom = abc.get(entity);
                AnimationTopComponent top = atc.get(entity);
                AnimationTopComponent.TopState topInputState = InputManager.getInstance().getTopState();
                AnimationButtomComponent.BottomState bottomInputState = InputManager.getInstance().getBottomState();

                // --- TOP STATE ---

                if (top.currentState == AnimationTopComponent.TopState.ATTACK) {
                    top.stateTime += deltaTime;
                    bottom.stateTime += deltaTime;
                    Animation<TextureAtlas.AtlasRegion> attackAnim = top.animations.get(AnimationTopComponent.TopState.ATTACK);
                    if (attackAnim.isAnimationFinished(top.stateTime)) {
                        // Атака завершена — повертаємось до поточного введеного стану (наприклад, WALK або IDLE)
                        top.currentState = topInputState;
                        top.stateTime = 0f;
                        InputManager.getInstance().setCanAttack(true);
                    }
                } else {
                    // Якщо не атака — просто оновлюємо стан і таймер
                    if (top.currentState != topInputState) {
                        top.currentState = topInputState;
                        top.stateTime = 0f;
                    } else {
                        top.stateTime += deltaTime;
                    }
                    if (bottom.currentState != bottomInputState) {
                        bottom.currentState = bottomInputState;
                        bottom.stateTime = 0f;
                    } else {
                        bottom.stateTime += deltaTime;
                    }
                }

                // --- UPDATE FRAMES ---

                Animation<TextureAtlas.AtlasRegion> currentTopAnim = top.animations.get(top.currentState);
                TextureAtlas.AtlasRegion topCurrentFrame = currentTopAnim.getKeyFrame(top.stateTime, true);

                Animation<TextureAtlas.AtlasRegion> currentBottomAnim = bottom.animations.get(bottom.currentState);
                TextureAtlas.AtlasRegion bottomCurrentFrame = currentBottomAnim.getKeyFrame(bottom.stateTime, true);


                // --- FLIPPING ---
                if (!facing.facingRight) {
                    if (!topCurrentFrame.isFlipX()) topCurrentFrame.flip(true, false);
                    if (!bottomCurrentFrame.isFlipX()) bottomCurrentFrame.flip(true, false);
                } else {
                    if (topCurrentFrame.isFlipX()) topCurrentFrame.flip(true, false);
                    if (bottomCurrentFrame.isFlipX()) bottomCurrentFrame.flip(true, false);
                }
                Gdx.app.log("ANIM", "Top state: " + top.currentState + ", frames: " + top.animations.get(top.currentState).getKeyFrames().length);

                top.currentFrame = currentTopAnim.getKeyFrame(top.stateTime, true);
                bottom.currentFrame = currentBottomAnim.getKeyFrame(bottom.stateTime, true);}


            // --------- ENEMY ---------
            else if (entity.getComponent(EnemyComponent.class) != null) {
                AnimationComponent anim = am.get(entity);
                Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
                if (currentAnim == null) continue;

                TextureAtlas.AtlasRegion frame = currentAnim.getKeyFrame(anim.stateTime, true);
                anim.stateTime += deltaTime;

                if (!facing.facingRight && !frame.isFlipX()) frame.flip(true, false);
                if (facing.facingRight && frame.isFlipX()) frame.flip(true, false);

                anim.currentFrame = frame;
            }
            // --------- NPC ---------
            else if (entity.getComponent(NPCComponent.class) != null) {
                AnimationComponent anim = am.get(entity);
                Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
                if (currentAnim == null) continue;

                anim.currentFrame = currentAnim.getKeyFrame(anim.stateTime, true);
                anim.stateTime += deltaTime;
            }
        }
    }
}
