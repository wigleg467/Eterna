package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.utils.WeaponType;

public class WeaponComponent implements Component {
    public WeaponType type;

    public WeaponComponent(){
        this.type = WeaponType.HAND;
    }
}
