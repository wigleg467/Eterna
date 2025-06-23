package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.managers.InputManager;

import static com.sillyrilly.gamelogic.ecs.utils.GameState.defeatedCemeteryMonsters;
import static com.sillyrilly.gamelogic.ecs.utils.GameState.defeatedHellGatesMonsters;

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

            boolean allCemeteryDead = true;
            boolean allHellGatesDead = true;

            for (Entity enemy : enemies) {
                BodyComponent bce = bc.get(enemy);
                HealComponent hce = hc.get(enemy);
                LocationComponent loc = enemy.getComponent(LocationComponent.class);


                if (isEnemyNearPlayer(bcp.getPosition(), bce.getPosition(), facingRight)) {
                    if (hce.isAlive) {
                        hce.takeDamage(wcp.type.DAMAGE);
                        hce.hitTimer = 0.15f;
                        Gdx.app.log("Hit", hce.hp + " " + hce.isAlive);
                    } else {
                        bce.body.setActive(false);
                    }
                }


                // Перевірка на alive
                if (loc != null && loc.location.equals("cemetery") && hce.isAlive) {
                    allCemeteryDead = false;
                }
                if (loc != null && loc.location.equals("hellGates") && hce.isAlive) {
                    allHellGatesDead = false;
                }
            }

            if (allCemeteryDead) {
                defeatedCemeteryMonsters = true;
                Gdx.app.log("Location", "All enemies in cemetery are dead cemetery");
            }
            if (allHellGatesDead) {
                defeatedHellGatesMonsters = true;
                Gdx.app.log("Location", "All enemies in hellGates are dead");
            }

        }
    }

    private boolean isEnemyNearPlayer(Vector2 p, Vector2 e, boolean facingRight) {
        float dx = e.x - p.x;

        if (facingRight) {
            if (dx < 0) {
                return false;
            }
        } else if (dx > 0) {
            return false;
        } else {
            dx = Math.abs(dx);
        }

        float dy = Math.abs(p.y - e.y);
        return dx <= 5f && dy <= 3f;
    }
}
