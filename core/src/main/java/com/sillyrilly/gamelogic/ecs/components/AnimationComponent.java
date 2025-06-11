package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.EnumMap;
import java.util.Map;

public class AnimationComponent implements Component {
    public enum State {
        IDLE, WALK, ATTACK
    }

    public Map<State, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(State.class);
    public State currentState = State.IDLE;
    public float stateTime = 0f;
}
