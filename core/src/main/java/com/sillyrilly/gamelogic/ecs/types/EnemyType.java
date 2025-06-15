package com.sillyrilly.gamelogic.ecs.types;

public enum EnemyType implements Animatable {
    GUARD("animations/guard.atlas", 1f, 0.2f);


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
