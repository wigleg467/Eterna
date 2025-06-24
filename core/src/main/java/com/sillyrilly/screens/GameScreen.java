package com.sillyrilly.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sillyrilly.gamelogic.ecs.ai.NavigationMap;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.entities.EntityFactory;
import com.sillyrilly.gamelogic.ecs.systems.*;
import com.sillyrilly.gamelogic.ecs.utils.DialogueWindow;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.utils.HintRenderer;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;
import com.sillyrilly.managers.AudioManager;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

import java.util.HashMap;
import java.util.Map;

import static com.sillyrilly.gamelogic.ecs.utils.GameState.*;
import static com.sillyrilly.managers.AssetsManager.bigWorld;
import static com.sillyrilly.managers.CameraManager.viewport;
import static com.sillyrilly.managers.FontManager.GAME_hoverFont;
import static com.sillyrilly.managers.FontManager.MENU_hoverFont;
import static com.sillyrilly.managers.ScreenManager.batch;
import static com.sillyrilly.util.Const.PPM;
import static com.sillyrilly.util.Const.TILE_SCALE;

public class GameScreen implements Screen {
    public static GameScreen instance;

    private final Map<String, Body> removableBodies = new HashMap<>();
    public DialogueWindow dialogueWindow;
    private OrthographicCamera camera;
    public Engine engine;
    private World world;
    private float zoom = 0.6f;
    public boolean initialized = false;
    public HintRenderer hintRenderer;
    public Stage stage;

    @Override
    public void show() {
        if (!initialized) {
            instance = this;
            camera = CameraManager.camera;
            stage = new Stage(viewport, batch);
            Gdx.input.setInputProcessor(stage);
            new TileGraph(new NavigationMap(bigWorld).grid);
            world = new World(new Vector2(0, 0), true);
            engine = new Engine();

            parseMaps();
            initFactory();
            addSystems();

            initRemovableCollision(bigWorld.getLayers().get("RemovableCollision"));

            initialized = true;
        }

        camera.zoom = zoom;
        Gdx.input.setCursorCatched(true);

        if (!hell) AudioManager.instance.playMusic(AudioManager.MusicType.MAIN_THEME);
        else AudioManager.instance.playMusic(AudioManager.MusicType.HELL);

        InputManager.multiplexer.addProcessor(InputManager.instance);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(delta, 6, 2);

        if (talkedToNun)
            removeBlock("cemetery_block");
        if (gotBlessing)
            removeBlock("bridge_block");

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
        bigWorld.dispose();
        world.dispose();
    }

    private void parseMaps() {
        Box2DMapObjectParser parser = new Box2DMapObjectParser(TILE_SCALE);
        parser.load(world, bigWorld);
    }

    private void initFactory() {
        EntityFactory factory = new EntityFactory(engine, world);

        factory.createTileLayer(bigWorld, "props", 5);
        factory.createTileLayer(bigWorld, "base", 0);
        factory.createTileLayer(bigWorld, "lava", 1);
        factory.createTileLayer(bigWorld, "path", 2);
        factory.createTileLayer(bigWorld, "base2", 3);
        factory.createTileLayer(bigWorld, "base3", 4);
        factory.createTileLayer(bigWorld, "base4", 5);
        factory.createTileLayer(bigWorld, "landscape", 2);
        factory.createTileLayer(bigWorld, "clouds", 6);
        factory.createObjectLayer(bigWorld, "Collisions", 1, 1, 0.01f, 0.01f);

        MapLayer spawnLayer = bigWorld.getLayers().get("Spawns");
        for (MapObject object : spawnLayer.getObjects()) {
            if ("PlayerSpawn".equals(object.getName()) && object instanceof RectangleMapObject rectObj) {
                Rectangle rect = rectObj.getRectangle();

                float spawnX = rect.x + rect.width / 2;
                float spawnY = rect.y + rect.height / 2;
                factory.createPlayer(spawnX, spawnY, 5);
            }
        }

        MapObjects objects = bigWorld.getLayers().get("Enemies").getObjects();
        for (MapObject object : objects) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            String typeStr = object.getProperties().get("type", String.class);
            if (typeStr == null) continue;
            EnemyType type = EnemyType.valueOf(typeStr.toUpperCase());
            String location = object.getProperties().get("location", String.class); // із Tiled
            if (location == null) location = "default";
            factory.createEnemy(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 5, location);
        }


        objects = bigWorld.getLayers().get("NPC").getObjects();
        for (MapObject object : objects) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            String typeStr = object.getProperties().get("type", String.class);
            if (typeStr == null) continue;
            NPCType type = NPCType.valueOf(typeStr.toUpperCase());
            factory.createNPC(rect.x + rect.width / 2, rect.y + rect.height / 2, type, 5);
        }

        objects = bigWorld.getLayers().get("InteractiveObjects").getObjects();
        for (MapObject obj : objects) {
            Rectangle rect = ((RectangleMapObject) obj).getRectangle();
            factory.createInteractiveObject(rect, obj.getProperties(), 5);
        }
    }

    private void addSystems() {
        engine.addSystem(new InputSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new CameraFollowSystem());
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new AttackSystem());
        engine.addSystem(new EnemyAttackSystem());
        engine.addSystem(new AISystem());
        engine.addSystem(new EnemyPathfindingSystem());
        dialogueWindow = new DialogueWindow(new Texture("images/dialogue.png"), MENU_hoverFont);
        engine.addSystem(new InteractionSystem(dialogueWindow));
        hintRenderer = new HintRenderer(GAME_hoverFont);
        engine.addSystem(new AttackSystem());
        engine.addSystem(new EnemyAttackSystem());
        engine.addSystem(new RenderSystem());
    }

    public void initRemovableCollision(MapLayer layer) {
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rect = rectObj.getRectangle();

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set((rect.x + rect.width / 2) / PPM, (rect.y + rect.height / 2) / PPM);

                Body body = world.createBody(bodyDef);

                PolygonShape shape = new PolygonShape();
                shape.setAsBox(rect.width / 2 / PPM, rect.height / 2 / PPM);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = shape;
                fixtureDef.isSensor = false;

                body.createFixture(fixtureDef).setUserData("removable");

                shape.dispose();

                String id = object.getName();
                if (id != null && !id.isEmpty()) {
                    removableBodies.put(id, body);
                } else {
                    Gdx.app.error("CollisionManager", "Removable object has no name!");
                }
            }
        }
    }

    public void removeBlock(String id) {
        Body body = removableBodies.get(id);
        if (body != null) {
            world.destroyBody(body);
            removableBodies.remove(id);
        }
    }

    private boolean closeEnoughToHouse(Vector2 playerPos) {
        int houseX = 350;
        int houseY = 10100;
        float dx = playerPos.x - houseX;
        float dy = playerPos.y - houseY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < 2f * PPM; // наприклад 2 тайла
    }
}
