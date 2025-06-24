package com.sillyrilly.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sillyrilly.gamelogic.ecs.utils.GameState;
import com.sillyrilly.managers.ScreenManager;

import static com.sillyrilly.managers.FontManager.MENU_hoverFont;

public class GameOverScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private BitmapFont font;
    private String stats;

    public GameOverScreen(String stats) {
        this.stats = stats;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        font = MENU_hoverFont; // або завантаж кастомний
        skin = new Skin(Gdx.files.internal("uiskin.json")); // або власна реалізація

        buildUI();
    }

    private void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(skin.get("default", TextButton.TextButtonStyle.class));
       style.font = font;
       style.fontColor = Color.WHITE;



        Label title = new Label("Ви все ж воз'єдналися але в іншому світі", new Label.LabelStyle(font, Color.WHITE));
        title.setFontScale(3);
        title.setAlignment(Align.center);
        root.add(title).colspan(2).padBottom(20).row();

        Label statsLabel = new Label(stats, new Label.LabelStyle(font, Color.WHITE));
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.center);
        root.add(statsLabel).colspan(2).padBottom(20).width(400).row();

        TextButton restart = new TextButton("Грати знову", style);;
        restart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameState.instance.reset();
                ScreenManager.instance.setScreen(ScreenManager.ScreenType.MENU);
            }
        });
        root.add(restart).colspan(2).center();
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}
