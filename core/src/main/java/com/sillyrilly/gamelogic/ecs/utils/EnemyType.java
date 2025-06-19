package com.sillyrilly.gamelogic.ecs.utils;

public enum EnemyType implements Animatable {
    GUARD("animations/guard.atlas", 1f, 0.0625f*2),
    ZOMBIE("animations/zombie.atlas", 1f, 0.0625f*2),
    SKELETON("animations/skeleton.atlas", 1f, 0.0625f*2),
    WATERMELON("animations/watermelon.atlas", 1f, 0.0625f*2),
    MUMMY("animations/mummy.atlas", 1f, 0.0625f*2),
    ANGEL("animations/angel.atlas", 1f, 1f),
    DEMON("animations/demon.atlas", 1f, 1f);
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
