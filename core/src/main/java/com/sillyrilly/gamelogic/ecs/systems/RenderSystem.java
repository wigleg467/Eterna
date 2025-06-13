package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.LevelComponent;
import com.sillyrilly.gamelogic.ecs.components.TileComponent;
import com.sillyrilly.managers.CameraManager;

import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.PPM;
import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.TILE_SIZE;

public class RenderSystem extends EntitySystem {
    private final ComponentMapper<AnimationComponent> ac = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<LevelComponent> lc = ComponentMapper.getFor(LevelComponent.class);
    private final ComponentMapper<TileComponent> tc = ComponentMapper.getFor(TileComponent.class);

    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private CameraManager cameraManager;
    private Array<Entity> sortedEntities = new Array<>();

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
                ya -= getTileHeightOffset(a);
            }
            if (tc.has(b)) {
                yb -= tc.get(b).renderOffsetY;
                yb -= getTileHeightOffset(b);
            }

            return Float.compare(yb, ya);
        });


        cameraManager.getCamera().update();
        batch.setProjectionMatrix(cameraManager.getCamera().combined);

        batch.begin();

        for (Entity entity : sortedEntities) {
            if (ac.has(entity)) {
                AnimationComponent anim = ac.get(entity);
                BodyComponent body = bc.get(entity);

                TextureAtlas.AtlasRegion frame = anim.currentFrame;
                //        if (frame == null) continue; // ще не оновлено

                float scale = 0.25f;
                float width = frame.getRegionWidth() * scale;
                float height = frame.getRegionHeight() * scale;

                Vector2 pos = body.getPosition().scl(PPM);
                batch.draw(frame, pos.x, pos.y, width, height);
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

        batch.end();
    }

    int getTileHeightOffset(Entity entity) {
        if (!tc.has(entity)) return 0;
        TileComponent tile = tc.get(entity);
        MapProperties props = tile.tile.getProperties();
        return props.containsKey("height") ? (int) props.get("height") : 0;
    }
}
