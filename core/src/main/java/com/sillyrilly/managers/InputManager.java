package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager {
    private static final Vector2 movement = new Vector2();
    private static AnimationComponent.State state;
    private static float stateTime = 0;

    public static void update() {
        if (ScreenManager.ScreenType.getCurrentScreenType() != ScreenManager.ScreenType.GAME)
            return;

        movement.set(0, 0);
        AnimationComponent.State previousState = state;

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            if (state != AnimationComponent.State.ATTACK) {
                state = AnimationComponent.State.ATTACK;
                stateTime = 0f;
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += 1;
            state = AnimationComponent.State.WALK;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= 1;
            state = AnimationComponent.State.WALK;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= 1;
            state = AnimationComponent.State.WALK;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += 1;
            state = AnimationComponent.State.WALK;
        } else {
            state = AnimationComponent.State.IDLE;
        }

        if (!state.equals(previousState)) {
            stateTime = 0f;
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
        }

        if (movement.len2() > 0) movement.nor();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            CameraFollowSystem.changeCameraSmoothing();
        }
    }

    public static Vector2 getMovementDirection() {
        return movement;
    }

    public static float getStateTime() {
        return stateTime;
    }

    public static AnimationComponent.State getState() {
        return state;
    }
}

