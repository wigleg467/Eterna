package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

public class LocationComponent implements Component {
    public final String location;

    public LocationComponent(String location) {
        this.location = location;
    }
}
