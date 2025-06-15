package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sillyrilly.gamelogic.ecs.components.AnimationComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.EnemyComponent;

public class EnemyAISystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    private ImmutableArray<Entity> enemies;
    private float direction = 1;
    private float time = 0;

    @Override
    public void addedToEngine(Engine engine) {
        enemies = engine.getEntitiesFor(Family.all(BodyComponent.class, EnemyComponent.class, AnimationComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        time += deltaTime;


        if (time > 3f) {
            direction *= -1;
            time = 0;
        }

        for (Entity enemy : enemies) {
            Body body = bm.get(enemy).body;


            Vector2 velocity = body.getLinearVelocity();
            body.setLinearVelocity(direction * 1.5f, velocity.y);


            AnimationComponent anim = am.get(enemy);
            anim.currentState = AnimationComponent.State.WALK;
        }
    }
}
