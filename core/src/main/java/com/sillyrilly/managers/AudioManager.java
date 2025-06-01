package com.sillyrilly.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private static AudioManager instance;

    private Music music;
    private Sound clickSound; // як приклад звуку

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    public void load() {
        //      Приблизно так
        //   music = Gdx.audio.newMusic(Gdx.files.internal("audio/music.mp3"));
        //   clickSound = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));
    }

    public void playMusic() {
        if (music != null) {
            // music.setLooping(true);
            music.play();
        }
    }

    public void stopMusic() {
        if (music != null) music.stop();
    }

    public void dispose() {
        music.dispose();
        clickSound.dispose();
    }
}

