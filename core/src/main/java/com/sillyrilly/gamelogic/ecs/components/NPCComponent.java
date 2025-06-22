package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;

public class NPCComponent implements Component {
    public final NPCType npcType;

    public NPCComponent(NPCType type) {
        npcType = type;
    }

}
