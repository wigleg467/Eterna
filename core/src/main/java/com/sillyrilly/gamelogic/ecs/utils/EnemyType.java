package com.sillyrilly.gamelogic.ecs.utils;

public enum EnemyType implements Animatable {
    GUARD("animations/guard.atlas", 1f, 0.0625f*2);
    //0.0625 це висота ступнів поділена на два


    private final String path;
    private final float w, h;

    EnemyType(String path, float w, float h) {
        this.path = path;
        this.w = w;
        this.h = h;
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
}
