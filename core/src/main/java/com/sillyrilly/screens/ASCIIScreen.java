package com.sillyrilly.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.util.ArrayList;
import java.util.List;

import static com.sillyrilly.util.d2.ASCIITexture.CHARACTER_LIST;

public class ASCIIScreen implements Screen {

    private SpriteBatch batch;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private BitmapFont renderFont;
    private BitmapFont miniMapFont;

    private final InputHandler inputHandler = new InputHandler();

    private List<SpriteEntity> sprites = new ArrayList<>();

    private final static int[][] MAP = {
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 2, 1, 1, 1, 0, 1, 0, 1, 1, 1, 2, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
        {1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1},
        {1, 0, 0, 2, 0, 0, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };

    private final static int EMPTY = 0;
    private final static int WALL = 1;
    private final static int DOOR = 2;
    private final int mapWidth = MAP[0].length;
    private final int mapHeight = MAP.length;

    private final static int antiAliasing = 1;

    private final static int CELL_W = 8 / antiAliasing;   // ширина символу у пікселях
    private final static int CELL_H = 16 / antiAliasing;  // висота символу у пікселях

    private final static float FOV = (float) Math.PI / 9 * 5;
    private final static float ROT_SPEED = 1.5f;  // radians per second
    private final static float MAX_DEPTH = 8f;

    private float playerX = 1.5f, playerY = 1.5f;
    private float angle = 0.0f;
    private float moveSpeed = 2.0f; // tiles per second

    private float scale = 1f * antiAliasing;

    private int screenWidth = 160 * antiAliasing;
    private int screenHeight = 45 * antiAliasing;

    private boolean isCursorCatched = true;
    private boolean isMapOn = false;


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputHandler);
        Gdx.input.setCursorCatched(isCursorCatched);
        {
            generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CascadiaMono-Regular.ttf"));
            parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.characters = CHARACTER_LIST;
            int size = (int) (scale * 16);
            if (size < 4) size = 4;
            else if (size > 16) size = 16;
            size = 16;
            parameter.size = size;

            renderFont = generator.generateFont(parameter);

            parameter.size = 16;
            miniMapFont = generator.generateFont(parameter);
        }
        batch = new SpriteBatch();

        sprites.add(new SpriteEntity(5.5f, 4.5f, '☼'));
        sprites.add(new SpriteEntity(2.5f, 2.5f, '&'));
        sprites.add(new SpriteEntity(4.5f, 6.5f, '$'));
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        batch.begin();

        for (float x = 0; x < screenWidth; x += scale) {
            Hit hit = castRay(angle - FOV / 2 + FOV * x / screenWidth);
            char symbol = getSymbol(hit);

            float dist = hit.dist;


            int wallHeight = (int) (screenHeight / (hit.dist + 0.1f));
            int wallStart = Math.max(0, (screenHeight - wallHeight) / 2);
            int wallEnd = Math.min(screenHeight - 1, wallStart + wallHeight);
            for (float y = 0; y < screenHeight; y += scale) {
                char ch = y < wallStart ? ' ' : (y > wallEnd ? '—' : symbol);
                if (ch != '—') {
                    if (ch == '/') {
                        renderFont.setColor(Color.RED);
                    } else {
                        renderFont.setColor(Color.GRAY);
                    }
                } else {
                    renderFont.setColor(Color.BROWN);
                }
                renderFont.draw(batch, String.valueOf(ch),
                    x * CELL_W, (screenHeight - y) * CELL_H);
            }
        }
        renderSprites();
        if (isMapOn) drawMiniMap();

        batch.end();
    }

    /**
     * @param width  - ширина
     * @param height - висота
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {

    }

    /**
     * @see ApplicationListener#pause()
     */
    @Override
    public void pause() {

    }

    /**
     * @see ApplicationListener#resume()
     */
    @Override
    public void resume() {

    }

    /**
     * Called when this screen is no longer the current screen for a {@link Game}.
     */
    @Override
    public void hide() {

    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        batch.dispose();
        generator.dispose();
        renderFont.dispose();
        miniMapFont.dispose();
    }

    private void drawMiniMap() {
        int miniSizeX = 8;
        int miniSizeY = 10;
        int offsetX = 10;
        int offsetY = 10;

        miniMapFont.setColor(Color.BLACK);
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                char ch = '█';
                miniMapFont.draw(batch, String.valueOf(ch),
                    offsetX + x * miniSizeX,
                    screenHeight * 16 - offsetY - y * miniSizeY
                );
            }
        }
        miniMapFont.setColor(Color.WHITE);
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                char ch = switch (MAP[y][x]) {
                    case WALL -> '#';
                    case DOOR -> 'D';
                    default -> ' ';
                };
                miniMapFont.draw(batch, String.valueOf(ch),
                    offsetX + x * miniSizeX,
                    screenHeight * 16 - offsetY - y * miniSizeY
                );
            }
        }

        // Малюємо гравця
        miniMapFont.draw(batch, "P",
            offsetX + (int) (playerX * miniSizeX),
            screenHeight * 16 - (float) offsetY * 2 / 3 - (int) (playerY * miniSizeY)
        );
    }

    private Hit castRay(float rayAngle) {
        float eyeX = (float) Math.cos(rayAngle);
        float eyeY = (float) Math.sin(rayAngle);
        float dist = 0f;

        while (dist < MAX_DEPTH) {
            int testX = (int) (playerX + eyeX * dist);
            int testY = (int) (playerY + eyeY * dist);

            if (testX < 0 || testX >= mapWidth || testY < 0 || testY >= mapHeight)
                return new Hit(MAX_DEPTH, WALL);

            int cell = MAP[testY][testX];
            if (cell == WALL || cell == DOOR)
                return new Hit(dist, cell);

            dist += 0.05f;
        }

        return new Hit(MAX_DEPTH, EMPTY);
    }

    private char getSymbol(Hit hit) {
        if (hit.cell == DOOR) return '/';          // двері

        float d = hit.dist;
        if (d < 2f) return '|';
        if (d < 4f) return '!';
        if (d < 6f) return ':';
        if (d < 8f) return '.';
        return ' ';

//        if (dist < 1.5f) return '=';
//        if (dist < 3f) return '—';
//        if (dist < 5f) return '-';
//        if (dist < 7f) return '.';
//        return ' ';
//        //   if (dist < maxDepth / 8f) return '█';
//        if (dist < maxDepth / 6f) return '▓';
//        else if (dist < maxDepth / 4f) return '▒';
//            //  else if (dist < maxDepth / 2f) return '░';
//        else return '░';
    }

    private void toggleDoor() {
        // шукаємо двері у радіусі 1 тайлу
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int tx = (int) playerX + dx;
                int ty = (int) playerY + dy;
                if (tx < 0 || ty < 0 || tx >= mapWidth || ty >= mapHeight) continue;
                if (MAP[ty][tx] == DOOR) {
                    MAP[ty][tx] = EMPTY;            // відчинили
                    return;
                }
            }
        }
    }

//    private void updateDoors(float delta) {
//        // Оновлюємо таймери відкритих дверей
//        Iterator<Map.Entry<Integer, Float>> it = doorOpenTimers.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<Integer, Float> entry = it.next();
//            float newTime = entry.getValue() - delta;
//            if (newTime <= 0f) {
//                // Час минув - зачиняємо двері
//                int pos = entry.getKey();
//                int x = pos >> 16;
//                int y = pos & 0xFFFF;
//                map[y][x] = DOOR;
//                it.remove();
//            } else {
//                entry.setValue(newTime);
//            }
//        }
//    }

    private void handleInput(float delta) {
        // Рух вперед/назад
        if (inputHandler.isRunning) {
            moveSpeed = 3f;
        } else {
            moveSpeed = 1f;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            float newX = playerX + (float) Math.cos(angle) * moveSpeed * delta;
            float newY = playerY + (float) Math.sin(angle) * moveSpeed * delta;
            if (MAP[(int) newY][(int) newX] == 0) {
                playerX = newX;
                playerY = newY;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            float newX = playerX - (float) Math.cos(angle) * moveSpeed * delta;
            float newY = playerY - (float) Math.sin(angle) * moveSpeed * delta;
            if (MAP[(int) newY][(int) newX] == 0) {
                playerX = newX;
                playerY = newY;
            }
        }

        // Рух вбік
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            float newX = playerX + (float) Math.sin(angle) * moveSpeed * delta;
            float newY = playerY - (float) Math.cos(angle) * moveSpeed * delta;
            if (MAP[(int) newY][(int) newX] == 0) {
                playerX = newX;
                playerY = newY;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            float newX = playerX - (float) Math.sin(angle) * moveSpeed * delta;
            float newY = playerY + (float) Math.cos(angle) * moveSpeed * delta;
            if (MAP[(int) newY][(int) newX] == 0) {
                playerX = newX;
                playerY = newY;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.E)) toggleDoor();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) isMapOn = !isMapOn;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isCursorCatched = !isCursorCatched;
            Gdx.input.setCursorCatched(isCursorCatched);
        }


        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle -= ROT_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle += ROT_SPEED * delta;
        }

        if (inputHandler.X != inputHandler.newX) {
            angle += inputHandler.getX() / 3 * delta;
        }

        if (inputHandler.scroll != 0) {
            if (inputHandler.scroll > 0) {
                scale += 0.1f;
                if (scale > 8f) scale = 8f;
            } else {
                scale -= 0.1f;
                if (scale < 0.1f) scale = 0.1f;
            }

//            renderFont.dispose();
//
//                 Gdx.app.log("ASCII", "Scale : " + scale);
//            int size = (int) (scale * 12);
//            if (size < 8) size = 8;
//            else if (size > 16) size = 16;
//            parameter.size = size;
//
//            renderFont = generator.generateFont(parameter);

            inputHandler.scroll = 0;
        }
    }

    void renderSprites() {
        for (SpriteEntity sprite : sprites) {
            // Вектор до спрайта
            float dx = sprite.x - playerX;
            float dy = sprite.y - playerY;
            float distance = (float)Math.sqrt(dx * dx + dy * dy);

            // Кут між гравцем і спрайтом
            float spriteAngle = (float)Math.atan2(dy, dx);
            float angleDiff = spriteAngle - angle;

            // Нормалізація кута
            while (angleDiff > Math.PI) angleDiff -= (float) (2 * Math.PI);
            while (angleDiff < -Math.PI) angleDiff += (float) (2 * Math.PI);

            // Якщо поза полем зору — ігнор
            if (Math.abs(angleDiff) > FOV / 2f) continue;

            // Знаходимо колонку на екрані, де малювати
            int screenX = (int)((angleDiff + FOV / 2f) / FOV * screenWidth);

            // Висота "спрайта" залежить від дистанції
            float correctedDist = distance * (float)Math.cos(angleDiff);
            int spriteHeight = (int)(screenHeight / correctedDist);
            int spriteY = (screenHeight - spriteHeight) / 2;

            // Малюємо символ спрайта
            renderFont.draw(batch,
                String.valueOf(sprite.symbol),
                screenX * CELL_W,
                (screenHeight - spriteY) * CELL_H
            );
        }
    }


    private static class InputHandler extends InputAdapter {
        float X = 0;
        float newX = 0;
        float scroll = 0;
        boolean isRunning = false;

        @Override
        public boolean keyDown(int keycode) {
            if (!isRunning) {
                if (keycode == Input.Keys.SHIFT_LEFT) {
                    isRunning = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if (isRunning) {
                if (keycode == Input.Keys.SHIFT_LEFT) {
                    isRunning = false;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if (screenX != X) {
                this.newX = screenX;
                return true;
            }
            newX = X;
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            if (scroll == 0) {
                scroll = amountY;
                return true;
            }
            return false;
        }

        public float getX() {
            float temp = newX - X;
            X = newX;
            return temp;
        }
    }

    private static class Hit {
        float dist;
        int cell;

        public Hit(float dist, int cell) {
            this.dist = dist;
            this.cell = cell;
        }
    }

    private static class SpriteEntity {
        float x, y;
        char symbol;

        SpriteEntity(float x, float y, char symbol) {
            this.x = x;
            this.y = y;
            this.symbol = symbol;
        }
    }
}

