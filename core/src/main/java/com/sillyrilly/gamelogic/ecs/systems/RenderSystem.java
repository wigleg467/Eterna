package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.ai.NavigationMap;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;
import com.sillyrilly.managers.*;

import static com.sillyrilly.util.Const.*;

public class RenderSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> ac = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<LevelComponent> lc = ComponentMapper.getFor(LevelComponent.class);
    private final ComponentMapper<TileComponent> tc = ComponentMapper.getFor(TileComponent.class);
    private final ComponentMapper<PathComponent> pc = ComponentMapper.getFor(PathComponent.class);

    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> enemies;

    private final OrthographicCamera camera = CameraManager.camera;
    private final SpriteBatch batch = ScreenManager.batch;
    private final ShapeRenderer shapeRenderer = ScreenManager.shapeRenderer;

    private final Array<Entity> sortedEntities = new Array<>();

    private final TileGraph graph = TileGraph.instance;
    private final int[][] grid = NavigationMap.instance.grid;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, LevelComponent.class).get());
        enemies = engine.getEntitiesFor(Family.all(PathComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        sortEntities();

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        renderEntities();
        batch.end();

        debugMode();
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

            if (tc.has(a)) ya -= tc.get(a).renderOffsetY;
            if (tc.has(b)) yb -= tc.get(b).renderOffsetY;

            return Float.compare(yb, ya);
        });
    }

    private void renderEntities() {
        for (Entity entity : sortedEntities) {
            if (ac.has(entity)) {
                AnimationComponent anim = ac.get(entity);
                BodyComponent body = bc.get(entity);

                TextureAtlas.AtlasRegion frame = anim.currentFrame;
                if (frame == null) continue;

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

    private void debugMode() {
        if (InputManager.instance.isDebugMode()) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            drawGrid();
            drawPaths();
            drawCursorCoordinates();
        }
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                TileNode node = graph.getNode(x, y);
                shapeRenderer.setColor(node.walkable ? Color.GREEN : Color.RED);
                shapeRenderer.rect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        shapeRenderer.end();
    }

    private void drawPaths() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Entity enemy : enemies) {
            PathComponent pathC = pc.get(enemy);
            GraphPath<TileNode> path = pathC.path;
            if (path == null || path.getCount() < 2) continue;

            shapeRenderer.setColor(Color.WHITE);
            for (int i = 0; i < path.getCount() - 1; i++) {
                TileNode from = path.get(i);
                TileNode to = path.get(i + 1);
                shapeRenderer.line((from.x + 0.5f) * TILE_SIZE, (from.y + 0.5f) * TILE_SIZE,
                    (to.x + 0.5f) * TILE_SIZE, (to.y + 0.5f) * TILE_SIZE);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        for (Entity enemy : enemies) {
            PathComponent pathC = pc.get(enemy);
            for (int i = 0; i < pathC.path.getCount(); i++) {
                TileNode node = pathC.path.get(i);
                shapeRenderer.circle((node.x + 0.5f) * TILE_SIZE, (node.y + 0.5f) * TILE_SIZE, 3f);
            }
        }
        shapeRenderer.end();
    }

    private void drawCursorCoordinates() {
        Vector3 screenCoords = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        FontManager.defaultFont.draw(batch,
            String.format("(%.1f, %.1f)", screenCoords.x, screenCoords.y),
            screenCoords.x + 10, screenCoords.y + 20);
        batch.end();
    }
}
