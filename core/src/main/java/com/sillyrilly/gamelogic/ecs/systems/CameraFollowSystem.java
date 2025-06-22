package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraFollowableComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraTargetComponent;
import com.sillyrilly.managers.CameraManager;

import static com.sillyrilly.util.Const.PPM;
import static com.sillyrilly.util.Const.TILE_SIZE;

public class CameraFollowSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<CameraFollowableComponent> cfc = ComponentMapper.getFor(CameraFollowableComponent.class);
    private final ComponentMapper<CameraTargetComponent> ctc = ComponentMapper.getFor(CameraTargetComponent.class);
    private CameraManager cameraManager;

    private ImmutableArray<Entity> targets;

    private static boolean cameraSmoothing = false;

    @Override
    public void addedToEngine(Engine engine) {
        targets = engine.getEntitiesFor(Family.all(CameraFollowableComponent.class).get());
        cameraManager = CameraManager.instance;
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : targets) {
            if (ctc.has(entity)) {
                Vector2 pos = bc.get(entity).body.getPosition();

                if (cameraSmoothing) {
                    cameraManager.centerOnSmooth((pos.x) * PPM, (pos.y) * PPM + TILE_SIZE);

                } else {
                    cameraManager.centerOn((pos.x) * PPM, (pos.y) * PPM + TILE_SIZE);
                }

                break;
            }
        }
    }

    public static void changeCameraSmoothing() {
        Gdx.app.log("Camera", "Changing CameraSmoothing");
        cameraSmoothing = !cameraSmoothing;
    }
}
