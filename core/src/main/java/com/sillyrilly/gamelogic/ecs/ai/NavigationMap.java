package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import static com.sillyrilly.util.Const.TILE_SIZE;

public class NavigationMap {
    public static NavigationMap instance;
    public int[][] grid;

    public NavigationMap(TiledMap map) {
        instance = this;

        TiledMapTileLayer groundLayer = (TiledMapTileLayer) map.getLayers().get("base"); // будь-який тайловий шар, щоб дізнатись розміри
        int mapWidth = groundLayer.getWidth();      // у клітинках
        int mapHeight = groundLayer.getHeight();    // у клітинках

        grid = new int[mapWidth][mapHeight];

        MapLayer collisionLayer = map.getLayers().get("Collision_lvl_1"); // <- твій Object Layer
        for (MapObject object : collisionLayer.getObjects()) {
            if (object instanceof RectangleMapObject rectObj) {
                Rectangle rect = rectObj.getRectangle();

                // Конвертуємо координати з пікселів у клітинки
                int startX = (int) (rect.x / TILE_SIZE);
                int startY = (int) (rect.y / TILE_SIZE);

                int endX = (int) ((rect.x + rect.width) / TILE_SIZE);
                int endY = (int) ((rect.y + rect.height) / TILE_SIZE);

                // Відмічаємо всі клітинки, які перетинає прямокутник
                for (int x = startX; x <= endX && x >= 0 && x < mapWidth; x++) {
                    for (int y = startY; y <= endY && y >= 0 && y < mapHeight; y++) {
                        Gdx.app.log("NavigationMap", x + ", " + y);
                        grid[x][y] = 1; // блокована клітинка
                    }
                }
            }
        }
    }
}
