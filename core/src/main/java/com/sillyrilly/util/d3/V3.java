package com.sillyrilly.util.d3;

public class V3 extends V2 {
    public float z;

    public V3(float x, float y, float z) {
        super(x, y);
        this.z = z;
    }

    public V3(V3 vector) {
        super(vector.x, vector.y);
        this.z = vector.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getZ() {
        return z;
    }
}
