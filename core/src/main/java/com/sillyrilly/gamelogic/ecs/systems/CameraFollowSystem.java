package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.sillyrilly.gamelogic.ecs.components.CameraTargetComponent;
import com.sillyrilly.gamelogic.ecs.components.PositionComponent;
import com.sillyrilly.managers.CameraManager;

public class CameraFollowSystem extends EntitySystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ImmutableArray<Entity> targets;
    private final CameraManager cameraManager;
    private Engine engine;

    private static boolean cameraSmoothing = false;

    public CameraFollowSystem(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        this.engine = engine;
        targets = engine.getEntitiesFor(Family.all(PositionComponent.class, CameraTargetComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (targets.size() == 0) {
            targets = engine.getEntitiesFor(Family.all(PositionComponent.class, CameraTargetComponent.class).get());
            return;
        }
        Entity target = targets.first();

        PositionComponent pos = pm.get(target);
        if (cameraSmoothing) {
            cameraManager.centerOnSmooth(pos.position.x+16, pos.position.y+16);
        } else {
            cameraManager.centerOn(pos.position.x+16, pos.position.y+16);
        }
    }

    public static void changeCameraSmoothing() {
        cameraSmoothing = !cameraSmoothing;
    }
}
