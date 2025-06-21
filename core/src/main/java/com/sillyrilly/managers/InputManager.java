package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationButtomComponent;
import com.sillyrilly.gamelogic.ecs.components.AnimationTopComponent;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager extends InputAdapter {
    private static InputManager instance;

    private final InputMultiplexer multiplexer;

    private final Vector2 movement = new Vector2();
    private AnimationButtomComponent.BottomState bottomState;
    private AnimationTopComponent.TopState topState;
    private boolean changeCamera = false;
    private boolean canAttack = true;
    private boolean debug = false;

    private InputManager() {
        this.multiplexer = new InputMultiplexer();

        Gdx.input.setInputProcessor(multiplexer);
    }

    public static InputManager getInstance() {
        if (instance == null) instance = new InputManager();

        return instance;
    }

    public void update() {

        topState = AnimationTopComponent.TopState.IDLE;
        bottomState = AnimationButtomComponent.BottomState.IDLE;



        movement.set(0, 0);

        boolean isAttacking = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean isMoving = false;

// Спочатку перевіряємо рух — незалежно від атаки
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            movement.y += 1;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            movement.y -= 1;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            movement.x -= 1;
            isMoving = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            movement.x += 1;
            isMoving = true;
        }

        if (movement.len2() > 0) {
            movement.nor();
        }

        if (isAttacking) {
            topState = AnimationTopComponent.TopState.ATTACK;
        }

        bottomState = isMoving
                ? AnimationButtomComponent.BottomState.WALK
                : AnimationButtomComponent.BottomState.IDLE;
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debug = !debug;
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            changeCamera = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.MENU);
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public void setChangeCamera(boolean changeCamera) {
        this.changeCamera = changeCamera;
    }

    public InputMultiplexer getMultiplexer() {
        return multiplexer;
    }

    public AnimationTopComponent.TopState getTopState() {
        return topState;
    }
    public AnimationButtomComponent.BottomState getBottomState() {
        return bottomState;
    }


    public Vector2 getMovement() {
        return movement;
    }

    public boolean isChangeCamera() {
        return changeCamera;
    }

    public boolean canAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }
}

