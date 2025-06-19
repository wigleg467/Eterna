package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public static BodyComponent bc;

    public PlayerComponent(BodyComponent body) {
        bc = body;
    }
}
