package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.*;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;
import com.sillyrilly.gamelogic.ecs.ai.DiagonalHeuristic;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;

public class EnemyPathfindingSystem extends EntitySystem {
    private final ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<AIComponent> aic = ComponentMapper.getFor(AIComponent.class);
    private final ComponentMapper<PathComponent> pc = ComponentMapper.getFor(PathComponent.class);

    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> player;

    private TileGraph graph;
    private DiagonalHeuristic diagonalHeuristic;
    private IndexedAStarPathFinder<TileNode> pathFinder;

    @Override
    public void addedToEngine(Engine engine) {
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class, BodyComponent.class, AIComponent.class, PathComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class, BodyComponent.class).get());

        graph = TileGraph.instance;
        pathFinder = new IndexedAStarPathFinder<>(graph);
        diagonalHeuristic = new DiagonalHeuristic();
    }

    @Override
    public void update(float deltaTime) {
        Entity p = player.first();
        BodyComponent bcp = bc.get(p);

        for (Entity enemy : enemies) {
            BodyComponent bce = bc.get(enemy);
            AIComponent ai = aic.get(enemy);
            PathComponent pathComp = pc.get(enemy);

            if (ai.stateMachine.getCurrentState() != EnemyState.CHASE) {
                bce.body.setLinearVelocity(0, 0);
                continue;
            }

            pathComp.timeSinceLastUpdate += deltaTime;
            Vector2 playerPos = bcp.body.getPosition();

            boolean needsUpdate = !pathComp.lastTargetPosition.epsilonEquals(playerPos, 0.1f) || pathComp.timeSinceLastUpdate > 1f;

            if (needsUpdate) {
                pathComp.timeSinceLastUpdate = 0f;
                pathComp.lastTargetPosition.set(playerPos);

                TileNode start = graph.getNode((int) bce.getPosition().x, (int) bce.getPosition().y);
                TileNode end = graph.getNode((int) playerPos.x, (int) playerPos.y);

                pathComp.path.clear();

                if (pathFinder.searchNodePath(start, end, diagonalHeuristic, pathComp.path)) {
                    pathComp.currentIndex = 1;
                }
            }

            if (pathComp.path.getCount() > pathComp.currentIndex) {
                TileNode nextStep = pathComp.path.get(pathComp.currentIndex);
                float targetX = nextStep.x + 0.5f;
                float targetY = nextStep.y + 0.5f;

                Vector2 currentPos = bce.getPosition();
                Vector2 direction = new Vector2(targetX, targetY).sub(currentPos);
                float distance = direction.len();

                if (distance < 0.1f) {
                    pathComp.currentIndex++;
                } else {
                    direction.nor();
                    float speed = 200f;
                    Vector2 velocity = direction.scl(speed * deltaTime);
                    bce.body.setLinearVelocity(velocity);
                }
            }
        }
    }
}
