package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.utils.EnemyType;
import com.sillyrilly.gamelogic.ecs.utils.GameState;
import com.sillyrilly.gamelogic.ecs.utils.GameStats;
import com.sillyrilly.managers.InputManager;
import com.sillyrilly.managers.ScreenManager;

import static com.sillyrilly.gamelogic.ecs.utils.GameState.*;


public class AttackSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<FacingComponent> fc = ComponentMapper.getFor(FacingComponent.class);
    private final ComponentMapper<WeaponComponent> wc = ComponentMapper.getFor(WeaponComponent.class);
    private final ComponentMapper<HealComponent> hc = ComponentMapper.getFor(HealComponent.class);
    private final ComponentMapper<EnemyComponent> ec = ComponentMapper.getFor(EnemyComponent.class);
    private InputManager inputManager;
    private ImmutableArray<Entity> enemies;
    private Entity player;
    GameStats stats = GameState.instance.stats;
    @Override
    public void addedToEngine(Engine engine) {
        inputManager = InputManager.instance;
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        if (inputManager.isAttacking()) {
            BodyComponent bcp = bc.get(player);
            FacingComponent fcp = fc.get(player);
            WeaponComponent wcp = wc.get(player);

            boolean facingRight = fcp.facingRight;

            boolean allCemeteryDead = true;
            boolean allHellGatesDead = true;
            boolean allForestDead = true;

            for (Entity enemy : enemies) {
                BodyComponent bce = bc.get(enemy);
                HealComponent hce = hc.get(enemy);
                EnemyComponent enc = ec.get(enemy);
                LocationComponent loc = enemy.getComponent(LocationComponent.class);


                if (isEnemyNearPlayer(bcp.getPosition(), bce.getPosition(), facingRight)) {
                    if (hce.isAlive) {
                        hce.takeDamage(wcp.type.DAMAGE);
                        Gdx.app.log("Hit", hce.hp + " " + hce.isAlive);
                        hce.hitTimer = 0.15f;
                    }
                    if (!hce.isAlive) {
                        bce.body.setActive(false);
                        GameState.instance.stats.addKill(enc.enemyType.name());
                        if(enc.enemyType.equals(EnemyType.ANGEL)){
                            ScreenManager.instance.setScreen(ScreenManager.ScreenType.WINSCREEN);
                        }
                    }
                }


                // Перевірка на alive
                if (loc != null && loc.location.equals("cemetery") && hce.isAlive) {
                    allCemeteryDead = false;
                }
                if (loc != null && loc.location.equals("hellGates") && hce.isAlive) {
                    allHellGatesDead = false;
                }
                if (loc != null && loc.location.equals("forest") && hce.isAlive) {
                    allForestDead = false;
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
            if (allForestDead) {
                defeatedForestMonsters = true;
                Gdx.app.log("Location", "All enemies in forest are dead");
            }
            inputManager.setAttack(false);
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
