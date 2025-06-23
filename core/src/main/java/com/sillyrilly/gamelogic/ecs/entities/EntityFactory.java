package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.utils.EntityType;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;

import static com.sillyrilly.util.Const.PPM;

public class EntityFactory {
    private final Engine engine;
    private final World world;

    public EntityFactory(Engine engine, World world) {
        this.engine = engine;
        this.world = world;
    }

    public void createPlayer(float x, float y, int lvl) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x / PPM, y / PPM);
        bodyDef.linearDamping = 0f; // опір
        bodyDef.awake = true;
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.0625f); // треба потикати

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //   fixtureDef.density = 0f;  // вага тіла
        //   fixtureDef.friction = 0f;  // тертя із землею (max 1f)
        //   fixtureDef.restitution = 0f; // пружність (відскок)

        body.createFixture(fixtureDef);
        shape.dispose();

        Entity entity = new Entity();

        entity.add(new BodyComponent(body));
        entity.add(new FacingComponent());
        entity.add(new CameraFollowableComponent());
        entity.add(new CameraTargetComponent());
        entity.add(new PlayerComponent());
        entity.add(new LevelComponent(lvl));
        entity.add(new AnimationButtomComponent(EntityType.PLAYER, "bottom_idle", "bottom_walk_right"));
        entity.add(new AnimationTopComponent(EntityType.PLAYER));
        entity.add(new WeaponComponent(WeaponComponent.WeaponType.SWORD));
        entity.add(new HealComponent(100));

        engine.addEntity(entity);

        Gdx.app.log("Create", "Player");
    }

    public void createEnemy(float x, float y, EnemyType type, int lvl, String location) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x) / PPM, (y) / PPM);
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(type.getWidth() / 2f, type.getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //   fixtureDef.density = 1f;
        //   fixtureDef.friction = 0.5f;
        //   fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();

        Entity entity = new Entity();
        entity.add(new AnimationComponent(type, "", "walk_right", ""));
        entity.add(new BodyComponent(body));
        entity.add(new EnemyComponent(type));
        entity.add(new FacingComponent());
        entity.add(new PathComponent());
        entity.add(new LevelComponent(lvl));
        entity.add(new EnemyComponent(type));
        entity.add(new HealComponent(100));

        entity.add(new LocationComponent(location));

        AIComponent ai = new AIComponent();
        ai.stateMachine = new DefaultStateMachine<>(entity, EnemyState.IDLE);
        entity.add(ai);

        AttackComponent ac = new AttackComponent();
        ac.damage = 5;
        ac.attackCooldown = 1.5f;
        entity.add(ac);

        engine.addEntity(entity);
    }

    public void createNPC(float x, float y, NPCType type, int lvl) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x) / PPM, (y) / PPM);
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(type.getWidth() / 2f, type.getHeight() / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        //   fixtureDef.density = 1f;
        //   fixtureDef.friction = 0.5f;
        //   fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();

        Entity entity = new Entity();
        entity.add(new AnimationComponent(type, "idle", "", "", "default"));
        entity.add(new BodyComponent(body));
        entity.add(new NPCComponent(type));
        entity.add(new InteractableComponent());
        entity.add(new FacingComponent());
        entity.add(new LevelComponent(lvl));

        engine.addEntity(entity);

        Gdx.app.log("Create", "NPC");
    }

    public void createTileLayer(TiledMap map, String layerName, int lvl) {
        createTileLayer(map, layerName, lvl, 0f, 0f, 0f);
    }

    public void createTileLayer(TiledMap map, String layerName, int lvl, float density, float friction, float restitution) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell == null || cell.getTile() == null) continue;

                TiledMapTile tile = cell.getTile();
                TextureRegion region = tile.getTextureRegion();

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((x), (y)); // Центр тайла

                Body body = world.createBody(bodyDef);

                if (density != 0 && friction != 0 && restitution != 0) {
                    PolygonShape shape = new PolygonShape();
                    float tileWidth = region.getRegionWidth() / PPM;
                    float tileHeight = region.getRegionHeight() / PPM;
                    shape.setAsBox(tileWidth / 2f, tileHeight / 2f);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = density;
                    fixtureDef.friction = friction;
                    fixtureDef.restitution = restitution;

                    body.createFixture(fixtureDef);
                    shape.dispose();
                }

                Entity entity = new Entity();

                BodyComponent bc = new BodyComponent(body);
                LevelComponent lc = new LevelComponent(lvl);
                TileComponent tc = new TileComponent(cell.getTile());

                entity.add(bc);
                entity.add(lc);
                entity.add(tc);
                engine.addEntity(entity);
            }
        }
    }

    public void createObjectLayer(TiledMap map, String layerName, int lvl) {
        createObjectLayer(map, layerName, lvl, 0f, 0f, 0f);
    }

    public void createObjectLayer(TiledMap map, String layerName, int lvl, float density, float friction, float restitution) {
        MapObjects objects = map.getLayers().get(layerName).getObjects(); // шар з колізією

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
                if (density != 0 && friction != 0 && restitution != 0) {
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(rect.width / 2f / PPM, rect.height / 2f / PPM);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = density;
                    fixtureDef.friction = friction;
                    fixtureDef.restitution = restitution;

                    body.createFixture(fixtureDef);
                    shape.dispose();
                }
                // Додаємо в ECS
                Entity entity = new Entity();

                BodyComponent bc = new BodyComponent(body);
                LevelComponent lc = new LevelComponent(lvl);

                entity.add(bc);
                entity.add(lc);
                engine.addEntity(entity);
            }
        }
    }
}
