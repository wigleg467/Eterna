package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class HintComponent implements Component {
    public String message;

    public HintComponent(String message) {
        this.message = message;
    }
}
