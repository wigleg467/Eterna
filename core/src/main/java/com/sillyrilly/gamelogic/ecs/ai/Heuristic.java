package com.sillyrilly.gamelogic.ecs.ai;

public class Heuristic implements com.badlogic.gdx.ai.pfa.Heuristic<TileNode> {
    @Override
    public float estimate(TileNode node, TileNode endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y); // Manhattan
    }
}
