package com.sillyrilly.gamelogic.ecs.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.sillyrilly.managers.ScreenManager;

public class GameOverWindow extends Window {

    public GameOverWindow(Skin skin, BitmapFont font, String stats) {
        super("", new WindowStyle(font, Color.WHITE, new TextureRegionDrawable(new TextureRegion(new Texture("images/game_over.png")))));

        // Розмір та позиція
        setSize(1280, 720);
        setPosition(0,
                0);
        setMovable(false);

        // Статистика
        Label statsLabel = new Label(stats, skin);
        statsLabel.setFontScale(1.1f);
        statsLabel.setWrap(true);
        statsLabel.setAlignment(Align.topLeft);
        statsLabel.setColor(Color.WHITE);


        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
                GameState.instance.reset(); // ← очищає статику
                ScreenManager.instance.setScreen(ScreenManager.ScreenType.GAME);
            }


            // Додай усе до таблиці
            padTop(30);
            add(statsLabel).width(400).padBottom(20).row();

        }
    }


