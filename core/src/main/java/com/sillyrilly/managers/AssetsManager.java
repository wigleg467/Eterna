package com.sillyrilly.managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import static com.sillyrilly.util.MenuConfig.*;

public class AssetsManager {
    public static AssetsManager instance;

    // Menu texture
    public static Texture texHead, texSettings, texExit, texWings, texBook,
            keyOutlineTexture, settingOutlineTexture,
            bestiaryOutlineTexture, labelTexture,
            backgroundTexture, continueTexture;

    // Menu Sprites
    public static Sprite head, leftWing,
            rightWing, keyOutline, settingOutline,
            bestiaryOutline, labelSprite,
            settingsBtn, exitBtn, bestiaryBtn;

    public static TiledMap bigWorld;


    private static boolean isInitialized = false;

    private AssetsManager() {
    }

    public static void initialize() {
        if (!isInitialized) {
            instance = new AssetsManager();

            instance.loadMenuTextures();
            instance.createMenuSprites();
            instance.loadMaps();

            isInitialized = true;
        }
    }

    public void dispose() {
        texHead.dispose();
        texSettings.dispose();
        texExit.dispose();
        texWings.dispose();
        texBook.dispose();
        keyOutlineTexture.dispose();
        settingOutlineTexture.dispose();
        bestiaryOutlineTexture.dispose();
        labelTexture.dispose();
        backgroundTexture.dispose();
        continueTexture.dispose();

        bigWorld.dispose();
    }

    private void loadMaps() {
        bigWorld = new TmxMapLoader().load("maps/bigmap.tmx");
    }

    private void loadMenuTextures() {
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

    private void createMenuSprites() {
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

    private void increaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 1.8f, sprite.getHeight() * 1.8f);
    }

    private void decreaseSize(Sprite sprite) {
        sprite.setSize(sprite.getWidth() * 0.7f, sprite.getHeight() * 0.7f);
    }
}
