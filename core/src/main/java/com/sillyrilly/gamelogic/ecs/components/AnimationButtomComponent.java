package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.sillyrilly.gamelogic.ecs.utils.Animatable;

import java.util.EnumMap;
import java.util.Map;

public class AnimationButtomComponent implements Component {
    public Map<BottomState, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(BottomState.class);
    public TextureAtlas.AtlasRegion currentFrame;
    public BottomState currentState = BottomState.IDLE;
    public float stateTime = 0f;

    public AnimationButtomComponent(Animatable animatable, String... animationNames) {
        if (animationNames.length > 0) {
            for (int i = 0; i < animationNames.length; i++) {
                if (!animationNames[i].isEmpty()) {
                    animations.put(AnimationButtomComponent.BottomState.get(i), createAnimation(animatable.getAnimationPath(), animationNames[i]));
                    Gdx.app.log("AnimationBottomComponent", animationNames[i]);
                }
            }
        }
    }

    private Animation<TextureAtlas.AtlasRegion> createAnimation(String atlasPath, String animationName) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(animationName);
        return new Animation<>(0.2f, regions, Animation.PlayMode.LOOP);
    }

    public enum BottomState {
        IDLE, WALK;

        public static BottomState get(int i) {
            return BottomState.values()[i];
        }
    }
}
