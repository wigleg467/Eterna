package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.systems.CameraFollowSystem;

public class InputManager extends InputAdapter {
    private static InputManager instance;

    private static final float MIN_ZOOM = 0.7f;
    private static final float MAX_ZOOM = 1.5f;
    private static final float ZOOM_LERP_SPEED = 3f;

    private float targetZoom;
    private final Vector2 movement = new Vector2();
    private boolean sprinting = false;

    private InputManager() {
        targetZoom = CameraManager.getInstance().getCamera().zoom;
        instance = this;
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

        // --- Спринт ---
        sprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        // --- Перемикання згладження камери ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            CameraFollowSystem.changeCameraSmoothing();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            // zoom in
            CameraManager.getInstance().setZoom(-0.1f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            // zoom out
            CameraManager.getInstance().setZoom(0.1f);
        }

        // --- Lerp Zoom ---
//        float currentZoom = CameraManager.getInstance().getCamera().zoom;
//        float lerpedZoom = lerp(currentZoom, targetZoom, Gdx.graphics.getDeltaTime() * ZOOM_LERP_SPEED);
//        lerpedZoom = clampZoom(lerpedZoom);
//
//        if (!Float.isNaN(lerpedZoom) && !Float.isInfinite(lerpedZoom)) {
//            CameraManager.getInstance().setZoom(lerpedZoom);
//        }
    }

    public Vector2 getMovementDirection() {
        return movement;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public void handleScroll(float amountY) {
        if ((int) amountY == 0) return; // Ігноруємо шум
        targetZoom = clampZoom(targetZoom + amountY * 0.1f);
    }

    private float lerp(float from, float to, float alpha) {
        return from + (to - from) * MathUtils.clamp(alpha, 0f, 1f);
    }

    private float clampZoom(float zoom) {
        return MathUtils.clamp(zoom, MIN_ZOOM, MAX_ZOOM);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        handleScroll(amountY);
        return true;
    }
}
