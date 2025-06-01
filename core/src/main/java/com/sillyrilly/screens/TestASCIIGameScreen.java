package com.sillyrilly.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.Coord;
import squidpony.squidmath.IRNG;
import squidpony.squidmath.StatefulRNG;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidgrid.gui.gdx.TextCellFactory;

import java.util.List;

public class TestASCIIGameScreen implements Screen {

    private static final int GRID_WIDTH = 126;
    private static final int GRID_HEIGHT = 35;

    private SpriteBatch batch;
    private SquidPanel display;
    private char[][] dungeon;

    private int playerX, playerY;

    private DungeonGenerator dungeonGen;
    private IRNG rng;

    @Override
    public void show() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/SourceCodePro-Black.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = 16; // розмір символів
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS; // або свій набір
        parameter.kerning = false; // для моноширинного шрифта
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;

        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        batch = new SpriteBatch();

        TextCellFactory factory = new TextCellFactory().font(font);
        display = new SquidPanel(GRID_WIDTH, GRID_HEIGHT, factory);

        rng = new StatefulRNG(12345);
        dungeonGen = new DungeonGenerator(GRID_WIDTH, GRID_HEIGHT, rng);
        dungeon = DungeonUtility.hashesToLines(dungeonGen.generate(TilesetType.OPEN_AREAS)); // тут можна змінити тип генерації

        Coord entry = Coord.get(1, 1);
        List<Coord> floor = DungeonUtility.allMatching(dungeon, '.');
        if (!floor.isEmpty()) entry = floor.get(rng.nextInt(floor.size()));

        playerX = entry.x;
        playerY = entry.y;
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        display.erase();

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                display.put(x, y, dungeon[x][y]);
            }
        }

        display.put(playerX, playerY, '#');

        batch.begin();
        display.draw(batch, 1.0f);
        batch.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W) && isWalkable(playerX, playerY - 1)) playerY--;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.S) && isWalkable(playerX, playerY + 1)) playerY++;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.A) && isWalkable(playerX - 1, playerY)) playerX--;
        else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && isWalkable(playerX + 1, playerY)) playerX++;
    }

    private boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return false;
        return dungeon[x][y] == '.';
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


}
