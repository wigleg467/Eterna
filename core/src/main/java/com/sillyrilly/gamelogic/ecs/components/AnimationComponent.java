package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.entities.EntityFactory;

import java.util.EnumMap;
import java.util.Map;

public class AnimationComponent implements Component {
    public enum State {
        IDLE, WALK, ATTACK;

        private static State get(int i) {
            return State.values()[i];
        }
    }

    public AnimationComponent(EntityFactory.EntityType type, String... animation) {
        if (animation.length > 0) {
            for (int i = 0, n = animation.length; i < n; i++) {
                if (!animation[i].isEmpty()) {
                    animations.put(AnimationComponent.State.get(i), createAnimation(type, animation[i]));
                }
            }
        }
    }

    public Map<State, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(State.class);
    public TextureAtlas.AtlasRegion currentFrame;
    public State currentState = State.IDLE;
    public float stateTime = 0f;

    private Animation<TextureAtlas.AtlasRegion> createAnimation(EntityFactory.EntityType type, String animationName) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(type.getAnimationPath()));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(animationName);
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }
}
