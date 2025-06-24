package com.sillyrilly.managers;

import com.badlogic.gdx.utils.ObjectMap;
import com.sillyrilly.gamelogic.ecs.utils.Dialogue;
import com.sillyrilly.gamelogic.ecs.utils.DialogueKey;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;

public class DialogueManager {
    private static final ObjectMap<DialogueKey, Dialogue> dialogues = new ObjectMap<>();

    public static void initialize() {
        dialogues.put(new DialogueKey(NPCType.LUMBERJACK, 0), new Dialogue(
                NPCType.LUMBERJACK.getName(),
                NPCType.LUMBERJACK.getPortraitPath(),
                "Привіт, друже!",
                "Я рубаю дрова.",
                "Можеш допомогти?"
        ));

        // Другий діалог для того самого NPC
        dialogues.put(new DialogueKey(NPCType.LUMBERJACK, 1), new Dialogue(
                NPCType.LUMBERJACK.getName(),
                NPCType.LUMBERJACK.getPortraitPath(),
                "Вони досі тут. Будь обачним"
        ));

        dialogues.put(new DialogueKey(NPCType.LUMBERJACK, 2), new Dialogue(
                NPCType.LUMBERJACK.getName(),
                NPCType.LUMBERJACK.getPortraitPath(),
                "Дякую за допомогу.",
                "За твою мужність я дарую тобі сокиру свого сина"
        ));

        dialogues.put(new DialogueKey(NPCType.DEMON, 0), new Dialogue(
                NPCType.DEMON.getName(),
                NPCType.DEMON.getPortraitPath(),
                "ОО, бачу жива дуща в пеклі",
                "У тебе чисте серце",
                "Біди знайди ниточку і підіймайся в рай"
        ));
        dialogues.put(new DialogueKey(NPCType.NUN, 0), new Dialogue(
                NPCType.NUN.getName(),
                NPCType.NUN.getPortraitPath(),
                "Слава Ісусу Христу",
                "У мене пістолет святої води.",
                "Допоможи побороти цих монстрів."
        ));
        dialogues.put(new DialogueKey(NPCType.NUN, 1), new Dialogue(
                NPCType.NUN.getName(),
                NPCType.NUN.getPortraitPath(),
                "Слава Ісусу Христу",
                "Вороги досі там."
        ));
        dialogues.put(new DialogueKey(NPCType.NUN, 2), new Dialogue(
                NPCType.NUN.getName(),
                NPCType.NUN.getPortraitPath(),
                "Ти отримуєш моє балословення",
                "Іди перетни міст"
        ));
        dialogues.put(new DialogueKey(NPCType.GUARDCAT, 0), new Dialogue(
                NPCType.GUARDCAT.getName(),
                NPCType.GUARDCAT.getPortraitPath(),
                "О нііі,так просто через міст ти не пройдеш",
                "Спершу отрисай благословення монашки!",
                "Вона біля монастиря, туди дорога через ліс"
        ));
        dialogues.put(new DialogueKey(NPCType.GUARDCAT, 1), new Dialogue(
                NPCType.GUARDCAT.getName(),
                NPCType.GUARDCAT.getPortraitPath(),
                "Нічого собі",
                "В тебе вийшло!.",
                "Але чи на щастя це..."
        ));
    }

    public static Dialogue getDialogue(NPCType type, int stage) {
        Dialogue d = dialogues.get(new DialogueKey(type, stage));
        if (d == null) {
            // fallback на перший діалог
            return dialogues.get(new DialogueKey(type, 0));
        }
        return d;
    }
}
