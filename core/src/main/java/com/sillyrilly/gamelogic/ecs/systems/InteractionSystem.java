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
        if (dialogueWindow.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                dialogueWindow.nextLine();
            }
            return;
        }

        if (!Gdx.input.isKeyJustPressed(Input.Keys.E)) return;

        Vector2 playerPos = bm.get(player).getPosition();

        for (Entity npc : npcs) {
            Vector2 npcPos = bm.get(npc).getPosition();
            float distance = playerPos.dst(npcPos);

            if (distance < 2.5f) {
                if (nm.has(npc)) {
                    AnimationComponent anim = am.get(npc);
                    anim.currentState = AnimationComponent.State.DEFAULT;
                    anim.stateTime = 0;

                    NPCComponent npcComp = nm.get(npc);
                    // Діалог для цього NPC
                    if (npcComp == null || npcComp.npcType == null) {
                        Gdx.app.error("InteractionSystem", "NPCComponent або npcType дорівнює null");
                        continue;
                    }
                    npcComp = nm.get(npc);
                    if (defeatedCemeteryMonsters && npcComp.npcType == NPCType.NUN ||
                            defeatedForestMonsters && npcComp.npcType == NPCType.LUMBERJACK) {
                        npcComp.dialogueStage = 2;
                    }
                    if (gotBlessing && npcComp.npcType == NPCType.GUARDCAT) {
                        npcComp.dialogueStage = 1;

                    }


                    Dialogue dialogue = getDialogue(npcComp.npcType, npcComp.dialogueStage);
                    NPCComponent finalNpcComp = npcComp;


                    dialogueWindow.onDialogueEnd = () -> {
                        if (finalNpcComp.npcType == NPCType.NUN && finalNpcComp.dialogueStage == 2) {
                            gotBlessing = true;
                        } else if (finalNpcComp.npcType == NPCType.NUN && finalNpcComp.dialogueStage == 0) {
                            talkedToNun = true;
                            finalNpcComp.dialogueStage++;
                        } else if (finalNpcComp.npcType == NPCType.LUMBERJACK && finalNpcComp.dialogueStage == 0) {
                            finalNpcComp.dialogueStage++;
                        }
                        anim.currentState = AnimationComponent.State.IDLE;
                    };
                    dialogueWindow.showDialogue(dialogue);
                } else if (iom.has(npc)) {
                    // це інтерактивний об’єкт
                    InteractiveObjectComponent ioc = iom.get(npc);
                    switch (ioc.type) {
                        case "web":
                            BodyComponent bc = bm.get(player);
                            // bc.body.setTransform(newX / PPM, newY / PPM, 0);
                            Gdx.app.log("Interaction", "Teleport to: heaven");
                            heaven = true;
                            break;

                        case "house":
                            if (defeatedHellGatesMonsters) {
                                Gdx.app.log("Transform", "to ASCII");
                                ScreenManager.instance.setScreen(ScreenManager.ScreenType.ASCII);
                            }
                            break;
                    }
                }
            }
        }
    }
}
