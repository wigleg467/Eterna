package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;

import static com.sillyrilly.util.Const.PPM;

public class EnemyAttackSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<HealComponent> hc = ComponentMapper.getFor(HealComponent.class);
    private final ComponentMapper<AttackComponent> ac = ComponentMapper.getFor(AttackComponent.class);


    private ImmutableArray<Entity> enemies;
    private Entity player;
    private BodyComponent bcp;
    private HealComponent hcp;

    @Override
    public void addedToEngine(Engine engine) {
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, AttackComponent.class, BodyComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class, BodyComponent.class, HealComponent.class).get()).first();

        bcp = bc.get(player);
        hcp = hc.get(player);
    }

    @Override
    public void update(float deltaTime) {
        for (Entity enemy : enemies) {
            AttackComponent attack = ac.get(enemy);
            BodyComponent body = bc.get(enemy);

            attack.timeSinceLastAttack += deltaTime;

            if (isPlayerInRange(body.getPosition(), bcp.getPosition())) {
                if (attack.timeSinceLastAttack >= attack.attackCooldown) {
                    attack.timeSinceLastAttack = 0f;

                    if (hcp.isAlive) {
                        hcp.takeDamage(attack.damage);
                        hcp.hitTimer = 0.15f;
                        Gdx.app.log("Enemy", "Player hit! HP: " + hcp.hp);
                    }
                }
            }
        }
    }

    private boolean isPlayerInRange(Vector2 enemyPos, Vector2 playerPos) {
        float dx = Math.abs(enemyPos.x - playerPos.x);
        float dy = Math.abs(enemyPos.y - playerPos.y);
        return dx <= 32f && dy <= 32f;
    }
}
