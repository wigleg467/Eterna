package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AIComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;

public enum EnemyState implements State<Entity> {
    IDLE {
        @Override
        public void enter(Entity entity) {
            Gdx.app.log("AI", "Enter IDLE");
        }

        @Override
        public void update(Entity entity) {
            // Якщо бачить гравця → змінити стан:
            if (AIUtils.canSeePlayer(entity)) {
                entity.getComponent(AIComponent.class).stateMachine.changeState(CHASE);
            }
        }

        @Override
        public void exit(Entity entity) {
            Gdx.app.log("AI", "Exit IDLE");
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    },

    CHASE {
        @Override
        public void enter(Entity entity) {
            Gdx.app.log("AI", "Enter CHASE");
        }

        private void updatePath(Entity entity) {
//            Vector2 enemyPos = entity.getComponent(BodyComponent.class).getPosition();
//            Vector2 playerPos = AIUtils.getPlayerPosition();
//
//            int ex = (int)(enemyPos.x / TILE_SIZE);
//            int ey = (int)(enemyPos.y / TILE_SIZE);
//            int px = (int)(playerPos.x / TILE_SIZE);
//            int py = (int)(playerPos.y / TILE_SIZE);
//
//            List<Vector2> path = Pathfinding.findPath(NavigationMap.getInstance().getGrid(), ex, ey, px, py);
//
//            EnemyComponent ec = entity.getComponent(EnemyComponent.class);
//            ec.path.clear();
//            for (Vector2 tile : path) {
//                ec.path.add(tile.scl(TILE_SIZE)); // повертаємо в світ координат
//            }
        }

        @Override
        public void update(Entity entity) {
            if (!AIUtils.canSeePlayer(entity)) {
                entity.getComponent(AIComponent.class).stateMachine.changeState(IDLE);
                return;
            }
//
//            EnemyComponent ec = entity.getComponent(EnemyComponent.class);
//            Body body = entity.getComponent(BodyComponent.class).getBody();
//            Vector2 myPos = body.getPosition();
//
//            if (ec.path.isEmpty()) {
//                updatePath(entity);
//                return;
//            }
//
//            Vector2 target = ec.path.peek();
//            if (target.dst2(myPos) < 0.1f) {
//                ec.path.poll(); // досягли цільової точки
//            } else {
//                Vector2 dir = target.cpy().sub(myPos).nor().scl(2f);
//                body.setLinearVelocity(dir);
//            }
        }

        @Override
        public void exit(Entity entity) {
            Gdx.app.log("AI", "Exit CHASE");
            entity.getComponent(BodyComponent.class).getBody().setLinearVelocity(Vector2.Zero);
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }
    }
}
