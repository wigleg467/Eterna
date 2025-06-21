package com.sillyrilly.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.sillyrilly.gamelogic.ecs.ai.NavigationMap;
import com.sillyrilly.gamelogic.ecs.entities.EntityFactory;
import com.sillyrilly.gamelogic.ecs.systems.*;
import com.sillyrilly.gamelogic.ecs.utils.DialogueStorage;
import com.sillyrilly.gamelogic.ecs.utils.DialogueWindow;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import static squidpony.squidgrid.gui.gdx.SColor.DARK_RED;

public class GameScreen implements Screen {
    public static GameScreen instance;

    private static final float tileScale = 1f / 32f;


    private Box2DMapObjectParser parser;
    private CameraManager cameraManager = CameraManager.getInstance();
    public Engine engine;
    private EntityFactory factory;
    public static OrthogonalTiledMapRenderer renderer;
    private SpriteBatch batch;
    private TiledMap realWorld;
    private TiledMap hell;
    private TiledMap heaven;
    private World world;
    private Skin skin;
    private Window.WindowStyle style;
    public DialogueWindow dialogueWindow;
    private BitmapFont customFont;
    private float zoom = 0.6f;
    private boolean initialized = false;

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        if (!initialized) {
instance = this;
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter paramCustom = new FreeTypeFontGenerator.FreeTypeFontParameter();
            paramCustom.size = 20;
            paramCustom.characters = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ" +
                    "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя" +
                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?.,:;(){}[]«»–-";

            customFont = generator.generateFont(paramCustom);
            customFont.getData().markupEnabled = true;

            generator.dispose();
            skin = new Skin(Gdx.files.internal("uiskin.json"));
            style = new Window.WindowStyle(skin.get("default", Window.WindowStyle.class));
            style.background = skin.newDrawable("white", DARK_RED);
            realWorld = new TmxMapLoader().load("maps/firstlevel.tmx");
            hell = new TmxMapLoader().load("maps/secondlevel.tmx");
            heaven = new TmxMapLoader().load("maps/third_level.tmx");
            //   renderer = new OrthogonalTiledMapRenderer(heaven);
            //   renderer.setView(cameraManager.getCamera());

            world = new World(new Vector2(0, 0), true);
            parser = new Box2DMapObjectParser(tileScale);
            parser.load(world, realWorld);
            parser.load(world, heaven);
            parser.load(world, hell);

            {
                //     float mapWidth = heaven.getProperties().get("width", Integer.class);
                //     float mapHeight = heaven.getProperties().get("height", Integer.class);
                //     float tileWidth = heaven.getProperties().get("tilewidth", Integer.class);
                //     float tileHeight = heaven.getProperties().get("tileheight", Integer.class);

                //     float centerX = mapWidth * tileWidth / 2f;
                //     float centerY = mapHeight * tileHeight / 2f;

                batch = new SpriteBatch();

                engine = new Engine();
                engine.addSystem(new InputSystem());
                engine.addSystem(new MovementSystem());
                engine.addSystem(new EnemyAISystem());
                engine.addSystem(new CameraFollowSystem());
                engine.addSystem(new AISystem());
                engine.addSystem(new EnemyPathfindingSystem(new NavigationMap(realWorld).getGrid()));
                factory = new EntityFactory(engine, world);
                factory.createPlayer(300f, 300f, 3);
                dialogueWindow = new DialogueWindow(new Texture("images/dialogue.png"), customFont);
                engine.addSystem(new InteractionSystem(dialogueWindow));
                engine.addSystem(new AnimationSystem());
                engine.addSystem(new RenderSystem(batch));
                DialogueStorage.init();



            }


            factory.createTileLayer(realWorld, "props", 3, 0, 0);
            factory.createTileLayer(realWorld, "base", 0, 0, 0);
            factory.createTileLayer(realWorld, "base2", 1, 0, 0);
            factory.createTileLayer(realWorld, "landscape", 2, 0, 0);
            factory.createTileLayer(realWorld, "house", 3, 0, 0);
            factory.createObjectLayer(realWorld, "Collision_lvl_1", 1, 1, 0.01f, 0.01f, 0, 0);


            factory.createTileLayer(hell, "props", 3, 0, -1);
            factory.createTileLayer(hell, "base", 0, 0, -1);
            factory.createTileLayer(hell, "lava", 1, 0, -1);
            factory.createTileLayer(hell, "path", 2, 0, -1);
            factory.createTileLayer(hell, "base2", 2, 0, -1);
            factory.createTileLayer(hell, "web", 4, 0, -1);
            factory.createObjectLayer(hell, "Collision_lvl_2", 1, 1, 0.01f, 0.01f, 0, -1);
            factory.createObjectLayer(hell, "Enemies", 1, 1, 0.01f, 0.01f, 0, -1);

            factory.createTileLayer(heaven, "props", 3, 0, 1);
            factory.createTileLayer(heaven, "base", 0, 0, 1);
            factory.createTileLayer(heaven, "base2", 1, 0, 1);
            factory.createTileLayer(heaven, "base3", 2, 0, 1);
            factory.createTileLayer(heaven, "base4", 2, 0, 1);
            factory.createTileLayer(heaven, "castle", 5, 0, 1);
            factory.createTileLayer(heaven, "clouds", 4, 0, 1);
            factory.createObjectLayer(heaven, "Collision_lvl_3", 1, 1, 0.01f, 0.01f, 0, 1);
            factory.createObjectLayer(heaven, "Enemies", 1, 1, 0.01f, 0.01f, 0, 1);


            MapObjects objects = realWorld.getLayers().get("Enemies").getObjects();
            for (MapObject object : objects) {
                Gdx.app.log("GameScreen", object.toString());
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String typeStr = object.getProperties().get("type", String.class);
                if (typeStr == null) continue;
                EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
                factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 3, 0, 0);
            }


            objects = realWorld.getLayers().get("NPC").getObjects();
            for (MapObject object : objects) {
                Gdx.app.log("GameScreen", object.toString());
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String typeStr = object.getProperties().get("type", String.class);
                if (typeStr == null) continue;
                NPCType type = NPCType.valueOf(typeStr.toUpperCase());
                factory.createNPC(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 3, 0, 0);
            }


            objects = heaven.getLayers().get("Enemies").getObjects();
            for (MapObject object : objects) {
                Gdx.app.log("GameScreen", object.toString());
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String typeStr = object.getProperties().get("type", String.class);
                if (typeStr == null) continue;
                EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
                factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 1, 0, 1);
            }

            objects = hell.getLayers().get("Enemies").getObjects();
            for (MapObject object : objects) {
                Gdx.app.log("GameScreen", object.toString());
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                String typeStr = object.getProperties().get("type", String.class);
                if (typeStr == null) continue;
                EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
                factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 1, 0, -1);
            }


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
        heaven.dispose();
        //   renderer.dispose();
        batch.dispose();
        world.dispose();
    }
}
