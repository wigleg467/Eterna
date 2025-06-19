package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.LevelComponent;
import com.sillyrilly.gamelogic.ecs.components.TileComponent;
import com.sillyrilly.gamelogic.ecs.ai.NavigationMap;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;

import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.PPM;
import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.TILE_SIZE;

public class RenderSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> ac = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<LevelComponent> lc = ComponentMapper.getFor(LevelComponent.class);
    private final ComponentMapper<TileComponent> tc = ComponentMapper.getFor(TileComponent.class);

    private TileGraph graph = TileGraph.getInstance();
    private int[][] grid = NavigationMap.getInstance().getGrid();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    ;
    private ImmutableArray<Entity> entities;
    private final SpriteBatch batch;
    private final CameraManager cameraManager;
    private final Array<Entity> sortedEntities = new Array<>();

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
        this.cameraManager = CameraManager.getInstance();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, LevelComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        sortEntities();

        cameraManager.getCamera().update();
        batch.setProjectionMatrix(cameraManager.getCamera().combined);

        batch.begin();

        renderEntities();

        batch.end();

        if (InputManager.getInstance().isDebug()) {
            shapeRenderer.setProjectionMatrix(cameraManager.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line); // або Filled

            for (int x = 0; x < grid.length; x++) {
                for (int y = 0; y < grid[0].length; y++) {

                    TileNode node = graph.getNode(x, y); // твій граф
                    boolean walkable = node.walkable;    // або як там у тебе збережено

                    if (walkable) {
                        shapeRenderer.setColor(Color.GREEN);  // прохідні
                    } else {
                        shapeRenderer.setColor(Color.RED);    // непрохідні
                    }

                    // Квадрат тайла (TILE_SIZE = 32, наприклад)
                    shapeRenderer.rect(x * 32, y * 32, 32, 32);
                }
            }

            shapeRenderer.end();


            if (EnemyPathfindingSystem.instance.path != null) {
                if (EnemyPathfindingSystem.instance.path.getCount() >= 1) {
                    Gdx.app.log("Path", "" + EnemyPathfindingSystem.instance.path.get(0).x);
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    shapeRenderer.setColor(Color.WHITE);

                    for (int i = 0; i < EnemyPathfindingSystem.instance.path.getCount() - 1; i++) {
                        TileNode from = EnemyPathfindingSystem.instance.path.get(i);
                        TileNode to = EnemyPathfindingSystem.instance.path.get(i + 1);

                        float fromX = from.x + 0.5f;
                        float fromY = from.y + 0.5f;
                        float toX = to.x + 0.5f;
                        float toY = to.y + 0.5f;

                        shapeRenderer.line(fromX * 32, fromY * 32, toX * 32, toY * 32);
                    }

                    shapeRenderer.end();

                    // Точки пути (опционально)
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.WHITE);
                    for (int i = 0; i < EnemyPathfindingSystem.instance.path.getCount(); i++) {
                        TileNode node = EnemyPathfindingSystem.instance.path.get(i);
                        shapeRenderer.circle((node.x + 0.5f) * 32, (node.y + 0.5f) * 32, 3f);
                    }
                    shapeRenderer.end();
                }
            }
        }
    }

    private void sortEntities() {
        sortedEntities.clear();
        for (int i = 0; i < entities.size(); i++) {
            sortedEntities.add(entities.get(i));
        }

        sortedEntities.sort((a, b) -> {
            LevelComponent la = lc.get(a);
            LevelComponent lb = lc.get(b);

            if (la.level != lb.level) {
                return Integer.compare(la.level, lb.level);
            }

            float ya = bc.get(a).body.getPosition().y;
            float yb = bc.get(b).body.getPosition().y;

            if (tc.has(a)) {
                ya -= tc.get(a).renderOffsetY;
            }
            if (tc.has(b)) {
                yb -= tc.get(b).renderOffsetY;
            }

            return Float.compare(yb, ya);
        });
    }

    private void renderEntities() {
        for (Entity entity : sortedEntities) {
            if (ac.has(entity)) {
                AnimationComponent anim = ac.get(entity);
                BodyComponent body = bc.get(entity);

                TextureAtlas.AtlasRegion frame = anim.currentFrame;
                if (frame == null) continue; // ще не оновлено

                float scale = 0.25f;
                float width = frame.getRegionWidth() * scale;
                float height = frame.getRegionHeight() * scale;

                Vector2 pos = body.getPosition().scl(PPM);
                batch.draw(frame, pos.x - width / 2f, pos.y, width, height);
            } else if (tc.has(entity)) {
                BodyComponent bodyC = bc.get(entity);
                TileComponent tileC = tc.get(entity);

                Vector2 pos = bodyC.body.getPosition().scl(PPM);
                TiledMapTile tile = tileC.tile;
                TextureRegion region = tile.getTextureRegion();

                batch.draw(region, pos.x, pos.y,
                    TILE_SIZE, tileC.renderOffsetY + TILE_SIZE);
            }
        }
    }
}
