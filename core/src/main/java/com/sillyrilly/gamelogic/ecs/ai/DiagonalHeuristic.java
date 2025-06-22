package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class DiagonalHeuristic implements Heuristic<TileNode> {
    @Override
    public float estimate(TileNode node, TileNode endNode) {
        float dx = Math.abs(node.x - endNode.x);
        float dy = Math.abs(node.y - endNode.y);
        return (dx + dy) + (1.41f - 2.0f) * Math.min(dx, dy);  // або Math.max(dx, dy);
    }
}
