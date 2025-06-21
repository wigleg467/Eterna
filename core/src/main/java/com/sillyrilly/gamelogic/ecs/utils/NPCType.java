package com.sillyrilly.gamelogic.ecs.utils;

public enum NPCType implements Animatable {
    LUMBERJACK("animations/lumberjack.atlas", 1f, 1f, "Лісоруб", "portraits/lumberjack.png"),
    DEMON("animations/demon.atlas", 1f, 1f, "Демон", "portraits/lumberjack.png"),;
    private final String path, ukrName, portrait;
    private final float w, h;

    NPCType(String path, float w, float h, String ukrName, String portrait) {
        this.path = path;
        this.w = w;
        this.h = h;
        this.ukrName = ukrName;
        this.portrait = portrait;
    }

    @Override
    public String getAnimationPath() {
        return path;
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public String getName(){
        return ukrName;
    }
    public String getPortraitPath(){
        return portrait;
    }
}
