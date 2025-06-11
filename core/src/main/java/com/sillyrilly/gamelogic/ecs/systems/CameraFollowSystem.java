package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraTargetComponent;
import com.sillyrilly.managers.CameraManager;

import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.PPM;

public class CameraFollowSystem extends EntitySystem {

    private ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
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
        targets = engine.getEntitiesFor(Family.all(BodyComponent.class, CameraTargetComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        if (targets.size() == 0) {
            targets = engine.getEntitiesFor(Family.all(CameraTargetComponent.class).get());
            return;
        }
        Entity target = targets.first();

        BodyComponent body = pm.get(target);
        Vector2 pos = body.getPosition();

        if (cameraSmoothing) {
            cameraManager.centerOnSmooth((pos.x) * PPM + 32, (pos.y) * PPM + 32);
        } else {
            cameraManager.centerOn((pos.x) * PPM + 32, (pos.y) * PPM + 32);
        }
    }

    public static void changeCameraSmoothing() {
        Gdx.app.log("Camera", "Changing CameraSmoothing");
        cameraSmoothing = !cameraSmoothing;
    }
}
