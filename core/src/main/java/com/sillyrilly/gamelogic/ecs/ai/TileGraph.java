package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class TileGraph implements IndexedGraph<TileNode> {
    private static TileGraph instance;
    private TileNode[][] nodes;
    private Array<TileNode> nodeList;

    public TileGraph(int[][] grid) {
        instance = this;

        int width = grid.length;
        int height = grid[0].length;

        nodes = new TileNode[width][height];
        nodeList = new Array<>();

        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode node = new TileNode(x, y, index++);
                node.walkable = (grid[x][y] == 0);
                nodes[x][y] = node;
                nodeList.add(node);
            }
        }

        // З'єднання сусідів (4 напрямки)
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                TileNode node = nodes[x][y];
                if (!node.walkable) continue;

                addConnection(node, x + 1, y);
                addConnection(node, x - 1, y);
                addConnection(node, x, y + 1);
                addConnection(node, x, y - 1);
            }
        }
    }

    public static TileGraph getInstance() {
        return instance;
    }

    private void addConnection(TileNode from, int x, int y) {
        if (x < 0 || y < 0 || x >= nodes.length || y >= nodes[0].length) return;
        TileNode to = nodes[x][y];
        if (!to.walkable) return;
        from.connections.add(new DefaultConnection<>(from, to));
    }

    public TileNode getNode(int x, int y) {
        return nodes[x][y];
    }

    public Array<TileNode> getNodes() {
        return nodeList;
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
