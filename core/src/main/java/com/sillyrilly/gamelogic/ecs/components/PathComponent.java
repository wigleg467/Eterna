package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;

public class PathComponent implements Component {
    public GraphPath<TileNode> path = new DefaultGraphPath<>();
    public int currentIndex = 1;
}
