package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraFollowableComponent;
import com.sillyrilly.gamelogic.ecs.components.CameraTargetComponent;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;

import static com.sillyrilly.gamelogic.ecs.entities.EntityFactory.PPM;

public class CameraFollowSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<CameraFollowableComponent> cfc = ComponentMapper.getFor(CameraFollowableComponent.class);
    private final ComponentMapper<CameraTargetComponent> ctc = ComponentMapper.getFor(CameraTargetComponent.class);
    private final CameraManager cameraManager;
    private final InputManager inputManager;

    private ImmutableArray<Entity> targets;
    private static boolean cameraSmoothing = false;

    public CameraFollowSystem() {
        this.cameraManager = CameraManager.getInstance();
        this.inputManager = InputManager.getInstance();
    }

    @Override
    public void addedToEngine(Engine engine) {
        targets = engine.getEntitiesFor(Family.all(CameraFollowableComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : targets) {
            if(ctc.has(entity)) {
            //    if(){}

                Vector2 pos = bc.get(entity).body.getPosition();
                if (cameraSmoothing) {
                    cameraManager.centerOnSmooth((pos.x) * PPM + 32, (pos.y) * PPM + 32);
                } else {
                    cameraManager.centerOn((pos.x) * PPM + 32, (pos.y) * PPM + 32);
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
