package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class InputManager {
    private static final Vector2 movement = new Vector2();

    public static void update() {
        if (ScreenManager.ScreenType.getCurrentScreenType() == ScreenManager.ScreenType.MENU) {

        } else if (ScreenManager.ScreenType.getCurrentScreenType() == ScreenManager.ScreenType.GAME) {
            movement.set(0, 0);

            if (Gdx.input.isKeyPressed(Input.Keys.W)) movement.y += 1;
            if (Gdx.input.isKeyPressed(Input.Keys.S)) movement.y -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.A)) movement.x -= 1;
            if (Gdx.input.isKeyPressed(Input.Keys.D)) movement.x += 1;

            if (movement.len2() > 0) movement.nor();
        }
    }

    public static Vector2 getMovementDirection() {
        return movement;
    }
}
