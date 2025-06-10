package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.gamelogic.ecs.components.*;

import java.util.EnumMap;
import java.util.Map;

public class EntityFactory implements Disposable {
    private final Engine engine;
    private final Map<EntityType, Texture> textureMap = new EnumMap<>(EntityType.class);

    public enum EntityType {
        PLAYER("animations/player.atlas", 300f),
        ENEMY("animations/player.atlas", 100f),
        NPC("animations/player.atlas", 100f);


        private final String animationPath;
        private final float speed;

        EntityType(String animationPath, float speed) {
            this.animationPath=animationPath;
            this.speed = speed;
        }

        public String getAnimationPath() {
            return animationPath;
        }


        public float getSpeed() {
            return speed;
        }
    }

    public EntityFactory(Engine engine) {
        this.engine = engine;
    }

    public Entity createEntity(EntityType type, float x, float y) {
        Entity entity = new Entity();

        PositionComponent pos = new PositionComponent();
        pos.position.set(x, y);

        VelocityComponent vel = new VelocityComponent();
        vel.velocity.set(1f, 1f);

        SpeedComponent speed = new SpeedComponent(type.getSpeed());
        Animation<TextureAtlas.AtlasRegion>  idleAnimation=createIdleAnimation(type);
        Animation<TextureAtlas.AtlasRegion>  walkAnimation=createWalkAnimation(type);
        Animation<TextureAtlas.AtlasRegion>  attackAnimation=createAtackAnimation(type);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put(AnimationComponent.State.IDLE, idleAnimation);
        anim.animations.put(AnimationComponent.State.WALK, walkAnimation);
        anim.animations.put(AnimationComponent.State.ATTACK, attackAnimation);
        anim.currentState = AnimationComponent.State.IDLE;


        entity.add(pos);
        entity.add(vel);
        entity.add(speed);
        entity.add(anim);
        entity.add(new FacingComponent());

        if (EntityType.NPC == type || EntityType.PLAYER == type) {
            entity.add(new CameraFollowableComponent());

            if (EntityType.PLAYER == type) {
                entity.add(new CameraTargetComponent());
                Gdx.app.log("Create", "Player");
            }
        }

        engine.addEntity(entity);
        return entity;
    }

    private Animation<TextureAtlas.AtlasRegion>  createWalkAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("walk_right");
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }
    private Animation<TextureAtlas.AtlasRegion>  createIdleAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("idle");
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }
    private Animation<TextureAtlas.AtlasRegion>  createAtackAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("attack");
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    public void dispose() {
        for (Texture tex : textureMap.values()) {
            tex.dispose();
        }
        textureMap.clear();
    }
}
