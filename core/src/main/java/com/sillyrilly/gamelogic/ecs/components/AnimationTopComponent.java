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
    public Map<WeaponComponent.WeaponType, Map<TopState, Animation<TextureAtlas.AtlasRegion>>> animations =
            new EnumMap<>(WeaponComponent.WeaponType.class);
    public TextureAtlas.AtlasRegion currentFrame;
    public TopState currentState = TopState.IDLE;
    public float stateTime = 0f;

    public AnimationTopComponent(Animatable animatable) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(animatable.getAnimationPath()));

        for (WeaponComponent.WeaponType weaponType : WeaponComponent.WeaponType.values()) {
            Map<TopState, Animation<TextureAtlas.AtlasRegion>> stateMap = new EnumMap<>(TopState.class);

            for (TopState state : TopState.values()) {
                String animName = "top_" + state.name().toLowerCase() + "_" + weaponType.name().toLowerCase();
                Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(animName);
                if (regions.size == 0) {
                    Gdx.app.log("AnimationTopComponent", "Missing animation: " + animName);
                    continue;
                }

                stateMap.put(state, new Animation<>(0.2f, regions, Animation.PlayMode.LOOP));
                Gdx.app.log("AnimationTopComponent", "Loaded animation: " + animName);
            }

            animations.put(weaponType, stateMap);
        }
    }

    public Animation<TextureAtlas.AtlasRegion> getAnimation(WeaponComponent.WeaponType weapon, TopState state) {
        Map<TopState, Animation<TextureAtlas.AtlasRegion>> stateMap = animations.get(weapon);
        if (stateMap == null) return null;
        return stateMap.get(state);
    }

    public enum TopState {IDLE, ATTACK}
}
