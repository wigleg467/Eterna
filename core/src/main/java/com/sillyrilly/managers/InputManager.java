package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager extends InputAdapter {
    private static InputManager instance;

    private static final float minZoom = 0.3f;
    private static final float maxZoom = 2.5f;
    private static final float zoomLerpSpeed = 5f;
    private float targetZoom = 1f;

    private final Vector2 movement = new Vector2();

    private boolean sprinting = false;

    private InputManager() {
        instance = this;
    }

    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public void update() {
        if (ScreenManager.ScreenType.getCurrentScreenType() == ScreenManager.ScreenType.MENU) {
            // меню — інпути не обробляємо тут
            return;
        }

        // --- ДИРЕКЦІЯ ---
        movement.set(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.W)) movement.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) movement.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) movement.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) movement.x += 1;

        if (movement.len2() > 0) movement.nor();

        // --- СПРИНТ ---
        sprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        // --- ПЕРЕМИКАННЯ КАМЕРИ ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            CameraFollowSystem.changeCameraSmoothing();
        }

        // --- ПЛАВНИЙ ЗУМ ---
      //  CameraManager cam = CameraManager.getInstance();
      //  float currentZoom = cam.getCamera().zoom;
      //  float lerpedZoom = lerp(currentZoom, targetZoom, Gdx.graphics.getDeltaTime() * zoomLerpSpeed);
      //  cam.setZoom(lerpedZoom);
    }

    public Vector2 getMovementDirection() {
        return movement;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void handleScroll(float amountY) {
        targetZoom += amountY * 0.1f;
        if (targetZoom < minZoom) targetZoom = minZoom;
        if (targetZoom > maxZoom) targetZoom = maxZoom;
    }

    private float lerp(float from, float to, float alpha) {
        return from + (to - from) * alpha;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        handleScroll(amountY);
        return true;
    }
}
