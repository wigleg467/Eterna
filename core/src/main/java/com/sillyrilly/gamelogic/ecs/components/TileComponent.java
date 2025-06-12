package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

public class TileComponent implements Component {
    public TiledMapTile tile;
    public float renderOffsetY = 0;

    public TileComponent(TiledMapTile tile) {
        this.tile = tile;
    }

    public TileComponent(TiledMapTile tile, float renderOffsetY) {
        this.tile = tile;
        this.renderOffsetY = renderOffsetY;
    }
}
