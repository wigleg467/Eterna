package com.sillyrilly.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.entities.EntityFactory;
import com.sillyrilly.gamelogic.ecs.systems.*;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.ai.NavigationMap;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import java.util.Arrays;

import static com.sillyrilly.managers.AssetsManager.*;
import static com.sillyrilly.util.Const.*;

public class GameScreen implements Screen {
    private OrthographicCamera camera;

    private Engine engine;
    private World world;

    private float zoom = 0.6f;
    private boolean initialized = false;

    @Override
    public void show() {
        if (!initialized) {
            camera = CameraManager.camera;

            new TileGraph(new NavigationMap(realWorld).grid);
            world = new World(new Vector2(0, 0), true);
            engine = new Engine();

            parseMaps();
            initFactory();
            addSystems();

            initialized = true;
        }

        camera.zoom = zoom;
        Gdx.input.setCursorCatched(true);
        InputManager.multiplexer.addProcessor(InputManager.instance);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);

        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        CameraManager.instance.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        zoom = camera.zoom;
        Gdx.input.setCursorCatched(false);
        InputManager.multiplexer.removeProcessor(InputManager.instance);
    }

    @Override
    public void dispose() {
        realWorld.dispose();
        hell.dispose();
        heaven.dispose();
        world.dispose();
    }

    private void parseMaps() {
        Box2DMapObjectParser parser = new Box2DMapObjectParser(TILE_SCALE);
        parser.load(world, realWorld);
        parser.load(world, heaven);
        parser.load(world, hell);
    }

    private void initFactory() {
        EntityFactory factory = new EntityFactory(engine, world);

        factory.createPlayer(300f, 300f, 3);
        factory.createTileLayer(realWorld, "props", 3, 0, 0);
        factory.createTileLayer(realWorld, "base", 0, 0, 0);
        factory.createTileLayer(realWorld, "base2", 2, 0, 0);
        factory.createTileLayer(realWorld, "landscape", 1, 0, 0);
        factory.createTileLayer(realWorld, "house", 3, 0, 0);
        factory.createObjectLayer(realWorld, "Collision_lvl_1", 0, 1, 0.01f, 0.01f, 0, 0);


//            factory.createTileLayer(hell, "props", 3, 0, -1);
//            factory.createTileLayer(hell, "base", 0, 0, -1);
//            factory.createTileLayer(hell, "lava", 1, 0, -1);
//            factory.createTileLayer(hell, "path", 2, 0, -1);
//            factory.createTileLayer(hell, "base2", 2, 0, -1);
//            factory.createTileLayer(hell, "web", 4, 0, -1);
//            factory.createObjectLayer(hell, "Collision_lvl_2", 1, 1, 0.01f, 0.01f, 0, -1);
//            factory.createObjectLayer(hell, "Enemies", 1, 1, 0.01f, 0.01f, 0, -1);
//
//            factory.createTileLayer(heaven, "props", 3, 0, 1);
//            factory.createTileLayer(heaven, "base", 0, 0, 1);
//            factory.createTileLayer(heaven, "base2", 1, 0, 1);
//            factory.createTileLayer(heaven, "base3", 2, 0, 1);
//            factory.createTileLayer(heaven, "base4", 2, 0, 1);
//            factory.createTileLayer(heaven, "castle", 5, 0, 1);
//            factory.createTileLayer(heaven, "clouds", 4, 0, 1);
//            factory.createObjectLayer(heaven, "Collision_lvl_3", 1, 1, 0.01f, 0.01f, 0, 1);
//            factory.createObjectLayer(heaven, "Enemies", 1, 1, 0.01f, 0.01f, 0, 1);

        MapObjects objects = realWorld.getLayers().get("Enemies").getObjects();
        for (MapObject object : objects) {
            Gdx.app.log("GameScreen", object.toString());
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            String typeStr = object.getProperties().get("type", String.class);
            if (typeStr == null) continue;
            EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
            factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 3, 0, 0);
        }

//            objects = heaven.getLayers().get("Enemies").getObjects();
//            for (MapObject object : objects) {
//                Gdx.app.log("GameScreen", object.toString());
//                Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                String typeStr = object.getProperties().get("type", String.class);
//                if (typeStr == null) continue;
//                EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
//                factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 1, 0, 1);
//            }
//
//            objects = hell.getLayers().get("Enemies").getObjects();
//            for (MapObject object : objects) {
//                Gdx.app.log("GameScreen", object.toString());
//                Rectangle rect = ((RectangleMapObject) object).getRectangle();
//                String typeStr = object.getProperties().get("type", String.class);
//                if (typeStr == null) continue;
//                EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
//                factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 1, 0, -1);
//            }

    }

    private void addSystems() {
        engine.addSystem(new InputSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new CameraFollowSystem());

        engine.addSystem(new EnemyPathfindingSystem());
        engine.addSystem(new AISystem());

        engine.addSystem(new AnimationSystem());

        engine.addSystem(new RenderSystem());
    }
}
