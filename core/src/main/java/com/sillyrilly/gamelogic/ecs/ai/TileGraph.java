package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class TileGraph implements IndexedGraph<TileNode> {
    public static TileGraph instance;

    private final Array<TileNode> nodeList;
    private final TileNode[][] nodes;

    public TileGraph(int[][] grid) {
        instance = this;

        int width = grid.length;
        int height = grid[0].length;

        nodes = new TileNode[width][height];
        nodeList = new Array<>();

        // Створення вузлів
        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode node = new TileNode(x, y, index++);
                node.walkable = (grid[x][y] == 0);
                nodes[x][y] = node;
                nodeList.add(node);
            }
        }

        // З'єднання сусідів
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode node = nodes[x][y];
                if (!node.walkable) continue;

                addConnection(node, x, y + 1);        // ↑
                addConnection(node, x + 1, y);        // →
                addConnection(node, x - 1, y);        // ←
                addConnection(node, x, y + 1);        // ↑
                addConnection(node, x, y - 1);        // ↓

                addConnection(node, x + 1, y + 1); // ↗
                addConnection(node, x - 1, y + 1); // ↖
                addConnection(node, x + 1, y - 1); // ↘
                addConnection(node, x - 1, y - 1); // ↙
            }
        }
    }

    private void addConnection(TileNode from, int x, int y) {
        if (x < 0 || y < 0 || x >= nodes.length || y >= nodes[0].length) return;

        TileNode to = nodes[x][y];
        if (!to.walkable) return;

        // Corner cutting check for diagonal movement
        int dx = x - from.x;
        int dy = y - from.y;

        // Якщо рух діагональний
        if (dx != 0 && dy != 0) {
            TileNode node1 = nodes[from.x + dx][from.y]; // горизонтальний сусід
            TileNode node2 = nodes[from.x][from.y + dy]; // вертикальний сусід

            if (!node1.walkable || !node2.walkable) return; // якщо один із сусідів заблокований
        }

        from.connections.add(new DefaultConnection<>(from, to));
    }

    public TileNode getNode(int x, int y) {
        if (x < 0 || y < 0 || x >= nodes.length || y >= nodes[0].length)
            return nodes[nodes.length - 1][nodes[0].length - 1];
        return nodes[x][y];
    }

    @Override
    public Array<Connection<TileNode>> getConnections(TileNode node) {
        return node.connections;
    }

    @Override
    public int getIndex(TileNode node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return nodeList.size;
    }
}
