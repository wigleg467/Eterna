package com.sillyrilly.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;
import com.sillyrilly.managers.AudioManager;
import com.sillyrilly.managers.CameraManager;
import com.sillyrilly.managers.InputManager;
import com.sillyrilly.managers.ScreenManager;

import java.util.ArrayList;

public class MenuScreen implements Screen {
    private static final Color DARK_RED = new Color(111 / 255f, 54 / 255f, 54 / 255f, 1f);
    private static final float WIDTH = 1280;
    private static final float HEIGHT = 720;
    static final float CENTRE_X = WIDTH / 2;
    private static final float CENTRE_Y = HEIGHT / 2;
    private static final int PADDING = 30;
    private static final float MAX_ANGLE = 4f;
    private static final float SPEED = 2f;
    private static final float FLOAT = 0.1f;

    private boolean fadingOut = true;
    private float alpha = 1f;
    private float time = 0;

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture texHead, texSettings, texExit, texWings, texBook,
        keyOutlineTexture, settingOutlineTexture,
        bestiaryOutlineTexture, labelTexture,
        backgroundTexture, continueTexture;
    private Sprite head, leftWing,
        rightWing, keyOutline, settingOutline,
        bestiaryOutline, labelSprite,
        settingsBtn, exitBtn, bestiaryBtn;
    private Stage stage;
    private VisTextButton close;
    private VisSlider volumeSlider;
    private VisTable settingsTable;
    private VisWindow settingsWindow;
    private BestiaryWindow bestiaryWindow;
    private BitmapFont customFont, hoverFont;
    private String hoveredHint;
    private Skin skin;
    Window.WindowStyle style;


    private boolean initialized = false;

    /**
     * Called when this screen becomes the current screen for a {@link Game}.
     */
    @Override
    public void show() {
        initFOV();

        if (!initialized) {
            loadTextures();
            createSprites();
            initFonts();
            initSettingWindow();
            initBestiaryWindow();
            initialized = true;
        }
    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        clearAndUpdate(delta);
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
        InputManager.getInstance().getMultiplexer().removeProcessor(stage);
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
        skin.dispose();
        VisUI.dispose();
    }

    //##################### Методи для show #####################

    private void initFOV() {
        viewport = CameraManager.getInstance().getViewport();
        viewport.apply();

        camera = CameraManager.getInstance().getCamera();
        if (!initialized) camera.position.set(CENTRE_X, CENTRE_Y, 0);
        camera.zoom = 1f;
        camera.update();

        if (!initialized) stage = new Stage(new ScreenViewport());
        InputManager.getInstance().getMultiplexer().addProcessor(stage);
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
        head.setPosition(CENTRE_X - head.getWidth() / 2, CENTRE_Y - head.getHeight() / 2);

        leftWing = new Sprite(texWings);
        increaseSize(leftWing);
        leftWing.setPosition(CENTRE_X - leftWing.getWidth(), CENTRE_Y - leftWing.getHeight() / 2 + 15);

        rightWing = new Sprite(texWings);
        rightWing.flip(true, false);
        increaseSize(rightWing);
        rightWing.setPosition(CENTRE_X, CENTRE_Y - rightWing.getHeight() / 2 + 15);

        settingsBtn = new Sprite(texSettings);
        settingOutline = new Sprite(settingOutlineTexture);
        decreaseSize(settingsBtn);
        decreaseSize(settingOutline);

        settingsBtn.setPosition(WIDTH - settingsBtn.getWidth() - PADDING, PADDING);
        settingOutline.setPosition(settingsBtn.getX(), settingsBtn.getY());

        exitBtn = new Sprite(texExit);
        keyOutline = new Sprite(keyOutlineTexture);
        exitBtn.setPosition(WIDTH - exitBtn.getWidth() - PADDING, HEIGHT - exitBtn.getHeight() - PADDING);
        keyOutline.setPosition(exitBtn.getX() - 2, exitBtn.getY() - 2);

        bestiaryBtn = new Sprite(texBook);
        bestiaryOutline = new Sprite(bestiaryOutlineTexture);
        bestiaryBtn.setPosition(PADDING, PADDING);
        bestiaryOutline.setPosition(bestiaryBtn.getX() - 2, bestiaryBtn.getY() - 2);

        labelSprite = new Sprite(labelTexture);
        labelSprite.setSize(labelSprite.getWidth() * 0.4f, labelSprite.getHeight() * 0.4f);
        labelSprite.setPosition(CENTRE_X - labelSprite.getWidth() / 2, HEIGHT - labelSprite.getHeight() * 2.3f);
    }

    private void initFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter paramCustom = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramCustom.size = 24;
        customFont = generator.generateFont(paramCustom);

        FreeTypeFontGenerator.FreeTypeFontParameter paramHover = new FreeTypeFontGenerator.FreeTypeFontParameter();
        paramHover.size = 20;
        paramHover.characters = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯ" +
                                "абвгґдеєжзиіїйклмнопрстуфхцчшщьюя" +
                                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!?.,:;(){}[]«»–-";

        hoverFont = generator.generateFont(paramHover);
        hoverFont.getData().markupEnabled = true;

        generator.dispose();
    }

    private void initSettingWindow() {
        VisUI.load();
        //##################### Стиль для вікна #####################
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        style = new Window.WindowStyle(skin.get("default", Window.WindowStyle.class));
        style.background = skin.newDrawable("white", DARK_RED);
        style.titleFont = customFont;

        settingsWindow = new VisWindow("", style);
        settingsWindow.top().padTop(0);

        settingsWindow.setSize(400, 300);
        settingsWindow.setPosition(CENTRE_X - settingsWindow.getWidth() / 2, CENTRE_Y - settingsWindow.getHeight() / 2);

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

        volumeSlider = new VisSlider(0, 1, 0.05f, false, skin.get("default-horizontal", VisSlider.SliderStyle.class));
        volumeSlider.setValue(0.05f);

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

    private void initBestiaryWindow(){
        Array<EnemyInfo> enemies = new Array<>();

        TextureAtlas watermelonAtlas = new TextureAtlas(Gdx.files.internal("animations/watermelon.atlas"));
        TextureAtlas angelAtlas = new TextureAtlas(Gdx.files.internal("animations/angel.atlas"));
        TextureAtlas mummyAtlas = new TextureAtlas(Gdx.files.internal("animations/mummy.atlas"));
        TextureAtlas skeletonAtlas = new TextureAtlas(Gdx.files.internal("animations/skeleton.atlas"));
        TextureAtlas zombieAtlas = new TextureAtlas(Gdx.files.internal("animations/zombie.atlas"));
        TextureAtlas guardAtlas = new TextureAtlas(Gdx.files.internal("animations/guard.atlas"));


        enemies.add(new EnemyInfo("Кавуняра", "     Нехай вас не обманює назва цього овоча-нападника. Замiсть бази вiн видає лиш влучнi удари", watermelonAtlas, 0.5f));
        enemies.add(new EnemyInfo("Янголятко", "        Це не дитинка з крилами, яку ви звикли бачити в картинах часiв рококо. Цей янгол дасть зрозумiти, чому у канонiчному письмi, вони говорять Do not fear", angelAtlas, 0.5f));
        enemies.add(new EnemyInfo("Мумiя", "        Лiто, спека, море, Египет-... МУМIЯ?!.", mummyAtlas, 0.5f));
        enemies.add(new EnemyInfo("Скелет", "       Можете спитати пораду про дiету, але ви певно не встигнете", skeletonAtlas, 0.5f));
        enemies.add(new EnemyInfo("Зомбі", "       Колись був такий самий як ми... Хто знае, може він теж намагався врятувати свою кохану", zombieAtlas, 0.9f));
        enemies.add(new EnemyInfo("Охоронець пекла", "       Охороняe вхiд до пекла i сумлінно виконує своi обов'язки. Все ж так просто у iнший свiт не можна пройти.", guardAtlas, 0.3f));


         bestiaryWindow = new BestiaryWindow(enemies, style);
        bestiaryWindow.setVisible(false);
        stage.addActor(bestiaryWindow);
    }

    private void functionalSettings() {
        stage.addActor(settingsWindow);

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                AudioManager.getInstance().setVolume(volume);
            }
        });

        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settingsWindow.setVisible(false);
            }
        });
    }

    private void increaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 1.8f, sprite.getHeight() * 1.8f);
    }

    private void decreaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.7f);
    }

    //##################### Методи для render #####################

    private void clearAndUpdate(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += delta;

        if (initialized) camera.position.set(CENTRE_X, CENTRE_Y, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    private void draw(float delta) {
        animateAngel();
        animateContinueButton(delta);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        leftWing.draw(batch);
        rightWing.draw(batch);
        head.draw(batch);
        settingsBtn.draw(batch);
        bestiaryBtn.draw(batch);
        exitBtn.draw(batch);
        labelSprite.draw(batch);

        batch.setColor(1, 1, 1, alpha);
        batch.draw(continueTexture, CENTRE_X - (float) continueTexture.getWidth() / 2, CENTRE_Y - continueTexture.getHeight() * 2.3f);
        batch.setColor(1, 1, 1, 1);

        hoveredHint();

        bestiaryWindow.act(delta);
        stage.act(delta);
        stage.draw();

        batch.end();
    }

    private void animateAngel() {
        float angle = MathUtils.sin(time * SPEED) * MAX_ANGLE;
        float offsetY = MathUtils.sin(time * SPEED) * FLOAT;

        leftWing.setOriginCenter();
        rightWing.setOriginCenter();

        leftWing.setRotation(angle);
        rightWing.setRotation(-angle);

        leftWing.setPosition(leftWing.getX(), leftWing.getY() + offsetY);
        rightWing.setPosition(rightWing.getX(), rightWing.getY() + offsetY);

        head.setPosition(head.getX(), head.getY() + offsetY);
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
            switch (hoveredHint.length()) {
                case 4:
                    drawButtonHint(exitBtn, 4);
                    keyOutline.draw(batch);
                    break;
                case 7:
                    drawButtonHint(settingsBtn, 7);
                    settingOutline.draw(batch);
                    break;
                case 9:
                    drawButtonHint(bestiaryBtn, 9);
                    bestiaryOutline.draw(batch);
                    break;
            }
        }
    }

    private void drawButtonHint(Sprite button, float length) {
        float buttonCentreX = button.getX() + button.getWidth() / 2;
        hoverFont.draw(batch, hoveredHint, buttonCentreX - length * 4, button.getY() - 10);
    }

    //##################### Обробка подій #####################

    private void handleInput() {
        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        CameraManager.getInstance().getCamera().unproject(mouse);

        if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Exit";
        else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Setting";
        else if (bestiaryBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
            hoveredHint = "Бестіарій";
        else hoveredHint = null;

        if (Gdx.input.justTouched()) {
            if (exitBtn.getBoundingRectangle().contains(mouse.x, mouse.y))
                Gdx.app.exit();
            else if (settingsBtn.getBoundingRectangle().contains(mouse.x, mouse.y)) {
                settingsWindow.setVisible(true);
            }
            else if(bestiaryBtn.getBoundingRectangle().contains(mouse.x, mouse.y)) {
                bestiaryWindow.setVisible(true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            ScreenManager.getInstance().setScreen(ScreenManager.ScreenType.GAME);
        }
    }

    //##################### Інше #####################

    private class Settings {
        private static final float rowSpacing = 10f;
        private static final float labelColumnWidth = 120f;

        private final VisLabel settingsLabel;
        private final Actor actor;

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
    }
    public class EnemyInfo {
        public final String name;
        public final String description;
        public final Animation<TextureAtlas.AtlasRegion> animation;
        public final float frameDuration;

        public EnemyInfo(String name, String description, TextureAtlas atlas, float frameDuration) {
            this.name = name;
            this.description = description;
            this.frameDuration = frameDuration;
            this.animation = new Animation<>(frameDuration, atlas.findRegions("walk_right"), Animation.PlayMode.LOOP);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public class BestiaryWindow extends VisWindow {
        private final VisList<EnemyInfo> enemyList;
        private final VisImage image;
        private final VisLabel descriptionLabel;
        private final Array<EnemyInfo> enemyInfos;
        private float animTime = 0f;

        public BestiaryWindow(Array<EnemyInfo> enemies, WindowStyle style) {
            super("", style);
            this.enemyInfos = enemies;

            this.setSize(600, 400);
            this.setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2f,
                    Gdx.graphics.getHeight() / 2f - getHeight() / 2f);

            // Список ворогів
            enemyList = new VisList<>();
            enemyList.setItems(enemies);

            // Зображення і опис
            descriptionLabel = new VisLabel("");
            descriptionLabel.setWrap(true);
            descriptionLabel.setAlignment(Align.topLeft);

            image = new VisImage();

            VisTable rightPanel = new VisTable(true);
            rightPanel.add(image).size(240, 128).center().row();
            rightPanel.add(descriptionLabel).width(300).padTop(10).top().left();

            VisSplitPane splitPane = new VisSplitPane(enemyList, rightPanel, false);
            splitPane.setSplitAmount(0.3f);


//            VisTable header = new VisTable();
//            header.add().expandX();
//            header.add(close).top().right().pad(5);
//            header.row();
//            header.add(new VisLabel(" ")).colspan(2).padBottom(5); // для відступу
//            this.add(header).expandX().fillX().row();

            VisLabel title = new VisLabel("Bestiary", new Label.LabelStyle(customFont, Color.WHITE));
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

            this.add(header).colspan(2).expandX().fillX().row();

            this.add(splitPane).expand().fill().row();

            // Обробник закриття
            close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setVisible(false);
                }
            });

            // Встановити стартовий ворог
            enemyList.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    updatePreview(enemyList.getSelected());
                }
            });

            if (enemyList.getItems().size > 0) {
                updatePreview(enemyList.getItems().first());
            }
        }


        private void updatePreview(EnemyInfo info) {
            descriptionLabel.setText(info.description);
            animTime = 0f;

            float scale_width=10f;
            float scale_height=1.5f;

            // якщо потрібно малювати кастомно — треба буде віддати Animation або кадр
            TextureRegion frame = info.animation.getKeyFrame(0);
            image.setDrawable(new TextureRegionDrawable(frame));
            //   image.setSize(frame.getRegionWidth() * 0.5f, frame.getRegionHeight() * 0.5f);
        }

        public void act(float delta) {
            super.act(delta);
            animTime += delta;

            if (enemyList.getSelected() != null) {
                TextureRegion frame = enemyList.getSelected().animation.getKeyFrame(animTime, true);
                image.setDrawable(new TextureRegionDrawable(frame));
            }
        }
    }
}

