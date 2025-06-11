package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.FacingComponent;
import com.sillyrilly.managers.CameraManager;

import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.PPM;

public class RenderSystem extends EntitySystem {
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);


    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private CameraManager cameraManager;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
        this.cameraManager = CameraManager.getInstance();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(AnimationComponent.class, BodyComponent.class, FacingComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        batch.setProjectionMatrix(cameraManager.getCamera().combined);

        batch.begin();

        for (Entity entity : entities) {
            AnimationComponent anim = am.get(entity);
            FacingComponent facing = fm.get(entity);
            BodyComponent body = bc.get(entity);

            anim.stateTime += Gdx.graphics.getDeltaTime();

            Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
            TextureAtlas.AtlasRegion frame = currentAnim.getKeyFrame(anim.stateTime, true);

            if (facing != null && !facing.facingRight && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (facing != null && facing.facingRight && frame.isFlipX()) {
                frame.flip(true, false);
            }

            float scale = 0.25f;
            float width = frame.getRegionWidth() * scale;
            float height = frame.getRegionHeight() * scale;
            Vector2 pos = body.getPosition().scl(PPM);

            batch.draw(frame, pos.x, pos.y, width, height);
        }

        batch.end();
    }
}
