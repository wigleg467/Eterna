package com.sillyrilly.gamelogic.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Entity implements Disposable {
    protected Vector2 position;
    protected Vector2 velocity;
    protected Texture texture;
    protected Rectangle bounds;

    protected float speed;
    protected float height, width;
    protected float health;

    public Entity(Texture texture, float x, float y, float speed) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.speed = speed;
        this.width = texture.getWidth();
        this.height = texture.getHeight();
    }

    public void update(float delta) {
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX() { return position.x; }
    public float getY() { return position.y; }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
