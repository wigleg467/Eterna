package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.sillyrilly.gamelogic.ecs.components.*;

import java.util.EnumMap;
import java.util.Map;

public class EntityFactory implements Disposable {
    public final static float PPM = 32f;

    private final Engine engine;
    private final World world;
    private final Map<EntityType, Texture> textureMap = new EnumMap<>(EntityType.class);

    public enum EntityType {
        PLAYER("animations/player.atlas"),
        ENEMY("animations/player.atlas"),
        NPC("animations/player.atlas");

        private final String animationPath;

        EntityType(String animationPath) {
            this.animationPath = animationPath;
        }

        public String getAnimationPath() {
            return animationPath;
        }
    }

    public EntityFactory(Engine engine, World world) {
        this.engine = engine;
        this.world = world;
    }

    public Entity createPlayer(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.linearDamping = 3f; // опір
        bodyDef.awake = true;
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10, 4); // треба потикати

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;  // вага тіла
        fixtureDef.friction = 0f;  // тертя із землею (max 1f)
        fixtureDef.restitution = 0f; // пружність (відскок)

        body.createFixture(fixtureDef);
        shape.dispose();

        Entity entity = new Entity();

        BodyComponent bc = new BodyComponent(body);

        Animation<TextureAtlas.AtlasRegion> idleAnimation = createIdleAnimation(EntityType.PLAYER);
        Animation<TextureAtlas.AtlasRegion> walkAnimation = createWalkAnimation(EntityType.PLAYER);
        Animation<TextureAtlas.AtlasRegion> attackAnimation = createAtackAnimation(EntityType.PLAYER);

        AnimationComponent anim = new AnimationComponent();
        anim.animations.put(AnimationComponent.State.IDLE, idleAnimation);
        anim.animations.put(AnimationComponent.State.WALK, walkAnimation);
        anim.animations.put(AnimationComponent.State.ATTACK, attackAnimation);
        anim.currentState = AnimationComponent.State.IDLE;

        entity.add(bc);
        entity.add(anim);
        entity.add(new FacingComponent());
        entity.add(new CameraFollowableComponent());
        entity.add(new CameraTargetComponent());

        engine.addEntity(entity);

        Gdx.app.log("Create", "Player");

        return entity;
    }

    // !!! У розробці !!!
    public void createCollisionEntity(TiledMap map) {
        MapObjects objects = map.getLayers().get("Collision").getObjects(); // шар з колізією

        for (MapObject object : objects) {
            if (object instanceof RectangleMapObject rectObject) {
                Rectangle rect = rectObject.getRectangle();

                // Створення BodyDef
                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;

                // Центр прямокутника
                float x = (rect.x + rect.width / 2f) / PPM;
                float y = (rect.y + rect.height / 2f) / PPM;
                bodyDef.position.set(x, y);

                // Створення тіла
                Body body = world.createBody(bodyDef);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rect.width / 2f / PPM, rect.height / 2f / PPM);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.friction = 0.5f;
                body.createFixture(fixtureDef);
                shape.dispose();

                // Додаємо в ECS
                Entity entity = new Entity();

                BodyComponent bc = new BodyComponent(body);

                entity.add(bc);
                engine.addEntity(entity);
            }
        }
    }

    private Animation<TextureAtlas.AtlasRegion> createWalkAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("walk_right");
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    private Animation<TextureAtlas.AtlasRegion> createIdleAnimation(EntityType type) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions("idle");
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    private Animation<TextureAtlas.AtlasRegion> createAtackAnimation(EntityType type) {
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
