package com.sillyrilly.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sillyrilly.managers.AudioManager;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import com.sillyrilly.managers.ScreenManager;
import com.sillyrilly.util.ASCIIConfig;

import java.util.Random;

import static com.sillyrilly.managers.FontManager.*;
import static com.sillyrilly.ui.hud.ASCIITexture.*;
import static com.sillyrilly.util.ASCIIConfig.*;

public class ASCIIScreen implements Screen {
    private SpriteBatch batch;
    private AudioManager audioManager;
    private InputHandler inputHandler;
    private Random rand;

    private float playerX = 1.5f, playerY = 1.5f;
    private float angle = 0.0f;

    private float scale = 1f * ANTI_ALIASING;

    private boolean isCursorCaught = true;
    private boolean isMapOn = false;
    private int hud = 0;

    private boolean isInitialized = false;

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        if (!isInitialized) {
            batch = ScreenManager.batch;
            audioManager = AudioManager.instance;
            inputHandler = new InputHandler();
            rand = new Random();

            isInitialized = true;
        }

        Gdx.input.setCursorCatched(isCursorCaught);
        InputManager.multiplexer.addProcessor(inputHandler);
        audioManager.playMusic(AudioManager.MusicType.BASEMENT);
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        update(delta);

        batch.begin();

        draw();
        drawHUD();
        if (isMapOn) drawMiniMap();

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        CameraManager.instance.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setCursorCatched(false);
        InputManager.multiplexer.removeProcessor(inputHandler);
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose() {
        batch.dispose();
        ASCII_mainFont.dispose();
        ASCII_minimapFont.dispose();
    }

    private void update(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);

        if (hud == 0) maxDepth = 1.3f;
        else if (hud == 1) maxDepth = 1.5f;
        else maxDepth = 4f;
    }

    private void draw() {
        float distToUnderWorld = distanceToValue(playerX, playerY);

        float correctedDistanceToExit = (float) Math.exp((-distToUnderWorld + 2f) * 0.5f);
        if (correctedDistanceToExit < 0f) correctedDistanceToExit = 0.01f;
        if (correctedDistanceToExit > 1f) correctedDistanceToExit = 0.999f;

        for (float x = 0; x < SCREEN_WIDTH; x += scale) {
            float rayAngle = angle - FOV / 2 + FOV * x / SCREEN_WIDTH;
            Hit hit = castRay(rayAngle);
            float correctedDist = hit.dist * (float) Math.cos(rayAngle - angle);

            int wallHeight = (int) (SCREEN_HEIGHT / correctedDist);
            int wallStart = Math.max(0, (SCREEN_HEIGHT - wallHeight) / 2);
            int wallEnd = Math.min(SCREEN_HEIGHT - 1, wallStart + wallHeight);

            for (float y = 0; y < SCREEN_HEIGHT; y += scale) {
                char ch = y < wallStart ? ' ' : (y > wallEnd ? new Random().nextBoolean() ? '—' : '-' : getSymbol(hit));

                boolean result = rand.nextDouble() < correctedDistanceToExit;
                audioManager.setVolume(correctedDistanceToExit, AudioManager.MusicType.BASEMENT);
                audioManager.setVolume(1 - correctedDistanceToExit, AudioManager.MusicType.HELL);
                if (result) ASCII_mainFont.setColor(Color.RED);
                else if (hud == 2)
                    switch (ch) {
                        case '—', '/', '⁄' -> ASCII_mainFont.setColor(Color.BROWN);
                        case '-' -> ASCII_mainFont.setColor(new Color(0x8b3313ff));
                        default -> ASCII_mainFont.setColor(Color.GRAY);
                    }
                else ASCII_mainFont.setColor(Color.GRAY);


                ASCII_mainFont.draw(batch, String.valueOf(ch),
                    x * CELL_W, (SCREEN_HEIGHT - y) * CELL_H);
            }
        }
    }

    private void drawHUD() {
        ASCII_mainFont.setColor(Color.WHITE);
        switch (hud) {
            case 1 -> ASCII_mainFont.draw(batch, SWORD, 0, 1500);
            case 2 -> ASCII_mainFont.draw(batch, LAMP, 0, 800);
            case 3 -> ASCII_mainFont.draw(batch, SKULL, 0, 800);
            case 4 -> ASCII_mainFont.draw(batch, PISTOL, 400, 300);
            case 5 -> ASCII_mainFont.draw(batch, KNIFE, 0, 880);
        }
    }

    private void drawMiniMap() {
        int sizeX = 8, sizeY = 10;
        int offsetX = 10, offsetY = 10;

        ASCII_minimapFont.setColor(Color.BLACK);
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                char ch = '█';
                ASCII_minimapFont.draw(batch, String.valueOf(ch),
                    offsetX + x * sizeX,
                    SCREEN_HEIGHT * 16 - offsetY - y * sizeY
                );
            }
        }
        ASCII_minimapFont.setColor(Color.WHITE);
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                char ch = switch (MAP[y][x]) {
                    case WALL -> '#';
                    case DOOR -> 'D';
                    default -> ' ';
                };
                ASCII_minimapFont.draw(batch, String.valueOf(ch),
                    offsetX + x * sizeX,
                    SCREEN_HEIGHT * 16 - offsetY - y * sizeY
                );
            }
        }

        batch.draw(ASCII_characterRegion, offsetX - 4 + (int) (playerX * sizeX),
            SCREEN_HEIGHT * 16 - (float) offsetY * 2 / 3 - (int) (playerY * sizeY) - 9,
            ASCII_characterGlyph.width / 2f, ASCII_characterGlyph.height / 2f,
            ASCII_characterGlyph.width, ASCII_characterGlyph.height, 1, 1, (float) -Math.toDegrees(angle));

        //        miniMapFont.draw(batch, "^",
//            offsetX + (int) (playerX * sizeX),
//            SCREEN_HEIGHT * 16 - (float) offsetY * 2 / 3 - (int) (playerY * sizeY)
//        );

        // miniMapFont.draw(batch, "*", offsetX + (int) (lampX * sizeX), SCREEN_HEIGHT * 15 - offsetY - (int) (lampY * sizeY));
    }

    private Hit castRay(float rayAngle) {
        float eyeX = (float) Math.cos(rayAngle);
        float eyeY = (float) Math.sin(rayAngle);
        float dist = 0f;

        while (dist < maxDepth) {
            int testX = (int) (playerX + eyeX * dist);
            int testY = (int) (playerY + eyeY * dist);

            if (testX < 0 || testX >= MAP_WIDTH || testY < 0 || testY >= MAP_HEIGHT)
                return new Hit(maxDepth, WALL);

            int cell = MAP[testY][testX];
            if (cell == WALL || cell == DOOR)
                return new Hit(dist, cell);

            dist += 0.05f;
        }

        return new Hit(maxDepth, EMPTY);
    }

    private char getSymbol(Hit hit) {
        int cell = hit.cell;
        float d = hit.dist;

        //   █▓▒░░=—-.

        boolean result = rand.nextBoolean();
        if (hud == 2) {
            result = rand.nextFloat() < 0.95f;
            return
                cell == DOOR ? result ? '/' : '⁄' :
                    d < 0.9f ? result ? '|' : '!' :
                        d < 1.05f ? rand.nextBoolean() ? '|' : '!' :
                            d < 1.9f ? result ? '!' : ':' :
                                d < 2.05f ? rand.nextBoolean() ? '!' : ':' :
                                    d < 2.9f ? result ? ':' : '.' :
                                        d < 3.05f ? rand.nextBoolean() ? ':' : '.' :
                                            d < 4f ? result ? '.' : '·' : ' ';
        } else if (hud == 1) {
            return d < 1.5f ? cell == DOOR ? result ? '/' : '⁄' : result ? '.' : '·' : ' ';
        } else {
            return d < 1.3f ? cell == DOOR ? result ? '/' : '⁄' : result ? '.' : '·' : ' ';
        }
    }

    private void toggleDoor() {
        // шукаємо двері у радіусі 1 тайлу
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int tx = (int) playerX + dx;
                int ty = (int) playerY + dy;
                if (tx < 0 || ty < 0 || tx >= MAP_WIDTH || ty >= MAP_HEIGHT) continue;
                if (MAP[ty][tx] == DOOR) {
                    MAP[ty][tx] = EMPTY;            // відчинили
                    return;
                }
                if (MAP[ty][tx] == DOOR_BACK) {
                    //   audioManager.
                    //  ScreenManager.getInstance().setScreen();
                }
                if (MAP[ty][tx] == DOOR_FORWARD) {
                    //  ScreenManager.getInstance().setScreen();
                }
            }
        }
    }

    private float distanceToValue(float playerX, float playerY) {
        for (int y = 0; y < ASCIIConfig.MAP_HEIGHT; y++) {
            for (int x = 0; x < ASCIIConfig.MAP_WIDTH; x++) {
                if (ASCIIConfig.MAP[y][x] == ASCIIConfig.DOOR_FORWARD) {
                    float dx = (x + 0.5f) - playerX;
                    float dy = (y + 0.5f) - playerY;
                    return (float) Math.sqrt(dx * dx + dy * dy);
                }
            }
        }
        return 1; // not found
    }

    private void handleInput(float delta) {
        // Рух вперед/назад
        // tiles per second
        float moveSpeed;

        if (inputHandler.isRunning) {
            moveSpeed = 1.5f;
            footstepCooldown = 0.5f;
        } else {
            moveSpeed = 1f;
            footstepCooldown = 0.9f;
        }

        footstepTimer -= delta; // зменшуємо таймер

        boolean isMoving = false;
        float dx = 0, dy = 0;

// Збір напрямку руху
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dx += (float) Math.cos(angle);
            dy += (float) Math.sin(angle);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dx -= (float) Math.cos(angle);
            dy -= (float) Math.sin(angle);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx += (float) Math.sin(angle);
            dy -= (float) Math.cos(angle);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx -= (float) Math.sin(angle);
            dy += (float) Math.cos(angle);
        }

        if (dx != 0 || dy != 0) {
            isMoving = true;
        }

        if (isMoving && footstepTimer <= 0f) {
            AudioManager.instance.playSound(AudioManager.SoundType.STEPS);
            footstepTimer = footstepCooldown;
        }

        if (dx != 0 || dy != 0) {
            // Нормалізація
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;

            // Потенційна нова позиція
            float newX = playerX + dx * moveSpeed * delta;
            float newY = playerY + dy * moveSpeed * delta;

            boolean canMoveX = MAP[(int) playerY][(int) newX] == 0;
            boolean canMoveY = MAP[(int) newY][(int) playerX] == 0;

            // Перевірка повного проходу
            if (canMoveX && canMoveY) {
                playerX = newX;
                playerY = newY;
            }
            // Якщо повністю не можна — пробуємо ковзнути
            else if (canMoveX) {
                playerX = newX;
            } else if (canMoveY) {
                playerY = newY;
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.F) || Gdx.input.isKeyJustPressed(Input.Keys.E)) toggleDoor();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) isMapOn = !isMapOn;

        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            if (hud + 1 < 6) hud++;
            else hud = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            isCursorCaught = !isCursorCaught;
            Gdx.input.setCursorCatched(isCursorCaught);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ScreenManager.instance.setScreen(ScreenManager.ScreenType.MENU);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            angle -= ROT_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            angle += ROT_SPEED * delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            scale -= 0.1f;
            if (scale < 0.1f) scale = 0.1f;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            scale += 0.1f;
            if (scale > 8f) scale = 8f;
        }

        if (inputHandler.X != inputHandler.newX) {
            angle += inputHandler.getX() / 3 * delta;
        }

        if (inputHandler.scroll != 0) {
            if (inputHandler.scroll > 0) {
                hud--;
                if (hud < 0) hud = 2;
            } else {
                hud++;
                if (hud > 2) hud = 0;
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
}

