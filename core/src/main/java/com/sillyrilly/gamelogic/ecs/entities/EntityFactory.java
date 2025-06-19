package com.sillyrilly.gamelogic.ecs.entities;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.utils.EntityType;

public class EntityFactory {
    public final static float PPM = 32f;
    public final static float TILE_SIZE = 32f;

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


        BodyComponent bc = new BodyComponent(body);
        entity.add(new AnimationComponent(EntityType.PLAYER, "idle", "walk_right", "attack"));
        entity.add(bc);
        entity.add(new FacingComponent());
        entity.add(new CameraFollowableComponent());
        entity.add(new CameraTargetComponent());
        entity.add(new LevelComponent(lvl));
        entity.add(new PlayerComponent(bc));

        engine.addEntity(entity);

        Gdx.app.log("Create", "Player");

    }

    public void createEnemy(float x, float y, EnemyType type, int lvl, float offsetX, float offsetY) {
        offsetY*=1216;
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((x + offsetX) / PPM, (y + offsetY) / PPM);
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
        entity.add(new LevelComponent(lvl));
        entity.add(new EnemyComponent(type));

        AIComponent ai = new AIComponent();
        ai.stateMachine = new DefaultStateMachine<>(entity, EnemyState.IDLE);
        entity.add(ai);

        engine.addEntity(entity);
    }

    public void createTileLayer(TiledMap map, String layerName, int lvl) {
        createTileLayer(map, layerName, lvl, 0f, 0f, 0f, 0, 0);
    }

    public void createTileLayer(TiledMap map, String layerName, int level, float offsetXTiles, float offsetYTiles) {
        createTileLayer(map, layerName, level, 0f, 0f, 0f, offsetXTiles, offsetYTiles);
    }

    public void createTileLayer(TiledMap map, String layerName, int level,
                                float density, float friction, float restitution,
                                float offsetXTiles, float offsetYTiles) {


        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
        if (layer == null) return;

        float offsetX = offsetXTiles * layer.getTileWidth() / EntityFactory.PPM;
        float offsetY = offsetYTiles * layer.getTileHeight() * layer.getHeight() / EntityFactory.PPM;

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell == null || cell.getTile() == null) continue;

                TiledMapTile tile = cell.getTile();
                TextureRegion region = tile.getTextureRegion();

                float tileWidth = region.getRegionWidth() / EntityFactory.PPM;
                float tileHeight = region.getRegionHeight() / EntityFactory.PPM;

                float posX = (x + 0.5f) * tileWidth + offsetX;
                float posY = (y + 0.5f) * tileHeight + offsetY;

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(posX, posY);

                Body body = world.createBody(bodyDef);

                if (density != 0 || friction != 0 || restitution != 0) {
                    PolygonShape shape = new PolygonShape();
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
                entity.add(new BodyComponent(body));
                entity.add(new LevelComponent(level));
                entity.add(new TileComponent(tile));
                engine.addEntity(entity);
            }
        }
    }

    public void createObjectLayer(TiledMap map, String layerName, int lvl) {
        createObjectLayer(map, layerName, lvl, 0f, 0f, 0f, 0, 0);
    }

    public void createObjectLayer(TiledMap map, String layerName, int level,
                                  float density, float friction, float restitution,
                                  float offsetXTiles, float offsetYTiles) {
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) return;

        float offsetX = offsetXTiles * TILE_SIZE / PPM;
        float offsetY = offsetYTiles * TILE_SIZE * map.getProperties().get("height", Integer.class) / PPM;

        for (MapObject object : layer.getObjects()) {
            if (!(object instanceof RectangleMapObject rectObject)) continue;

            Rectangle rect = rectObject.getRectangle();
            float x = (rect.x + rect.width / 2f) / PPM + offsetX;
            float y = (rect.y + rect.height / 2f) / PPM + offsetY;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(x, y);

            Body body = world.createBody(bodyDef);

            if (density != 0 || friction != 0 || restitution != 0) {
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

            Entity entity = new Entity();
            entity.add(new BodyComponent(body));
            entity.add(new LevelComponent(level));
            engine.addEntity(entity);
        }
    }
}
