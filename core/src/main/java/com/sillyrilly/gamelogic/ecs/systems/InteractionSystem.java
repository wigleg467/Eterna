package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.utils.Dialogue;
import com.sillyrilly.gamelogic.ecs.utils.DialogueWindow;
import com.sillyrilly.gamelogic.ecs.utils.GameState;
import com.sillyrilly.gamelogic.ecs.utils.NPCType;

import static com.sillyrilly.managers.DialogueManager.getDialogue;

public class InteractionSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<InteractableComponent> im = ComponentMapper.getFor(InteractableComponent.class);
    private final ComponentMapper<NPCComponent> nm = ComponentMapper.getFor(NPCComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);


    private ImmutableArray<Entity> npcs;
    private Entity player;
    private DialogueWindow dialogueWindow;

    public InteractionSystem(DialogueWindow dialogueWindow) {
        this.dialogueWindow = dialogueWindow;
    }

    @Override
    public void addedToEngine(Engine engine) {
        npcs = engine.getEntitiesFor(Family.all(InteractableComponent.class, BodyComponent.class, NPCComponent.class).get());
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

            if (distance < 2f) {
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
                Dialogue dialogue = getDialogue(npcComp.npcType, npcComp.dialogueStage);
                NPCComponent finalNpcComp = npcComp;
                dialogueWindow.onDialogueEnd = () -> {
                    if (finalNpcComp.npcType == NPCType.NUN && finalNpcComp.dialogueStage == 2) {
                        GameState.instance.gotBlessing = true;
                    }
                    else   if (finalNpcComp.npcType == NPCType.NUN && finalNpcComp.dialogueStage == 0) {
                        GameState.instance.talkedToNun = true;
                        finalNpcComp.dialogueStage++;
                    }
                    else   if (finalNpcComp.npcType == NPCType.NUN && finalNpcComp.dialogueStage == 1&&GameState.instance.defeatedCemeteryMonsters) {
                        finalNpcComp.dialogueStage++;
                    }
                    else if (finalNpcComp.npcType == NPCType.LUMBERJACK&&finalNpcComp.dialogueStage == 0) {
                        finalNpcComp.dialogueStage++;
                    }
                    else if (finalNpcComp.npcType == NPCType.LUMBERJACK&& GameState.instance.defeatedForestMonsters) {
                        finalNpcComp.dialogueStage++;
                    }
                };
                dialogueWindow.showDialogue(dialogue);
            }
        }
    }
}
