package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.managers.InputManager;

public class AttackSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<FacingComponent> fc = ComponentMapper.getFor(FacingComponent.class);
    private final ComponentMapper<WeaponComponent> wc = ComponentMapper.getFor(WeaponComponent.class);
    private final ComponentMapper<HealComponent> hc = ComponentMapper.getFor(HealComponent.class);

    private InputManager inputManager;
    private ImmutableArray<Entity> enemies;
    private Entity player;

    @Override
    public void addedToEngine(Engine engine) {
        inputManager = InputManager.instance;
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        if (inputManager.canAttack()) {
            inputManager.setCanAttack(false);

            BodyComponent bcp = bc.get(player);
            FacingComponent fcp = fc.get(player);
            WeaponComponent wcp = wc.get(player);

            boolean facingRight = fcp.facingRight;

            for (Entity enemy : enemies) {
                BodyComponent bce = bc.get(enemy);
                if (facingRight) {
                    if (bcp.getPosition().x < bce.getPosition().x) {
                        if (isEnemyNearPlayer(bcp.getPosition(), bce.getPosition())) {
                            HealComponent hce = hc.get(enemy);
                            if (hce.isAlive) {
                                hce.hp = hce.hp - wcp.type.DAMAGE;
                            }
                        }
                    }

                } else {
                    if (bcp.getPosition().x > bce.getPosition().x) {
                        if (isEnemyNearPlayer(bcp.getPosition(), bce.getPosition())) {
                            HealComponent hce = hc.get(enemy);
                            if (hce.isAlive) {
                                hce.hp = hce.hp - wcp.type.DAMAGE;
                            }
                        }
                    }
                }
            }

        }
    }

    private boolean isEnemyNearPlayer(Vector2 p, Vector2 e) {
        float dx = p.x - e.x;
        float dy = Math.abs(p.y - e.y);
        return dx <= 32f && dy <= 16f && !(dx == 0 && dy == 0);
    }
}
