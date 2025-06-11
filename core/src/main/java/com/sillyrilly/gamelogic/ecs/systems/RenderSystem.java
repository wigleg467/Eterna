package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.FacingComponent;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.managers.CameraManager;

public class RenderSystem extends EntitySystem {
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private ComponentMapper<FacingComponent> fm = ComponentMapper.getFor(FacingComponent.class);

    private ImmutableArray<Entity> entities;
    private SpriteBatch batch;
    private CameraManager cameraManager;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
        this.cameraManager = CameraManager.getInstance();
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, AnimationComponent.class, FacingComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(cameraManager.getCamera().combined);
        batch.begin();

        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            AnimationComponent anim = am.get(entity);
            FacingComponent facing = fm.get(entity);
            anim.stateTime += Gdx.graphics.getDeltaTime();

            Animation<TextureAtlas.AtlasRegion> currentAnim = anim.animations.get(anim.currentState);
            TextureAtlas.AtlasRegion frame = currentAnim.getKeyFrame(anim.stateTime, true);


            if (facing != null && !facing.facingRight && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (facing != null && facing.facingRight && frame.isFlipX()) {
                frame.flip(true, false);
            }
            float scale = 0.25f;
            float drawX = pos.position.x;
            float width = frame.getRegionWidth() * scale;

//            if (!facing.facingRight) {
//                drawX += width; // посунути правіше на ширину
//                width *= -1;    // а ширину зробити від’ємною — це дзеркало
//            }

            if (!facing.facingRight && anim.currentState == AnimationComponent.State.ATTACK) {
//                drawX -= frame.offsetX;
                drawX -= width/4;
            }
            //це тре уважно порахувати :(

            batch.draw(frame, drawX, pos.position.y,
                    width,
                    frame.getRegionHeight() * scale);

            batch.end();
        }
    }
}
