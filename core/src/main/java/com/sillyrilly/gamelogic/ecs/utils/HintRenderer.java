package com.sillyrilly.gamelogic.ecs.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import static com.sillyrilly.util.Const.PPM;

public class HintRenderer {
    private BitmapFont font;
    private String hintText = "";
    private Vector2 screenPos = new Vector2();
    private boolean visible = false;
    private float npcWidth;

    public HintRenderer(BitmapFont font) {
        this.font = font;
        font.getData().setScale(0.6f);
    }

    public void showHint(String text, Vector2 worldPos) {
        this.hintText = text;
        // Vector3 projected = CameraManager.camera.project(new Vector3(worldPos, 0));
        screenPos.set(worldPos.x * PPM, worldPos.y * PPM);
        visible = true;
    }

    public void hideHint() {
        this.visible = false;
    }

    public void render(SpriteBatch batch) {
        if (!visible || hintText == null) return;

        font.draw(batch, hintText, screenPos.x - 160, screenPos.y + 100);
    }
}
