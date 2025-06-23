package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;

public class PathComponent implements Component {
    public GraphPath<TileNode> path = new DefaultGraphPath<>();
    public int currentIndex = 1;

    public Vector2 lastTargetPosition = new Vector2();
    public float timeSinceLastUpdate = 0f;
}
