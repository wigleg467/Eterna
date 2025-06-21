package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.utils.Animatable;

import java.util.EnumMap;
import java.util.Map;

public class AnimationTopComponent implements Component {
    public enum TopState { IDLE, ATTACK;

        public static AnimationTopComponent.TopState get(int i) {
            return AnimationTopComponent.TopState.values()[i];
        }

    }

    public Map<TopState, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(TopState.class);
    public TextureAtlas.AtlasRegion currentFrame;
    public TopState currentState = TopState.IDLE;
    public float stateTime = 0f;


    public AnimationTopComponent(Animatable animatable, String... animationNames) {
        if (animationNames.length > 0) {
            for (int i = 0; i < animationNames.length; i++) {
                if (!animationNames[i].isEmpty()) {
                    animations.put(AnimationTopComponent.TopState.get(i), createAnimation(animatable.getAnimationPath(), animationNames[i]));
                    Gdx.app.log("AnimationTopComponent", animationNames[i]);
                }
            }
        }
    }

    private Animation<TextureAtlas.AtlasRegion> createAnimation(String atlasPath, String animationName) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(animationName);
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }
}
