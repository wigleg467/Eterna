package com.sillyrilly.managers;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class CollisionManager {
    private static CollisionManager instance;

    private final TiledMapTileLayer collisionLayer;
    private final int tileSize;

    public CollisionManager(TiledMap map, String collisionLayerName, int tileSize) {
        this.collisionLayer = (TiledMapTileLayer) map.getLayers().get(collisionLayerName);
        this.tileSize = tileSize;
        instance = this;
    }

    public static CollisionManager getInstance() {
       return instance;
    }

    public boolean isBlocked(float worldX, float worldY) {
        int tileX = (int) (worldX / tileSize);
        int tileY = (int) (worldY / tileSize);

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell == null) return false;

        TiledMapTile tile = cell.getTile();
        return tile != null && tile.getProperties().containsKey("blocked");
    }


}
