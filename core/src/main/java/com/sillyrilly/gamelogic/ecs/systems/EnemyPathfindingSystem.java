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
            PathComponent pc = this.pc.get(enemy);

            if (ai.stateMachine.getCurrentState() != EnemyState.CHASE)
                continue;

            TileNode start = graph.getNode((int) bce.getPosition().x, (int) bce.getPosition().y);
            TileNode end = graph.getNode((int) bcp.getPosition().x, (int) bcp.getPosition().y);

            pc.path.clear();

            if (pathFinder.searchNodePath(start, end, diagonalHeuristic, pc.path)) {
                pc.currentIndex = 1;
            }

            if (pc.path.getCount() > pc.currentIndex) {
                TileNode nextStep = pc.path.get(pc.currentIndex);
                float targetX = nextStep.x + 0.5f;
                float targetY = nextStep.y + 0.5f;

                Vector2 currentPos = bce.getPosition();
                Vector2 direction = new Vector2(targetX, targetY).sub(currentPos);
                float distance = direction.len();

                if (distance < 0.1f) {
                    pc.currentIndex++;
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
