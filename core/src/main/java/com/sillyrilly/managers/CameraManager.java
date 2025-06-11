package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraManager {
    private static CameraManager instance;

    private OrthographicCamera camera;
    private Viewport viewport;

    private CameraManager() {
    }

    public static CameraManager getInstance() {
        if (instance == null) instance = new CameraManager();
        return instance;
    }

    public void initialize(float width, float height) {
        camera = new OrthographicCamera();
        viewport = new FitViewport(width, height, camera);
        viewport.apply();
        camera.position.set(width / 2f, height / 2f, 0);
        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void centerOn(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
    }

    public void centerOnSmooth(float x, float y) {
        camera.position.lerp(new Vector3(x, y, 0), 0.05f);
        camera.update();
    }

    public void setZoom(float zoom) {
        camera.zoom -= zoom;
        if (camera.zoom < 0.1f)
            camera.zoom = 0.1f;

        Gdx.app.log("CameraManager", "Zoom: " + camera.zoom);
        camera.update();
    }
}
