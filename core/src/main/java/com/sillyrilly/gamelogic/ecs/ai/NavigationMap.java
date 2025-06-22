package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

public class NavigationMap {
    private static NavigationMap instance;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    private final int[][] grid;

    public NavigationMap(TiledMap map) {
        instance = this;

        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("base"); // будь-який тайловий шар, щоб дізнатись розміри
        int mapWidth = groundLayer.getWidth();      // у клітинках
        int mapHeight = groundLayer.getHeight();    // у клітинках
        int tileWidth = groundLayer.getTileWidth();
        int tileHeight = groundLayer.getTileHeight();

        grid = new int[mapWidth][mapHeight];

        MapLayer collisionLayer = map.getLayers().get("Collision_lvl_1"); // <- твій Object Layer
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rect = rectObj.getRectangle();

                // Конвертуємо координати з пікселів у клітинки
                int startX = (int) (rect.x / tileWidth);
                int startY = (int) (rect.y / tileHeight);
                int endX = (int) ((rect.x + rect.width) / tileWidth) - 1;
                int endY = (int) ((rect.y + rect.height) / tileHeight) - 1;

                // Відмічаємо всі клітинки, які перетинає прямокутник
                for (int x = startX; x <= endX && x < mapWidth; x++) {
                    for (int y = startY; y <= endY && y < mapHeight; y++) {
                        grid[x][y] = 1; // блокована клітинка
                    }
                }
            }
        }
    }

    public static NavigationMap getInstance() {
        return instance;
    }

    public boolean isWalkable(int x, int y) {
        return grid[x][y] == 0;
    }

    public int[][] getGrid() {
        return grid;
    }
}
