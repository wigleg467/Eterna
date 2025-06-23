package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.utils.Animatable;

import java.util.EnumMap;
import java.util.Map;

public class AnimationComponent implements Component {
    public Map<State, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(State.class);
    public TextureAtlas.AtlasRegion currentFrame;
    public State currentState = State.WALK;
    public float stateTime = 0f;
    public AnimationComponent(Animatable animatable, String... animationNames) {
        if (animationNames.length > 0) {
            for (int i = 0; i < animationNames.length; i++) {
                if (!animationNames[i].isEmpty()) {
                    animations.put(State.get(i), createAnimation(animatable.getAnimationPath(), animationNames[i]));
                }
            }
        }
    }

    private Animation<TextureAtlas.AtlasRegion> createAnimation(String atlasPath, String animationName) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(animationName);
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    public enum State {
        IDLE, WALK, ATTACK, DEFAULT;

        private static State get(int i) {
            return State.values()[i];
        }
    }
}
