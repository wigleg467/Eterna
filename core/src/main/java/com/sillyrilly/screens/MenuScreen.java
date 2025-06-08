package com.sillyrilly.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.sillyrilly.managers.CameraManager;

import java.util.ArrayList;

public class MenuScreen implements Screen {
    private Game game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture texHead, texEye, texNewGame, texSettings, texExit, texWings, texBook, keyOutlineTexture, settingOutlineTexture, bestiaryOutlineTexture;
    private Sprite head, eye, newGameBtn, settingsBtn, exitBtn, leftWing, rightWing, bestiary, keyOutline, settingOutline, bestiaryOutline;
    private ShapeRenderer shapeRenderer;
    private TextureRegion textureRegion;
    private static final float width = 1280, height = 720;
    private static final int padding = 30;
    private BitmapFont font;
    private String hoveredHint = null;
    private Stage stage;
    private Window settingsWindow;
    private VisTextButton close;
    private Slider volumeSlider;
    private VisTable settingsTable;
    private BitmapFont customFont;

    private Music bgm;
    private Skin skin;
    private float centreX = width / 2;
    private float centreY = height / 2;
    private ArrayList<Settings> settingsList;


    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        camera = CameraManager.getInstance().getCamera();
        viewport = CameraManager.getInstance().getViewport();
        viewport.apply();
        camera.position.set(centreX, centreY, 0);
        camera.update();

        bgm = Gdx.audio.newMusic(Gdx.files.internal("audio/music/policy-of-truth.mp3"));
        bgm.setLooping(true);
        bgm.play();

        VisUI.load();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        initializeFonts();
        initializeSettingWindow();
        functionalSettings();

        loadTextures();
        createSprites();

        font = new BitmapFont();
        font.getData().setScale(1.2f);
    }

    private void initializeFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.BLACK;
        customFont = generator.generateFont(parameter);
        generator.dispose();
    }

    private void initializeSettingWindow() {
        skin = VisUI.getSkin();
        Window.WindowStyle style = new Window.WindowStyle(skin.get("default", Window.WindowStyle.class));
        style.background = skin.newDrawable("white", new Color(111 / 255f, 54 / 255f, 54 / 255f, 1f));

        style.titleFont = customFont;

        //ТЕСТОВА КАРТИНКА

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


        Skin skinknob = new Skin(Gdx.files.internal("uiskin.json"));
        volumeSlider = new Slider(0, 1, 0.1f, false, skinknob, "default-horizontal");
        volumeSlider.setValue(0.005f);

        System.out.println(volumeSlider.getStyle().background);


        settingsList = new ArrayList<>();

        settingsList.add(new Settings("Гучнiсть", volumeSlider));
        settingsList.add(new Settings("Страшнi звуки", new CheckBox("йоу", skinknob)));
        settingsList.add(new Settings("Селект бокс", new SelectBox<>(skinknob)));

        for (Settings settings : settingsList) {
            settings.placeSettings();
        }

        settingsWindow.add(settingsTable).colspan(2).expandX().fillX().padTop(10).row();
        settingsWindow.setVisible(false);
        stage.addActor(settingsWindow);


    }

    private void functionalSettings() {
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

    private void loadTextures() {
        texHead = new Texture("images/angel_head.png");
        texWings = new Texture("images/wing.png");
        texSettings = new Texture("images/settings.png");
        settingOutlineTexture = new Texture("images/settingsOutline.png");
        texExit = new Texture("images/key-cut.png");
        keyOutlineTexture = new Texture("images/keyOutline.png");
        texBook = new Texture("images/bestiary.png");
        bestiaryOutlineTexture = new Texture("images/bestiaryOutline.png");
    }

    private void createSprites() {
        batch = new SpriteBatch();

        head = new Sprite(texHead);
        increaseSize(head);
        head.setPosition(centreX - head.getWidth() / 2, centreY - head.getHeight() / 2);

        leftWing = new Sprite(texWings);
        increaseSize(leftWing);
        leftWing.setPosition(centreX - leftWing.getWidth(), centreY - leftWing.getHeight() / 2);

        rightWing = new Sprite(texWings);
        rightWing.flip(true, false);
        increaseSize(rightWing);
        rightWing.setPosition(centreX, centreY - rightWing.getHeight() / 2);

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
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        handleInput();

        draw(delta);


//        Gdx.gl.glLineWidth(1);
//        shapeRenderer.setProjectionMatrix(camera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.Red);
////        shapeRenderer.rect(bestiary.getX(), bestiary.getY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight());
//
//        shapeRenderer.end();
    }

    public void draw(float delta) {
        batch.begin();
        leftWing.draw(batch);
        rightWing.draw(batch);
        head.draw(batch);
        settingsBtn.draw(batch);
        bestiary.draw(batch);
        exitBtn.draw(batch);

        hoveredHint();

        stage.act(delta);
        stage.draw();

        batch.end();
    }

    private void hoveredHint() {
        if (hoveredHint != null) {
            font.setColor(Color.BLACK);
            if (hoveredHint.length() == 8) {
                drawButtonHint(bestiary, 8);
                bestiaryOutline.draw(batch);
            } else if (hoveredHint.length() == 7) {
                drawButtonHint(settingsBtn, 7);
                settingOutline.draw(batch);
            } else if (hoveredHint.length() == 4) {
                drawButtonHint(exitBtn, 4);
                keyOutline.draw(batch);
            }
        }
    }


    private void handleInput() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);
        if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Exit";
        else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Setting";
        else if (bestiary.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Bestiary";
        else hoveredHint = null;

        if (Gdx.input.justTouched()) {
            if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
                Gdx.app.exit();
            else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y)) {
                settingsWindow.setVisible(true);
            }
        }
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
    }


    private void increaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 1.8f, sprite.getHeight() * 1.8f);
    }

    private void decreaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.7f);
    }

    private void drawButtonHint(Sprite button, float length) {
        float buttonCentreX = button.getX() + button.getWidth() / 2;
        font.draw(batch, hoveredHint, buttonCentreX - length * 4, button.getY() - 10);
    }

    private class Settings {
        VisLabel settingsLabel;
        Actor actor;
        private static final float rowSpacing = 10f;
        private static final float labelColumnWidth = 120f;

        public Settings(String label, Slider slider) {
            this.settingsLabel = new VisLabel(label);
            this.actor = slider;
        }

        public Settings(String label, CheckBox checkbox) {
            this.settingsLabel = new VisLabel(label);
            this.actor = checkbox;
        }

        public Settings(String label, SelectBox<?> selectBox) {
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

