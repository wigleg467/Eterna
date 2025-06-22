package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;

public class NPCComponent implements Component {
    public final NPCType npcType;

    public int dialogueStage = 0; // <- етап діалогу
    public AnimationTopComponent.TopState defaultTopState; // <- для повернення після розмови
    public boolean interacted = false; // <- чи вже говорив гравець

    public NPCComponent(NPCType type) {
        this.npcType = type;
        this.defaultTopState = AnimationTopComponent.TopState.IDLE; // можна задати інше
    }
}
