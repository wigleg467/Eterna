package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;

public enum EnemyState implements State<Entity> {
    IDLE {
        @Override
        public void enter(Entity entity) {
            Gdx.app.log("AI", "Enter IDLE");
        }

        @Override
        public void update(Entity entity) {
            Gdx.app.log("AI", "Update IDLE");
            entity.getComponent(BodyComponent.class).body.setLinearVelocity(Vector2.Zero);
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

        @Override
        public void update(Entity entity) {

        }

        @Override
        public void exit(Entity entity) {
            Gdx.app.log("AI", "Exit CHASE");
        }

        @Override
        public boolean onMessage(Entity entity, Telegram telegram) {
            return false;
        }

//        private void updatePath(Entity entity) {
//            //            Vector2 enemyPos = entity.getComponent(BodyComponent.class).getPosition();
////            Vector2 playerPos = AIUtils.getPlayerPosition();
////
////            int ex = (int)(enemyPos.x / TILE_SIZE);
////            int ey = (int)(enemyPos.y / TILE_SIZE);
////            int px = (int)(playerPos.x / TILE_SIZE);
////            int py = (int)(playerPos.y / TILE_SIZE);
////
////            List<Vector2> path = Pathfinding.findPath(NavigationMap.getInstance().getGrid(), ex, ey, px, py);
////
////            EnemyComponent ec = entity.getComponent(EnemyComponent.class);
////            ec.path.clear();
////            for (Vector2 tile : path) {
////                ec.path.add(tile.scl(TILE_SIZE)); // повертаємо в світ координат
////            }
//
//        }
    }
}
