package com.sillyrilly.gamelogic.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.sillyrilly.managers.InputManager;

public class InputSystem extends EntitySystem {
    private InputManager inputManager;
    @Override
    public void addedToEngine(Engine engine) {
        inputManager = InputManager.getInstance();
    }

    @Override
    public void update(float deltaTime) {
        inputManager.update();
    }
}
