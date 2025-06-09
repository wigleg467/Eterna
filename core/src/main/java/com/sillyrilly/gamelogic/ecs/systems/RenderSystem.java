package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.gamelogic.ecs.components.TextureComponent;
import com.sillyrilly.managers.CameraManager;

public class RenderSystem extends EntitySystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private CameraManager cameraManager;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
        this.cameraManager = CameraManager.getInstance();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();

        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            TextureComponent tex = tm.get(entity);

            batch.draw(tex.texture, pos.position.x, pos.position.y);
        }
        batch.end();
    }
}
