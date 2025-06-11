package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager extends InputAdapter {
    private static InputManager instance;

    private final Vector2 movement = new Vector2();

    private InputManager() {
    }

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public void update() {
        if (ScreenManager.ScreenType.getCurrentScreenType() == ScreenManager.ScreenType.MENU) return;

        // --- Рух ---
        movement.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) movement.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) movement.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) movement.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) movement.x += 1;
        if (movement.len2() > 0) movement.nor();

        // --- Перемикання згладження камери ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            CameraFollowSystem.changeCameraSmoothing();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // zoom in
            CameraManager.getInstance().setZoom(0.02f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // zoom out
            CameraManager.getInstance().setZoom(-0.02f);
        }

    }

    public Vector2 getMovementDirection() {
        return movement;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {

        return false;
    }
}
