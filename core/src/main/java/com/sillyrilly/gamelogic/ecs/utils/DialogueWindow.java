package com.sillyrilly.gamelogic.ecs.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class DialogueWindow {
    private Texture background;
    private BitmapFont font;
    private String portrait;
    private Texture portraitTexture;
    private Array<String> lines;
    private int currentLineIndex;
    private boolean visible = false;
    private String name;
    public Runnable onDialogueEnd;


        public DialogueWindow(Texture background, BitmapFont font) {
            this.background = background;
            this.font = font;
            this.lines = new Array<>();
        }

        public void showDialogue(Dialogue dialogue) {
            this.name=dialogue.name;
            this.portrait = dialogue.portraitPath;
            this.lines.clear();
            this.lines.addAll(dialogue.lines);
            this.currentLineIndex = 0;
            this.visible = true;

            if (portraitTexture != null) portraitTexture.dispose();
            portraitTexture = new Texture(portrait);
        }

    public void nextLine() {
        if (!visible) return;

        if (currentLineIndex >= lines.size - 1) {
            visible = false;
            if (onDialogueEnd != null) onDialogueEnd.run();
        } else {
            currentLineIndex++;
        }
    }

        public boolean isVisible() {
            return visible;
        }
        public void render(SpriteBatch batch) {
            float width = background.getWidth();
            float height = background.getHeight();
            float x = (float) Gdx.graphics.getWidth() /2-width/2;
            float y = 0;


            // портрет персонажа
            if (portrait != null&&this.isVisible()) {
                batch.draw(background, x, y, width, height);
                Texture portraitTexture = new Texture(portrait);
               batch.draw(portraitTexture, x + 22, y + 17, (float) portraitTexture.getWidth() /3, (float) portraitTexture.getHeight() /3);
                // ім’я
                font.draw(batch, name, x + 174, y +152);

                // текст діалогу
                font.draw(batch, lines.get(currentLineIndex), x + 161, y + 118);
            }


        }
    }
