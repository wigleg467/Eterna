package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.types.EntityType;


public class ClassificationComponent implements Component {
    public EntityType type;
    public ClassificationComponent(EntityType type) {
        this.type = type;
    }

}
