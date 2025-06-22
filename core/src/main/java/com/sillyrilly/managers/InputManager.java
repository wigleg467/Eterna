package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AnimationButtomComponent;
import com.sillyrilly.gamelogic.ecs.components.AnimationTopComponent;
import com.sillyrilly.gamelogic.ecs.components.WeaponComponent;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager extends InputAdapter {
    public static InputManager instance;
    public static InputMultiplexer multiplexer;

    private final Vector2 movement = new Vector2();
    private AnimationButtomComponent.BottomState bottomState;
    private AnimationTopComponent.TopState topState;
    private boolean changeCamera = false;
    private boolean canAttack = true;
    private boolean debug = false;
    private WeaponComponent.WeaponType currentWeapon = WeaponComponent.WeaponType.SWORD;

    private static boolean isInitialized = false;

    private InputManager() {
    }

    public static void initialize() {
        if (!isInitialized) {
            instance = new InputManager();
            multiplexer = new InputMultiplexer();

            Gdx.input.setInputProcessor(multiplexer);
        }
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
            CameraManager.instance.setZoom(0.02f);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS) && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            // ### Zoom out ###
            CameraManager.instance.setZoom(-0.02f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            //   changeCamera = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.instance.setMenuScreen();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            currentWeapon = WeaponComponent.WeaponType.SWORD;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            currentWeapon = WeaponComponent.WeaponType.AXE;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            currentWeapon = WeaponComponent.WeaponType.FORK;
        }
    }

    public boolean isDebugMode() {
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

    public boolean canAttack() {
        return canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public WeaponComponent.WeaponType getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(WeaponComponent.WeaponType type) {
        this.currentWeapon = type;
    }
}
