package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.gamelogic.ecs.components.SpeedComponent;
import com.sillyrilly.gamelogic.ecs.components.VelocityComponent;
import com.sillyrilly.gamelogic.ecs.components.TextureComponent;

import java.util.EnumMap;
import java.util.Map;

public class EntityFactory implements Disposable {
    private final Engine engine;
    private final Map<EntityType, Texture> textureMap = new EnumMap<>(EntityType.class);

    public enum EntityType {
        PLAYER("images/entity/cat.png", 100f),
        ENEMY("images/entity/cat.png", 100f),
        NPC("images/entity/cat.png", 100f);

        private final String texturePath;
        private final float speed;

        EntityType(String texturePath,  float speed) {
            this.texturePath = texturePath;
            this.speed = speed;
        }

        public String getTexturePath() {
            return texturePath;
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
        vel.velocity.set(0, 0);

        SpeedComponent speed = new SpeedComponent(type.getSpeed());

        TextureComponent tex = new TextureComponent();
        tex.texture = getOrLoadTexture(type);

        entity.add(pos);
        entity.add(vel);
        entity.add(speed);
        entity.add(tex);

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

    public void dispose() {
        for (Texture tex : textureMap.values()) {
            tex.dispose();
        }
        textureMap.clear();
    }
}
