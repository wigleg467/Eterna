package com.sillyrilly.util.d3;

public class V2 {
    public float x, y;

    public V2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public V2(V2 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public V2(V3 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
