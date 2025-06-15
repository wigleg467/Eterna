package com.sillyrilly.gamelogic.ecs.types;

public enum EnemyType {
    OGRE("animations/ogre.atlas", 1.2f, 2.4f),
    SLIME("animations/slime.atlas", 0.8f, 1.6f);

    private final String atlasPath;
    private final float width;
    private final float height;

    EnemyType(String atlasPath, float width, float height) {
        this.atlasPath = atlasPath;
        this.width = width;
        this.height = height;
    }

    public String getAtlasPath() { return atlasPath; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
