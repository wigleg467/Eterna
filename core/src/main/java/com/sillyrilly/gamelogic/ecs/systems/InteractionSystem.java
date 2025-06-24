package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.utils.Dialogue;
import com.sillyrilly.gamelogic.ecs.utils.DialogueWindow;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;
import com.sillyrilly.managers.ScreenManager;
import com.sillyrilly.screens.GameScreen;

import static com.sillyrilly.gamelogic.ecs.utils.GameState.*;
import static com.sillyrilly.managers.DialogueManager.getDialogue;

public class InteractionSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<InteractableComponent> im = ComponentMapper.getFor(InteractableComponent.class);
    private final ComponentMapper<NPCComponent> nm = ComponentMapper.getFor(NPCComponent.class);
    private final ComponentMapper<InteractiveObjectComponent> iom = ComponentMapper.getFor(InteractiveObjectComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);


    private ImmutableArray<Entity> npcs;
    private Entity player;
    private final DialogueWindow dialogueWindow;

    public InteractionSystem(DialogueWindow dialogueWindow) {
        this.dialogueWindow = dialogueWindow;
    }

    @Override
    public void addedToEngine(Engine engine) {
        npcs = engine.getEntitiesFor(Family.all(InteractableComponent.class, BodyComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class, BodyComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        // Якщо відкрито діалогове вікно — обробляємо тільки ENTER
        if (dialogueWindow.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                dialogueWindow.nextLine();
            }
            return;
        }

        Vector2 playerPos = bm.get(player).getPosition();

        boolean hintShown = false;

        for (Entity entity : npcs) {
            Vector2 entityPos = bm.get(entity).getPosition();
            float distance = playerPos.dst(entityPos);

            if (distance < 2.5f) {

                // ===== NPC ====
                if (nm.has(entity)) {
                    NPCComponent npcComp = nm.get(entity);
                    AnimationComponent anim = am.get(entity);

                    // Показуємо підказку
                    GameScreen.instance.hintRenderer.showHint(npcComp.npcType.getHint(), entityPos);
                    hintShown = true;


                    // Взаємодія при натисканні E
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        GameScreen.instance.hintRenderer.hideHint();

                        // Переводимо анімацію в DEFAULT (наприклад, поворот обличчям)
                        anim.currentState = AnimationComponent.State.DEFAULT;
                        anim.stateTime = 0;

                        // Оновлюємо stage діалогу
                        if (defeatedCemeteryMonsters && npcComp.npcType == NPCType.NUN ||
                                defeatedForestMonsters && npcComp.npcType == NPCType.LUMBERJACK) {
                            npcComp.dialogueStage = 2;
                            Gdx.app.log("Dialogue", "Dialogue Dialogue");
                        }
                        if (gotBlessing && npcComp.npcType == NPCType.GUARDCAT) {
                            npcComp.dialogueStage = 1;
                        }

                        Dialogue dialogue = getDialogue(npcComp.npcType, npcComp.dialogueStage);

                        dialogueWindow.onDialogueEnd = () -> {
                            if (npcComp.npcType == NPCType.NUN && npcComp.dialogueStage == 2) {
                                gotBlessing = true;
                            } else if (npcComp.npcType == NPCType.NUN && npcComp.dialogueStage == 0) {
                                talkedToNun = true;
                                npcComp.dialogueStage++;
                            } else if (npcComp.npcType == NPCType.LUMBERJACK && npcComp.dialogueStage == 0) {
                                talkedToLumberjack = true;
                                npcComp.dialogueStage++;
                            }

                            anim.currentState = AnimationComponent.State.IDLE;
                        };

                        dialogueWindow.showDialogue(dialogue);

                        break;
                    }

                }

                // ===== ІНТЕРАКТИВНИЙ ОБ'ЄКТ =====
                else if (iom.has(entity)) {
                    InteractiveObjectComponent ioc = iom.get(entity);

                    // Показати підказку
                    String hint = switch (ioc.type) {
                        case "web" -> "Натисніть E щоб піднятися";
                        case "house" -> "Натисніть E щоб увійти";
                        default -> null;
                    };


                        GameScreen.instance.hintRenderer.showHint(hint, entityPos);
                    hintShown = true;

                    // Обробка взаємодії
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        GameScreen.instance.hintRenderer.hideHint();

                        switch (ioc.type) {
                            case "web" -> {
                                Gdx.app.log("Interaction", "Teleport to: heaven");
                                heaven = true;
                            }
                            case "house" -> {
                                if (defeatedHellGatesMonsters) {
                                    Gdx.app.log("Interaction", "Teleport to: ASCII screen");
                                    ScreenManager.instance.setScreen(ScreenManager.ScreenType.ASCII);
                                }
                            }
                        }

                        break;
                    }
                }
            }
        }

        if (!hintShown) {
            GameScreen.instance.hintRenderer.hideHint();
        }
    }
}
