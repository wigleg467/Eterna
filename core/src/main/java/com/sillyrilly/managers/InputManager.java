package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;
import net.dermetfan.gdx.Multiplexer;

public class InputManager extends InputAdapter {
    private static InputManager instance;

    private final InputMultiplexer multiplexer;

    private final Vector2 movement = new Vector2();
    private AnimationComponent.State state;
    private float stateTime = 0;

    private InputManager() {
        this.multiplexer = new InputMultiplexer();

        Gdx.input.setInputProcessor(multiplexer);
    }

    public static InputManager getInstance() {
        if (instance == null) instance = new InputManager();

        return instance;
    }

    public void update() {
        AnimationComponent.State previousState = state;
        state = AnimationComponent.State.IDLE;

        movement.set(0, 0);

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // ### Удар ###
            if (state != AnimationComponent.State.ATTACK) {
                state = AnimationComponent.State.ATTACK;
                stateTime = 0f;
            }
        } else {
            // ### Рух ###
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                movement.y += 1;
                state = AnimationComponent.State.WALK;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                movement.y -= 1;
                state = AnimationComponent.State.WALK;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                movement.x -= 1;
                state = AnimationComponent.State.WALK;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                movement.x += 1;
                state = AnimationComponent.State.WALK;
            }

            if (movement.len2() > 0) {
                movement.nor();
            } else {
                state = AnimationComponent.State.IDLE;
            }
        }

        if (!state.equals(previousState)) {
            stateTime = 0f;
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // ### Перемикання згладження камери ###
            CameraFollowSystem.changeCameraSmoothing();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // ### Zoom in ###
            CameraManager.getInstance().setZoom(0.02f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // ### Zoom out ###
            CameraManager.getInstance().setZoom(-0.02f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);
        }
    }

    public Vector2 getMovement() {
        return movement;
    }

    public float getStateTime() {
        return stateTime;
    }

    public AnimationComponent.State getState() {
        return state;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }
}
