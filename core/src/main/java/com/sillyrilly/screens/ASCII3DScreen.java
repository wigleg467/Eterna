package com.sillyrilly.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.sillyrilly.util.d3.V3;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.sillyrilly.util.d3.Const.*;

public class ASCII3DScreen implements Screen {
    BitmapFont font = new BitmapFont(); // у fb = 8×16 px
    SpriteBatch batch = new SpriteBatch();

    // --- параметри екрану у символах ---
    private static final int cols = 160;
    final int rows = 45;
    char[][] buffer = new char[rows][cols];
    float[] zBuf = new float[rows * cols];

    float angle = 0;

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        angle += delta; // крутимо куб

        // очистка буферів
        Arrays.fill(zBuf, Float.NEGATIVE_INFINITY);
        for (char[] row : buffer) Arrays.fill(row, ' ');

        // проекція всіх вершин — спочатку обертаємо
        V3[] proj = new V3[CUBE_VERTS.length];
        for (int i = 0; i < CUBE_VERTS.length; i++) {
            V3 v = new V3(CUBE_VERTS[i]);
            rotateY(v, angle);
            rotateX(v, angle * 0.5f);
            v.z += 5f;                // відсунути, щоб було >0
            proj[i] = project(v);     // x,y = символи, z = depth
        }

        // перебираємо грані (Painter’s algorithm: сортуємо по середній z)
        Integer[] order = IntStream.range(0, CUBE_FACES.length).boxed().toArray(Integer[]::new);
        Arrays.sort(order, (a, b) -> Float.compare(
            avgZ(CUBE_FACES[b], proj), avgZ(CUBE_FACES[a], proj)));

        for (int idx : order) {
            int[] f = CUBE_FACES[idx];
            // простенький залив – пройдись по ребрам та scanline
            // тут для прикладу тільки вершини малюю:
            for (int vi : f) plot(proj[vi], 1f /*яскравість*/);
        }

        // — Вивід —
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, " ");
        float charW = layout.width;
        float charH = font.getCapHeight();
        for (int y = 0; y < rows; y++)
            font.draw(batch, new String(buffer[y]), 0, (rows - y) * charH);
        batch.end();
    }


    void plot(V3 p, float brightness) {
        int x = Math.round(p.x);
        int y = Math.round(p.y);
        if (x < 0 || x >= cols || y < 0 || y >= rows) return;
        int idx = y * cols + x;
        if (p.z > zBuf[idx]) { // z‑тест
            zBuf[idx] = p.z;
            int shade = Math.min((int)(brightness * (SHADES.length()-1)), SHADES.length()-1);
            buffer[y][x] = SHADES.charAt(shade);
        }
    }

    V3 project(V3 v) {
        float f = 1.0f / (float)Math.tan(Math.toRadians(60) / 2);
        float x = (v.x * f) / v.z;
        float y = (v.y * f) / v.z;
        return new V3(
            (x + 1) * 0.5f * (cols-1),
            (1 - (y + 1) * 0.5f) * (rows-1),
            v.z);
    }

    void rotateY(V3 v, float a) {
        float s = MathUtils.sin(a), c = MathUtils.cos(a);
        float x = v.x * c - v.z * s;
        float z = v.x * s + v.z * c;
        v.x = x; v.z = z;
    }
    void rotateX(V3 v, float a) {
        float s = MathUtils.sin(a), c = MathUtils.cos(a);
        float y = v.y * c - v.z * s;
        float z = v.y * s + v.z * c;
        v.y = y; v.z = z;
    }
    float avgZ(int[] f, V3[] p) {
        float sum = 0; for (int idx : f) sum += p[idx].z; return sum / f.length;
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
        font.dispose();
    }
}
