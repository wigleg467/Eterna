package com.sillyrilly.gamelogic.ecs.utils;

public enum WeaponType {
    HAND(5),
    INFECTED_HAND(9),
    KNIFE(15),
    SWORD(25),
    AXE(35),
    FARM_FORK(50),
    MAGIC(75);

    public final float DAMAGE;

    WeaponType(float damage) {
        DAMAGE = damage;
    }
}
