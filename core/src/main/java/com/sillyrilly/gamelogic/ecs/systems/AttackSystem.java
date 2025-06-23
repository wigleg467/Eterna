package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.utils.GameState;
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
                                Gdx.app.log("Удар", enemy.toString());
                            }
                        }
                    }

                } else {
                    if (bcp.getPosition().x > bce.getPosition().x) {
                        if (isEnemyNearPlayer(bcp.getPosition(), bce.getPosition())) {
                            HealComponent hce = hc.get(enemy);
                            if (hce.isAlive) {
                                hce.hp = hce.hp - wcp.type.DAMAGE;
                                Gdx.app.log("Удар", enemy.toString());
                            }
                        }
                    }
                }
                HealComponent hce = hc.get(enemy);
                if (!hce.isAlive && isLocationCleared("cemetery")) {
                    // наприклад:
                    Gdx.app.log("Location", "Всі вороги на cemetery знищені!");
                    GameState.instance.defeatedCemeteryMonsters=true;
                }
            }

        }
    }

    public boolean isLocationCleared(String locationName) {
        for (Entity enemy : enemies) {
            LocationComponent loc = enemy.getComponent(LocationComponent.class);
            HealComponent heal = hc.get(enemy);

            if (loc != null && loc.location.equals(locationName) && heal != null && heal.isAlive) {
                return false;
            }
        }
        return true;
    }


    private boolean isEnemyNearPlayer(Vector2 p, Vector2 e) {
        float dx = p.x - e.x;
        float dy = Math.abs(p.y - e.y);
        return dx <= 32f && dy <= 16f && !(dx == 0 && dy == 0);
    }
}
