package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class TileComponent implements Component {
    public TiledMapTile tile;
    public float renderOffsetY;

    public TileComponent(TiledMapTile tile) {
        this.tile = tile;
        this.renderOffsetY = getTileOffset();
    }

    private int getTileOffset() {
        MapProperties props = tile.getProperties();
        return props.containsKey("height") ? (int) props.get("height") : 0;
    }
}
