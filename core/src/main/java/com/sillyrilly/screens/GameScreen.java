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
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;
import com.sillyrilly.gamelogic.ecs.systems.InputSystem;
import com.sillyrilly.gamelogic.ecs.systems.MovementSystem;
import com.sillyrilly.gamelogic.ecs.systems.RenderSystem;
import com.sillyrilly.managers.CameraManager;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;


public class GameScreen implements Screen {
    private Engine engine;
    private SpriteBatch batch;
    private EntityFactory factory;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private World world;
    private Box2DMapObjectParser parser;
    float tileScale = 1f / 32f;

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("maps/test-map.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        renderer.setView(CameraManager.getInstance().getCamera());

        float mapWidth = map.getProperties().get("width", Integer.class);
        float mapHeight = map.getProperties().get("height", Integer.class);
        float tileWidth = map.getProperties().get("tilewidth", Integer.class);
        float tileHeight = map.getProperties().get("tileheight", Integer.class);

        world = new World(new Vector2(0, 0), true);
         parser = new Box2DMapObjectParser(tileScale);
        parser.load(world, map);

        float centerX = mapWidth * tileWidth / 2f;
        float centerY = mapHeight * tileHeight / 2f;

        batch = new SpriteBatch();
        engine = new Engine();

        engine.addSystem(new MovementSystem());
        engine.addSystem(new RenderSystem(batch));
        engine.addSystem(new InputSystem());
        engine.addSystem(new CameraFollowSystem(CameraManager.getInstance()));

        factory = new EntityFactory(engine);

        factory.createEntity(EntityFactory.EntityType.PLAYER, centerX, centerY);
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
        CameraManager.getInstance().getCamera().update();
        renderer.setView(CameraManager.getInstance().getCamera());
        renderer.render();

        float stateTime = 0f;





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
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        batch.dispose();
        factory.dispose();
    }
}
