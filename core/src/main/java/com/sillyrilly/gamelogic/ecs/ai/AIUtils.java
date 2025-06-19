package com.sillyrilly.gamelogic.ecs.ai;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;

public class AIUtils {
    public static Vector2 getPlayerPosition() {
        if (PlayerComponent.bc == null) {
            Gdx.app.error("AIUtils", "PlayerComponent.bc is null");
            return new Vector2(0, 0);
        }
        return PlayerComponent.bc.getPosition();
    }

    public static boolean canSeePlayer(Entity enemy) {
        Vector2 enemyPos = enemy.getComponent(BodyComponent.class).body.getPosition();
        Vector2 playerPos = getPlayerPosition();
        float dst = enemyPos.dst2(playerPos);
        return dst < 30*32f; // хз що це за число насправді
    }
}
