package com.sillyrilly.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.ScreenManager;

import java.util.ArrayList;

public class MenuScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture texHead, texSettings, texExit, texWings, texBook, keyOutlineTexture, settingOutlineTexture, bestiaryOutlineTexture, labelTexture, backgroundTexture, continueTexture;
    private Sprite head, settingsBtn, exitBtn, leftWing, rightWing, bestiary, keyOutline, settingOutline, bestiaryOutline, labelSprite;
    private Stage stage;
    private VisWindow settingsWindow;
    private VisTextButton close;
    private VisSlider volumeSlider;
    private VisTable settingsTable;
    private BitmapFont customFont, hoverFont;
    private Music bgm;
    private String hoveredHint = null;
    private final Color darkRed = new Color(111 / 255f, 54 / 255f, 54 / 255f, 1f);
    private static final float width = 1280, height = 720, centreX = width / 2, centreY = height / 2;
    private static final int padding = 30;

    //Для анімації
    float time = 0;
    float maxAngle = 4f;
    float speed = 2f;
    float headAmplitude = 0.1f;
    boolean fadingOut = true;
    float alpha = 1f;


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        initViewportAndCamera();
        playMusic();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        loadTextures();
        createSprites();
        initializeFonts();
        initializeSettingWindow();
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        time += delta;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();
        draw(delta);
    }

    /**
     * @param width  ширина
     * @param height висота
     * @see ApplicationListener#resize(int, int)
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        texHead.dispose();
        texWings.dispose();
        texSettings.dispose();
        texExit.dispose();
        texBook.dispose();
        keyOutlineTexture.dispose();
        settingOutlineTexture.dispose();
        bestiaryOutlineTexture.dispose();
        labelTexture.dispose();
        backgroundTexture.dispose();
        continueTexture.dispose();
        hoverFont.dispose();
        customFont.dispose();
        stage.dispose();
        bgm.dispose();
        VisUI.dispose();
    }

    //************          Методи для show          ************
    private void initViewportAndCamera() {
        camera = CameraManager.getInstance().getCamera();
        viewport = CameraManager.getInstance().getViewport();
        viewport.apply();
        camera.position.set(centreX, centreY, 0);
        camera.update();
    }

    private void loadTextures() {
        texHead = new Texture("images/angel_head.png");
        texWings = new Texture("images/wing.png");
        texSettings = new Texture("images/settings.png");
        settingOutlineTexture = new Texture("images/settingsOutline.png");
        texExit = new Texture("images/key-cut.png");
        keyOutlineTexture = new Texture("images/keyOutline.png");
        texBook = new Texture("images/bestiary.png");
        bestiaryOutlineTexture = new Texture("images/bestiaryOutline.png");
        labelTexture = new Texture("images/title.png");
        backgroundTexture = new Texture("images/bg.jpg");
        continueTexture = new Texture("images/continue.png");
    }

    private void createSprites() {
        batch = new SpriteBatch();

        head = new Sprite(texHead);
        increaseSize(head);
        head.setPosition(centreX - head.getWidth() / 2, centreY - head.getHeight() / 2);

        leftWing = new Sprite(texWings);
        increaseSize(leftWing);
        leftWing.setPosition(centreX - leftWing.getWidth(), centreY - leftWing.getHeight() / 2 + 15);

        rightWing = new Sprite(texWings);
        rightWing.flip(true, false);
        increaseSize(rightWing);
        rightWing.setPosition(centreX, centreY - rightWing.getHeight() / 2 + 15);

        settingsBtn = new Sprite(texSettings);
        settingOutline = new Sprite(settingOutlineTexture);
        decreaseSize(settingsBtn);
        decreaseSize(settingOutline);

        settingsBtn.setPosition(width - settingsBtn.getWidth() - padding, padding);
        settingOutline.setPosition(settingsBtn.getX(), settingsBtn.getY());

        exitBtn = new Sprite(texExit);
        keyOutline = new Sprite(keyOutlineTexture);
        exitBtn.setPosition(width - exitBtn.getWidth() - padding, height - exitBtn.getHeight() - padding);
        keyOutline.setPosition(exitBtn.getX() - 2, exitBtn.getY() - 2);

        bestiary = new Sprite(texBook);
        bestiaryOutline = new Sprite(bestiaryOutlineTexture);
        bestiary.setPosition(padding, padding);
        bestiaryOutline.setPosition(bestiary.getX() - 2, bestiary.getY() - 2);

        labelSprite = new Sprite(labelTexture);
        labelSprite.setSize(labelSprite.getWidth() * 0.4f, labelSprite.getHeight() * 0.4f);
        labelSprite.setPosition(centreX - labelSprite.getWidth() / 2, height - labelSprite.getHeight() * 2.3f);
    }

    private void initializeFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        customFont = generator.generateFont(parameter);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = 20;

        param.characters = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ" +
                "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя" +
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?.,:;(){}[]«»–-";

        hoverFont = generator.generateFont(param);

        hoverFont.getData().markupEnabled = true;

        generator.dispose();
    }

    private void initializeSettingWindow() {
        VisUI.load();
        //******       Стиль для вікна       ******
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Window.WindowStyle style = new Window.WindowStyle(skin.get("default", Window.WindowStyle.class));
        style.background = skin.newDrawable("white", darkRed);
        style.titleFont = customFont;

        settingsWindow = new VisWindow("", style);
        settingsWindow.top().padTop(0);

        settingsWindow.setSize(400, 300);
        settingsWindow.setPosition(centreX - settingsWindow.getWidth() / 2, centreY - settingsWindow.getHeight() / 2);

        VisLabel title = new VisLabel("Settings", new Label.LabelStyle(customFont, Color.WHITE));
        title.setAlignment(Align.center);
        title.setFontScale(1.2f);

        VisTable header = new VisTable();
        settingsTable = new VisTable();
        close = new VisTextButton("x");

        header.add().expandX();
        header.add(close).right();
        header.row();
        header.add(title).colspan(2).center().padTop(10);
        header.row();

        settingsWindow.add(header).colspan(2).expandX().fillX().row();


        volumeSlider = new VisSlider(0, 1, 0.1f, false, skin.get("default-horizontal", VisSlider.SliderStyle.class));
        volumeSlider.setValue(0.5f);

        ArrayList<Settings> settingsList = new ArrayList<>();

        settingsList.add(new Settings("Гучнiсть", volumeSlider));
        settingsList.add(new Settings("Страшнi звуки", new VisCheckBox("Йоу")));
        settingsList.add(new Settings("Селект бокс", new VisSelectBox<>()));

        for (Settings settings : settingsList) {
            settings.placeSettings();
        }

        settingsWindow.add(settingsTable).colspan(2).expandX().fillX().padTop(10).row();
        settingsWindow.setVisible(false);

        functionalSettings();
    }

    private void functionalSettings() {
        stage.addActor(settingsWindow);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                bgm.setVolume(volume);
            }
        });

        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsWindow.setVisible(false);
            }
        });
    }

    private void playMusic() {
        bgm = Gdx.audio.newMusic(Gdx.files.internal("audio/music/policy-of-truth.mp3"));
        bgm.setVolume(0.5f);
        bgm.setLooping(true);
        bgm.play();
    }

    private void increaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 1.8f, sprite.getHeight() * 1.8f);
    }

    private void decreaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.7f);
    }

    //************          Методи для render          ************
    private void draw(float delta) {
        animateAngel();
        animateContinueButton(delta);


        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        leftWing.draw(batch);
        rightWing.draw(batch);
        head.draw(batch);
        settingsBtn.draw(batch);
        bestiary.draw(batch);
        exitBtn.draw(batch);
        labelSprite.draw(batch);


        batch.setColor(1, 1, 1, alpha);
        batch.draw(continueTexture, centreX - (float) continueTexture.getWidth() / 2, centreY - continueTexture.getHeight() * 2.3f);
        batch.setColor(1, 1, 1, 1);


        hoveredHint();

        stage.act(delta);
        stage.draw();

        batch.end();
    }

    private void animateAngel() {
        float angle = MathUtils.sin(time * speed) * maxAngle;
        leftWing.setOriginCenter();
        rightWing.setOriginCenter();

        leftWing.setRotation(angle);
        rightWing.setRotation(-angle);

        float offsetY = MathUtils.sin(time * speed) * headAmplitude;

        head.setPosition(head.getX(), head.getY() + offsetY);
        leftWing.setPosition(leftWing.getX(), leftWing.getY() + offsetY);
        rightWing.setPosition(rightWing.getX(), rightWing.getY() + offsetY);
    }

    private void animateContinueButton(float delta) {
        if (fadingOut) {
            alpha -= delta;
            if (alpha <= 0) {
                alpha = 0;
                fadingOut = false;
            }
        } else {
            alpha += delta;
            if (alpha >= 1) {
                alpha = 1;
                fadingOut = true;
            }
        }
    }

    private void hoveredHint() {
        if (hoveredHint != null) {
            hoverFont.setColor(Color.BLACK);
            if (hoveredHint.length() == 9) {
                drawButtonHint(bestiary, 9);
                bestiaryOutline.draw(batch);
            } else if (hoveredHint.length() ==7) {
                drawButtonHint(settingsBtn, 7);
                settingOutline.draw(batch);
            } else if (hoveredHint.length() == 4) {
                drawButtonHint(exitBtn, 4);
                keyOutline.draw(batch);
            }
        }
    }

    private void drawButtonHint(Sprite button, float length) {
        float buttonCentreX = button.getX() + button.getWidth() / 2;
        hoverFont.draw(batch, hoveredHint, buttonCentreX - length * 4, button.getY() - 10);
    }

    //************          Ввід користувача          ************
    private void handleInput() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Exit";
        else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Setting";
        else if (bestiary.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Бестіарій";
        else hoveredHint = null;

        if (Gdx.input.justTouched()) {
            if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
                Gdx.app.exit();
            else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y)) {
                settingsWindow.setVisible(true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.GAME);
        }
    }

    private class Settings {
        VisLabel settingsLabel;
        Actor actor;
        private static final float rowSpacing = 10f;
        private static final float labelColumnWidth = 120f;

        public Settings(String label, VisSlider slider) {
            this.settingsLabel = new VisLabel(label);
            this.actor = slider;
        }

        public Settings(String label, VisCheckBox checkbox) {
            this.settingsLabel = new VisLabel(label);
            this.actor = checkbox;
        }

        public Settings(String label, VisSelectBox<?> selectBox) {
            this.settingsLabel = new VisLabel(label);
            this.actor = selectBox;
        }

        public void placeSettings() {
            settingsTable.row().padTop(rowSpacing);
            settingsTable.add(settingsLabel).width(labelColumnWidth).padLeft(30).left();
            settingsTable.add(actor).expandX().fillX().padRight(30).left();
        }

        public Actor getActor() {
            return actor;
        }

    }
}

//    private class PixelPerfectSprite {
//        private final Sprite sprite;
//        private final Pixmap pixmap;
//
//        public PixelPerfectSprite(String texturePath) {
//            Texture texture = new Texture(Gdx.files.internal(texturePath));
//            this.sprite = new Sprite(texture);
//
//            Pixmap source = new Pixmap(Gdx.files.internal(texturePath));
//            this.pixmap = new Pixmap(source.getWidth(), source.getHeight(), source.getFormat());
//            this.pixmap.drawPixmap(source, 0, 0);
//            source.dispose();
//        }
//
//        public Sprite getSprite() {
//            return sprite;
//        }
//
//        public boolean isClicked(float worldX, float worldY) {
//            Rectangle bounds = sprite.getBoundingRectangle();
//            if (!bounds.contains(worldX, worldY)) return false;
//
//            float localX = (worldX - sprite.getX()) / sprite.getWidth();
//            float localY = (worldY - sprite.getY()) / sprite.getHeight();
//
//            int pixelX = (int) (localX * pixmap.getWidth());
//            int pixelY = pixmap.getHeight() - (int) (localY * pixmap.getHeight());
//
//            if (pixelX < 0 || pixelX >= pixmap.getWidth() || pixelY < 0 || pixelY >= pixmap.getHeight())
//                return false;
//
//            int tolerance = 2; // радіус у пікселях
//
//            for (int dx = -tolerance; dx <= tolerance; dx++) {
//                for (int dy = -tolerance; dy <= tolerance; dy++) {
//                    int px = pixelX + dx;
//                    int py = pixelY + dy;
//
//                    if (px < 0 || px >= pixmap.getWidth() || py < 0 || py >= pixmap.getHeight())
//                        continue;
//
//                    int alpha = (pixmap.getPixel(px, py) >> 24) & 0xff;
//                    if (alpha > 10) return true;
//                }
//            }
//            return false;
//        }
//
//        public void dispose() {
//            sprite.getTexture().dispose();
//            pixmap.dispose();
//        }
//    }

