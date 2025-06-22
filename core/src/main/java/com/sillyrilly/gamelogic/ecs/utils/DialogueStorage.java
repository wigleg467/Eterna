package com.sillyrilly.gamelogic.ecs.utils;

import com.badlogic.gdx.utils.ObjectMap;

public class DialogueStorage {
    private static final ObjectMap<NPCType, Dialogue> dialogues = new ObjectMap<>();

    public static void init() {
        dialogues.put(NPCType.LUMBERJACK, new Dialogue(
                NPCType.LUMBERJACK.getName(),
                NPCType.LUMBERJACK.getPortraitPath(),
                "Привіт, друже!",
                "Я рубаю дрова.",
                "Можеш допомогти?"
        ));

        dialogues.put(NPCType.DEMON, new Dialogue(
                NPCType.DEMON.getName(),
                NPCType.DEMON.getPortraitPath(),
                "Не наближайся!",
                "Я з пекла."
        ));
        dialogues.put(NPCType.NUN, new Dialogue(
                NPCType.NUN.getName(),
                NPCType.NUN.getPortraitPath(),
                "Слава Ісусу Христу",
                "У мене пістолет святої води."
        ));
    }

    public static Dialogue getDialogue(NPCType type) {
        return dialogues.get(type);
    }
}
