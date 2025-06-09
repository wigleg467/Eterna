package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class AudioManager implements Disposable {
    private static AudioManager instance;

    private Music music;
    // private Sound clickSound; // як приклад звуку

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    public void load() {
        //      Приблизно так
        music = Gdx.audio.newMusic(Gdx.files.internal("audio/music/policy-of-truth.mp3"));
        playMusic(true);
        setVolume(0.05f);
        //   clickSound = Gdx.audio.newSound(Gdx.files.internal("audio/click.wav"));
    }

    public void playMusic(boolean loop) {
        if (music != null) {
            music.setLooping(loop);
            music.play();
        }
    }

    public void stopMusic() {
        if (music != null) music.stop();
    }

    public void setVolume(float volume) {
        music.setVolume(volume);
    }

    public void dispose() {
        music.dispose();
        // clickSound.dispose();
    }
}

