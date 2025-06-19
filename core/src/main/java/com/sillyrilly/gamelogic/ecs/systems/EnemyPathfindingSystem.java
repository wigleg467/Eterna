package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.sillyrilly.gamelogic.ecs.components.AIComponent;
import com.sillyrilly.gamelogic.ecs.components.BodyComponent;
import com.sillyrilly.gamelogic.ecs.components.EnemyComponent;
import com.sillyrilly.gamelogic.ecs.components.PlayerComponent;
import com.sillyrilly.gamelogic.ecs.ai.EnemyState;
import com.sillyrilly.gamelogic.ecs.ai.Heuristic;
import com.sillyrilly.gamelogic.ecs.ai.TileGraph;
import com.sillyrilly.gamelogic.ecs.ai.TileNode;

import java.util.HashMap;
import java.util.Map;

public class EnemyPathfindingSystem extends EntitySystem {
    public static EnemyPathfindingSystem instance;
    public GraphPath<TileNode> path = new DefaultGraphPath<>();

    private ComponentMapper<BodyComponent> bc = ComponentMapper.getFor(BodyComponent.class);

    private TileGraph graph;
    private Heuristic heuristic;
    private IndexedAStarPathFinder<TileNode> pathFinder;

    private Map<Entity, GraphPath<TileNode>> enemyPaths = new HashMap<>();
    private Map<Entity, Integer> enemyPathIndices = new HashMap<>();

    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> player;

    public EnemyPathfindingSystem(int[][] grid) {
        instance = this;
        this.graph = new TileGraph(grid);
        this.heuristic = new Heuristic();
        this.pathFinder = new IndexedAStarPathFinder<>(graph);
    }

    @Override
    public void addedToEngine(Engine engine) {
        enemies = engine.getEntitiesFor(Family.all(EnemyComponent.class).get());
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class).get());


    }

    @Override
    public void update(float deltaTime) {
        Entity p = player.first();
        BodyComponent bcp = bc.get(p);

        for (Entity enemy : enemies) {
            BodyComponent bce = bc.get(enemy);
            if (enemy.getComponent(AIComponent.class).stateMachine.getCurrentState() != EnemyState.CHASE)
                continue;

            TileNode start = graph.getNode((int) (bce.body.getPosition().x), (int) (bce.body.getPosition().y));
            TileNode end = graph.getNode((int) (bcp.body.getPosition().x), (int) (bcp.body.getPosition().y));

//            Gdx.app.log("AI", "enemy=(" + bce.body.getPosition().x + "," + bce.body.getPosition().y + ") → tile=(" +
//                              (int) (bce.body.getPosition().x) + "," + (int) (bce.body.getPosition().y) + ")");
//
//            Gdx.app.log("AI", "player=(" + bcp.body.getPosition().x + "," + bcp.body.getPosition().y + ") → tile=(" +
//                              (int) (bcp.body.getPosition().x) + "," + (int) (bcp.body.getPosition().y) + ")");

            enemyPaths.clear();
            path.clear();
            if (pathFinder.searchNodePath(start, end, heuristic, path)) {
                enemyPaths.put(enemy, path);
                enemyPathIndices.put(enemy, 1); // Починаємо з першого кроку після старту
            }

            GraphPath<TileNode> currentPath = enemyPaths.get(enemy);
            Integer currentIndex = enemyPathIndices.get(enemy);

            if (currentPath != null && currentIndex != null && currentIndex < currentPath.getCount()) {
                TileNode nextStep = currentPath.get(currentIndex);
                float targetX = (nextStep.x + 0.5f);
                float targetY = (nextStep.y + 0.5f);

                Vector2 currentPos = bce.body.getPosition();
                Vector2 direction = new Vector2(targetX, targetY).sub(currentPos);
                float distance = direction.len();

                bce.body.setLinearVelocity(direction.scl(3f));

                if (distance < 1f) {  // Досягли точки
                    enemyPathIndices.put(enemy, currentIndex + 1);
                } else {
                    direction.nor();
                    float speed = 100f;
                    Vector2 velocity = direction.scl(speed * deltaTime);
                    bce.body.setLinearVelocity(velocity);
                }
            }

        }
    }
}
