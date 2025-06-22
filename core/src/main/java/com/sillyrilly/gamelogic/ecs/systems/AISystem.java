package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;
import com.sillyrilly.gamelogic.ecs.components.AIComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;

import static com.sillyrilly.util.Const.TILE_SIZE;

public class AISystem extends EntitySystem {
    private final ComponentMapper<AIComponent> am = ComponentMapper.getFor(AIComponent.class);

    private Entity player;
    private ImmutableArray<Entity> enemies;

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).first();
        enemies = engine.getEntitiesFor(Family.all(AIComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Vector2 pp = player.getComponent(BodyComponent.class).body.getPosition();

        for (Entity e : enemies) {
            AIComponent ai = am.get(e);

            checkState(e, ai, pp);
            ai.stateMachine.update();
        }
    }

    private void checkState(Entity e, AIComponent ai, Vector2 playerPos) {
        if (canSeePlayer(e, playerPos)) {
            if (ai.stateMachine.isInState(EnemyState.IDLE))
                ai.stateMachine.changeState(EnemyState.CHASE);
        } else {
            if (ai.stateMachine.isInState(EnemyState.CHASE))
                ai.stateMachine.changeState(EnemyState.IDLE);
        }
    }

    private boolean canSeePlayer(Entity enemy, Vector2 playerPos) {
        Vector2 enemyPos = enemy.getComponent(BodyComponent.class).getPosition();
        float dst = enemyPos.dst2(playerPos);
        return dst < 30 * TILE_SIZE; // хз що це за число насправді (певно відстань по прямій)
    }
}
