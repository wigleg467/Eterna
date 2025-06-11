package com.sillyrilly.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;

public class AudioManager implements Disposable {
    private static AudioManager instance;

    private Music music;

    private boolean loaded = false;

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    public void load() {
        //      Приблизно так
        music = Gdx.audio.newMusic(Gdx.files.internal("audio/music/The-Return-of-the-Obra-Dinn-OST.mp3"));
        new Thread(() -> playMusic(true)).start();
    }

    public void playMusic(boolean loop) {
        if (music != null) {
            music.setLooping(loop);
            music.play();
            setVolume(0.05f);
            music.pause();
            switch (new Random().nextInt(0, 26)) {
                case 0:
                    music.setPosition(0);
                    break;
                case 1:
                    music.setPosition(177); // 02:57
                    break;
                case 2:
                    music.setPosition(198); // 03:18
                    break;
                case 3:
                    music.setPosition(277); // 04:37
                    break;
                case 4:
                    music.setPosition(343); // 05:43
                    break;
                case 5:
                    music.setPosition(410); // 06:50
                    break;
                case 6:
                    music.setPosition(475); // 07:55
                    break;
                case 7:
                    music.setPosition(540); // 09:00
                    break;
                case 8:
                    music.setPosition(602); // 10:02
                    break;
                case 9:
                    music.setPosition(676); // 11:16
                    break;
                case 10:
                    music.setPosition(737); // 12:17
                    break;
                case 11:
                    music.setPosition(797); // 13:17
                    break;
                case 12:
                    music.setPosition(858); // 14:18
                    break;
                case 13:
                    music.setPosition(918); // 15:18
                    break;
                case 14:
                    music.setPosition(987); // 16:27
                    break;
                case 15:
                    music.setPosition(1048); // 17:28
                    break;
                case 16:
                    music.setPosition(1092); // 18:12
                    break;
                case 17:
                    music.setPosition(1137); // 18:57
                    break;
                case 18:
                    music.setPosition(1179); // 19:39
                    break;
                case 19:
                    music.setPosition(1220); // 20:20
                    break;
                case 20:
                    music.setPosition(1284); // 21:24
                    break;
                case 21:
                    music.setPosition(1350); // 22:30
                    break;
                case 22:
                    music.setPosition(1417); // 23:37
                    break;
                case 23:
                    music.setPosition(1472); // 24:32
                    break;
                case 24:
                    music.setPosition(1480); // 24:40
                    break;
                case 25:
                    music.setPosition(1502); // 25:02
                    break;
            }
            music.play();
        }
        loaded = true;
    }

    public void stopMusic() {
        if (music != null) music.stop();
    }

    public void setVolume(float volume) {
        music.setVolume(volume);
    }

    public void dispose() {
        music.dispose();
    }
}

