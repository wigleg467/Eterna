package com.sillyrilly.gamelogic.ecs.utils;

public enum EntityType implements Animatable {
    PLAYER("animations/player.atlas");


    private final String path;

    EntityType(String path) {
        this.path = path;
    }

    @Override
    public String getAnimationPath() {
        return path;
    }

}
