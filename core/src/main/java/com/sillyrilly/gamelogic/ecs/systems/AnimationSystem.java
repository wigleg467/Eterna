package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
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

    public AnimationSystem() {
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(FacingComponent.class, BodyComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            FacingComponent facing = fm.get(entity);
            BodyComponent body = bc.get(entity);
            Vector2 movement = body.body.getLinearVelocity();


            if (movement.x > 0.1f) {
                facing.facingRight = true;
            } else if (movement.x < -0.1f) {
                facing.facingRight = false;
            }

            // --------- PLAYER ---------
            if (entity.getComponent(PlayerComponent.class) != null) {
                AnimationButtomComponent bottom = abc.get(entity);
                AnimationTopComponent top = atc.get(entity);


                AnimationTopComponent.TopState topInputState = InputManager.instance.getTopState();
                AnimationButtomComponent.BottomState bottomInputState = InputManager.instance.getBottomState();
                WeaponComponent.WeaponType weapon = InputManager.instance.getCurrentWeapon();

                // --- TOP STATE ---

                if (top.currentState == AnimationTopComponent.TopState.ATTACK) {
                    top.stateTime += deltaTime;
                    bottom.stateTime += deltaTime;

                    Animation<TextureAtlas.AtlasRegion> attackAnim = top.getAnimation(weapon, AnimationTopComponent.TopState.ATTACK);
                    if (attackAnim != null && attackAnim.isAnimationFinished(top.stateTime)) {
                        top.currentState = topInputState;
                        top.stateTime = 0f;
                        InputManager.instance.setCanAttack(true);
                    }
                } else {
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

                Animation<TextureAtlas.AtlasRegion> currentTopAnim = top.getAnimation(weapon, top.currentState);
                Animation<TextureAtlas.AtlasRegion> currentBottomAnim = bottom.animations.get(bottom.currentState);


                TextureAtlas.AtlasRegion topCurrentFrame = currentTopAnim.getKeyFrame(top.stateTime, true);
                TextureAtlas.AtlasRegion bottomCurrentFrame = currentBottomAnim.getKeyFrame(bottom.stateTime, true);

                // --- FLIPPING ---
                if (!facing.facingRight) {
                    if (!topCurrentFrame.isFlipX()) topCurrentFrame.flip(true, false);
                    if (!bottomCurrentFrame.isFlipX()) bottomCurrentFrame.flip(true, false);
                } else {
                    if (topCurrentFrame.isFlipX()) topCurrentFrame.flip(true, false);
                    if (bottomCurrentFrame.isFlipX()) bottomCurrentFrame.flip(true, false);
                }

                top.currentFrame = topCurrentFrame;
                bottom.currentFrame = bottomCurrentFrame;
            }

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
                if (anim.currentState == AnimationComponent.State.WALK)
                    anim.currentState = AnimationComponent.State.IDLE;
                Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
                if (currentAnim == null) continue;

                anim.currentFrame = currentAnim.getKeyFrame(anim.stateTime, true);
                anim.stateTime += deltaTime;
            }
        }
    }
}
