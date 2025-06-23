package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;

import java.util.HashMap;
import java.util.Map;

public class InteractiveObjectComponent implements Component {
    public String type; // напр. "web", "portal", "lever"
    public Map<String, String> data = new HashMap<>(); // будь-які додаткові параметри
}
