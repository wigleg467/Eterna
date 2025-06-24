package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import static com.sillyrilly.util.ASCIIConfig.CHARACTER_LIST;
import static com.sillyrilly.util.MenuConfig.MENU_CHARACTERS;

public class FontManager implements Disposable {
    public static FontManager instance;

    // Default font
    public static BitmapFont defaultFont;

    // Menu
    public static BitmapFont MENU_mainFont;
    public static BitmapFont MENU_hoverFont;
    public static BitmapFont GAME_hoverFont;


    // ASCII
    public static BitmapFont ASCII_mainFont;
    public static BitmapFont ASCII_minimapFont;
    public static BitmapFont.Glyph ASCII_characterGlyph;
    public static TextureRegion ASCII_characterRegion;

    private static boolean isInitialized = false;

    private FontManager() {
    }

    public static void initialize() {
        if (!isInitialized) {
            instance = new FontManager();

            defaultFont = new BitmapFont();

            FreeTypeFontGenerator menuGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/settings.ttf"));
            FreeTypeFontGenerator asciiGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/CascadiaMono-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

            param.characters = MENU_CHARACTERS;
            param.size = 24;
            MENU_mainFont = menuGen.generateFont(param);
            param.size = 20;
            MENU_hoverFont = menuGen.generateFont(param);
            MENU_hoverFont.getData().markupEnabled = true;

            param.characters = CHARACTER_LIST;
            param.size = 14;
            ASCII_mainFont = asciiGen.generateFont(param);
            param.size = 16;
            ASCII_minimapFont = asciiGen.generateFont(param);

            param.characters = MENU_CHARACTERS;
            param.size = 17;
            GAME_hoverFont = menuGen.generateFont(param);
            GAME_hoverFont.getData().markupEnabled = true;

            assert ASCII_minimapFont != null;
            ASCII_characterGlyph = ASCII_minimapFont.getData().getGlyph('>');
            assert ASCII_characterGlyph != null;
            ASCII_characterRegion = new TextureRegion(ASCII_minimapFont.getRegion().getTexture(),
                    ASCII_characterGlyph.srcX, ASCII_characterGlyph.srcY, ASCII_characterGlyph.width, ASCII_characterGlyph.height);

            menuGen.dispose();
            asciiGen.dispose();

            isInitialized = true;
        }
    }

    public void dispose() {
        defaultFont.dispose();

        MENU_mainFont.dispose();
        MENU_hoverFont.dispose();

        ASCII_mainFont.dispose();
        ASCII_minimapFont.dispose();
    }
}
