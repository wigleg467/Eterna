package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

import static com.sillyrilly.util.Const.*;

public class AudioManager implements Disposable {
    private static final Music[] mainTheme = new Music[23];
    private static final Sound[] steps = new Sound[5];
    public static AudioManager instance;
    private static Music basementTheme;
    private static Music hellTheme;
    private static Music forkTheme;
    private static boolean isInitialized = false;
    private float globalVolume = AUDIO_VOLUME;

    private AudioManager() {
    }

    public static AudioManager initialize() {
        if (!isInitialized) {
            instance = new AudioManager();

            // Musics init
            for (int i = 0; i < mainTheme.length; i++)
                mainTheme[i] = Gdx.audio.newMusic(Gdx.files.internal
                    (MUSIC_FOLDER_PATH + "main-" + (i + 1) + AUDIO_FORMAT));
            basementTheme = Gdx.audio.newMusic(Gdx.files.internal
                (MUSIC_FOLDER_PATH + "basement" + AUDIO_FORMAT));
            hellTheme = Gdx.audio.newMusic(Gdx.files.internal
                ("audio/music/hell.wav"));
            forkTheme = Gdx.audio.newMusic(Gdx.files.internal
                (MUSIC_FOLDER_PATH + "fork" + AUDIO_FORMAT));

            // Sounds init
            for (int i = 0; i < steps.length; i++)
                steps[i] = Gdx.audio.newSound(Gdx.files.internal
                    (SOUND_FOLDER_PATH + "footstep-" + (i + 1) + AUDIO_FORMAT));

            isInitialized = true;
        }
        return instance;
    }

    public void playMusic(MusicType type) {
        switch (type) {
            case MAIN_THEME -> playMainMusic(globalVolume);
            case BASEMENT -> playBasementMusic(globalVolume);
            case HELL -> playHellThem(globalVolume);
        }
    }

    public void playSound(SoundType type) {
        switch (type) {
            case STEPS -> playStep();
        }
    }

    public void setVolume(float volume) {
        globalVolume = volume;
        for (Music m : mainTheme) m.setVolume(volume);
        basementTheme.setVolume(volume);
        hellTheme.setVolume(volume);
    }

    public void setVolume(float volume, MusicType type) {
        volume = volume * globalVolume;
        switch (type) {
            case MAIN_THEME -> {
                for (Music m : mainTheme) m.setVolume(volume);
            }
            case BASEMENT -> basementTheme.setVolume(volume);
            case HELL -> hellTheme.setVolume(volume);
        }
    }

    public void dispose() {
        for (Music m : mainTheme) m.dispose();
        basementTheme.dispose();
        hellTheme.dispose();
        forkTheme.dispose();

        for (Sound s : steps) s.dispose();
    }

    private void pauseAll() {
        for (Music m : mainTheme) m.pause();
        basementTheme.pause();
        hellTheme.pause();
        for (Sound s : steps) s.stop();
    }

    private void playStep() {
        float volume = 1f;
        if (globalVolume == 0) volume = 0f;
        steps[new Random().nextInt(steps.length)].play(volume);
    }

    private void playMainMusic(float volume) {
        if (mainTheme != null) {
            pauseAll();
            int num = new Random().nextInt(mainTheme.length);
            mainTheme[num].play();
            setVolume(volume, MusicType.MAIN_THEME);

            mainTheme[num].setOnCompletionListener(m ->
                playMainMusic(mainTheme[num].getVolume())
            );
        }
    }

    private void playBasementMusic(float volume) {
        if (basementTheme != null) {
            pauseAll();
            basementTheme.play();
            basementTheme.setLooping(true);
            setVolume(volume, MusicType.BASEMENT);

            hellTheme.play();
            setVolume(0f, MusicType.HELL);
            hellTheme.setLooping(true);
        }
    }

    private void playHellThem(float volume) {
        pauseAll();
        hellTheme.play();
        hellTheme.setLooping(true);
        setVolume(volume, MusicType.HELL);
    }

    public enum MusicType {
        MAIN_THEME, BASEMENT, HELL, HEAVEN, FORK
    }

    public enum SoundType {
        STEPS
    }
}

