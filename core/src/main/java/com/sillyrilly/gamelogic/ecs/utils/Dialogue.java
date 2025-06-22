package com.sillyrilly.gamelogic.ecs.utils;

import com.badlogic.gdx.utils.Array;

public class Dialogue {
    public final String name;
    public final String portraitPath;
    public final Array<String> lines;

    public Dialogue(String name, String portraitPath, String... lines) {
        this.name = name;
        this.portraitPath = portraitPath;
        this.lines = new Array<>(lines);
    }
}
