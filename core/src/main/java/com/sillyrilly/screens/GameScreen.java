package com.sillyrilly.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sillyrilly.gamelogic.ecs.entities.EntityFactory;
import com.sillyrilly.gamelogic.ecs.systems.*;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

public class GameScreen implements Screen {
    private static final float tileScale = 1f / 32f;

    private Box2DMapObjectParser parser;
    private CameraManager cameraManager = CameraManager.getInstance();
    private Engine engine;
    private EntityFactory factory;
    public static OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private TiledMap map;
    private World world;

    private float zoom = 0.6f;
    private boolean initialized = false;

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        if (!initialized) {
            map = new TmxMapLoader().load("maps/firstlevel.tmx");

            world = new World(new Vector2(0, 0), true);
            parser = new Box2DMapObjectParser(tileScale);
            parser.load(world, map);

            {
                //     float mapWidth = map.getProperties().get("width", Integer.class);
                //     float mapHeight = map.getProperties().get("height", Integer.class);
                //     float tileWidth = map.getProperties().get("tilewidth", Integer.class);
                //     float tileHeight = map.getProperties().get("tileheight", Integer.class);

                //     float centerX = mapWidth * tileWidth / 2f;
                //     float centerY = mapHeight * tileHeight / 2f;

                batch = new SpriteBatch();

                engine = new Engine();
                engine.addSystem(new InputSystem());
                engine.addSystem(new MovementSystem());
                engine.addSystem(new AnimationSystem());
                engine.addSystem(new CameraFollowSystem());
                engine.addSystem(new RenderSystem(batch));

                factory = new EntityFactory(engine, world);
            }

            factory.createPlayer(300f, 300f, 3);

            factory.createTileLayer(map, "base", 0);
          //  factory.createTileLayer(map, "base2", 1);
          //  factory.createTileLayer(map, "landscape", 2);
          //  factory.createTileLayer(map, "house", 3);
          //
             //  factory.createTileLayer(map, "props", 3);

                 factory.createObjectLayer(map, "Collosion land", 3, 1, 1, 1);
            //    factory.createObjectLayer(map, "Little platform", 0);

            //   factory.createTileLayer(map, "props", 1);
            //v factory.createTileLayer(map, "Collision", 1, 1, 0.01f, 0.01f);
            initialized = true;
        }

        cameraManager.getCamera().zoom = zoom;
        InputManager.getInstance().getMultiplexer().addProcessor(InputManager.getInstance());
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);

        engine.update(delta);
    }

    /**
     * @param width
     * @param height
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {
        zoom = CameraManager.getInstance().getCamera().zoom;
        InputManager.getInstance().getMultiplexer().removeProcessor(InputManager.getInstance());
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        map.dispose();
        //   renderer.dispose();
        batch.dispose();
        world.dispose();
    }
}
