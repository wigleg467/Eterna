package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class TileNode {
    public final int x, y;
    public final int index;
    public boolean walkable = true;

    public Array<Connection<TileNode>> connections = new Array<>();

    public TileNode(int x, int y, int index) {
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
