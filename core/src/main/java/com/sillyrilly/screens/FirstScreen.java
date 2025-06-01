package com.sillyrilly.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** First screen of the application. Displayed after the application is created. */
public class  FirstScreen implements Screen {
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    // Поміняй цей тип, щоб подивитися, як він працює
    private enum ViewportType {
        FIT, STRETCH, SCREEN
    }

    private final ViewportType viewportType = ViewportType.SCREEN;
    @Override
    public void show() {
        camera = new OrthographicCamera();

        switch (viewportType) {
            case FIT:
                viewport = new FitViewport(800, 600, camera);
                break;
            case STRETCH:
                viewport = new StretchViewport(800, 600, camera);
                break;
            case SCREEN:
                viewport = new ScreenViewport(camera);
                break;
        }

        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Малюємо прямокутник по центру
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(300, 250, 200, 100);

        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(0, 0, 50, 50); // кут сцени

        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
