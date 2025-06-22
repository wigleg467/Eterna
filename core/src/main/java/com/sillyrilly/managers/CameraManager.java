package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.math.MathUtils.round;

public class CameraManager {
    public static CameraManager instance;

    public static OrthographicCamera camera;
    public static Viewport viewport;

    private static boolean isInitialized = false;

    private CameraManager() {
    }

    public static void initialize(float width, float height) {
        if (!isInitialized) {
            instance = new CameraManager();

            camera = new OrthographicCamera();
            camera.position.set(width / 2f, height / 2f, 0);
            camera.update();

            viewport = new FitViewport(width, height, camera);
            viewport.apply();

            isInitialized = true;
        }
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void centerOn(float x, float y) {
        camera.position.set(x, y, 0);
    }

    public void centerOnSmooth(float x, float y) {
        camera.position.lerp(new Vector3(x, y, 0), 0.02f);
    }

    public void setZoom(float zoom) {
        camera.zoom = round((camera.zoom - zoom) * 100) / 100f;
        if (camera.zoom < 0.1f) camera.zoom = 0.1f;

        Gdx.app.log("CameraManager", "Zoom: " + camera.zoom);
    }
}
