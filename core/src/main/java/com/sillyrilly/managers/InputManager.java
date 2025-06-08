package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class InputManager {

    public enum InputDirection {
        //  NONE,
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private static final Vector2 movement = new Vector2();

    public static void update() {
        movement.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            movement.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            movement.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            movement.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            movement.x += 1;
        }

        if (movement.len2() > 0) {
            movement.nor();
        }
    }

    public static Vector2 getMovementDirection() {
        return movement;
    }

//    public static boolean isInteracting() {
//        return Gdx.input.isKeyJustPressed(Input.Keys.E);
//    }
//    public static boolean isPaused() {
//        return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
//    }
}
