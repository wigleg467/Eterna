package com.sillyrilly.gamelogic.ecs.utils;

public class DialogueKey {
    public final NPCType type;
    public final int stage;

    public DialogueKey(NPCType type, int stage) {
        this.type = type;
        this.stage = stage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DialogueKey other)) return false;
        return this.stage == other.stage && this.type == other.type;
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + stage;
    }
}
