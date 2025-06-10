package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.gamelogic.ecs.components.*;

import java.util.EnumMap;
import java.util.Map;

public class EntityFactory implements Disposable {
    private final Engine engine;
    private final Map<EntityType, Texture> textureMap = new EnumMap<>(EntityType.class);

    public enum EntityType {
        PLAYER("images/entity/cat.png", "animations/walkingright.atlas", 300f),
        ENEMY("images/entity/cat.png", "animations/walkingright.atlas", 100f),
        NPC("images/entity/cat.png", "animations/walkingright.atlas", 100f);

        private final String texturePath;
        private final String animationPath;
        private final float speed;

        EntityType(String texturePath, String animationPath, float speed) {
            this.texturePath = texturePath;
            this.animationPath=animationPath;
            this.speed = speed;
        }

        public String getTexturePath() {
            return texturePath;
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

        TextureComponent tex = new TextureComponent();
        tex.texture = getOrLoadTexture(type);

        AnimationComponent animationComp = new AnimationComponent();
        animationComp.animation = createWalkAnimation(type);
        entity.add(animationComp);

        entity.add(pos);
        entity.add(vel);
        entity.add(speed);
        entity.add(tex);

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

    private Texture getOrLoadTexture(EntityType type) {
        if (!textureMap.containsKey(type)) {
            Texture tex = new Texture(Gdx.files.internal(type.getTexturePath()));
            textureMap.put(type, tex);
        }
        return textureMap.get(type);
    }

    private Animation<TextureRegion> createWalkAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(type.name().toLowerCase() + "_walk");

        return new Animation<>(0.2f, frames, Animation.PlayMode.LOOP);
    }

    public void dispose() {
        for (Texture tex : textureMap.values()) {
            tex.dispose();
        }
        textureMap.clear();
    }
}
