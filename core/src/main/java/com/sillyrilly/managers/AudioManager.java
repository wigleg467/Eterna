package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

public class AudioManager implements Disposable {
    private static AudioManager instance;

    private final Music mainTheme;
    private final Music basementTheme;
    private final Music hellTheme;
    private static final Sound[] steps = new Sound[5];

    public float globalVolume = 0.4f;

    private AudioManager() {
        mainTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/music/The-Return-of-the-Obra-Dinn-OST.ogg"));
        basementTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/music/Cave-Ambience-ASMR-Loop.ogg"));
        hellTheme = Gdx.audio.newMusic(Gdx.files.internal("audio/music/Hell.wav"));
        for (int i = 0; i < steps.length; i++) {
            steps[i] = Gdx.audio.newSound(
                Gdx.files.internal("audio/sfx/footstep-" + (i + 1) + ".mp3"));
        }
        setVolume(globalVolume);
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    public void playStep(float volume) {
        if (globalVolume == 0) volume = 0f;
        steps[new Random().nextInt(steps.length)].play(volume);
    }

    public void playMainMusic(boolean loop, float volume) {
        if (mainTheme != null) {
            pauseAll();
            mainTheme.play();
            mainTheme.pause();
            mainTheme.setLooping(loop);
            setVolume(volume, 0);

            float[] jump = {0, 177, 198, 277, 343, 410, 475, 540, 602, 676, 737,
                797, 858, 918, 987, 1048, 1092, 1137, 1179, 1220,
                1284, 1350, 1417, 1472, 1480, 1502};

            mainTheme.setPosition(jump[MathUtils.random(jump.length - 1)]);
            mainTheme.play();
        }
    }

    public void playBasementMusic(boolean loop, float volume) {
        if (basementTheme != null) {
            pauseAll();
            basementTheme.play();
            basementTheme.setLooping(loop);
            setVolume(volume, 1);

            hellTheme.play();
            setVolume(0f, 2);
            hellTheme.setLooping(loop);
        }
    }

    public void playHellThem(boolean loop, float volume) {
        pauseAll();
        hellTheme.play();
        hellTheme.setLooping(loop);
        setVolume(volume, 2);
    }

    public void setVolume(float volume) {
        mainTheme.setVolume(volume);
        basementTheme.setVolume(volume);
        hellTheme.setVolume(volume);
        globalVolume = volume;
    }

    public void setVolume(float volume, int number) {
        volume = volume * globalVolume;
        switch (number) {
            ///
            case 0 -> mainTheme.setVolume(volume);
            case 1 -> basementTheme.setVolume(volume);
            case 2 -> hellTheme.setVolume(volume);
        }
    }

    public void pauseAll() {
        mainTheme.pause();
        basementTheme.pause();
        hellTheme.pause();
    }

    public void dispose() {
        basementTheme.dispose();
        mainTheme.dispose();
        for (Sound s : steps) s.dispose();
    }
}

