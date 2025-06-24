package com.sillyrilly.gamelogic.ecs.utils;

public enum NPCType implements Animatable {
    LUMBERJACK("lumberjack", "animations/lumberjack.atlas", 1f, 1f, "Лісоруб", "portraits/lumberjack.png", "Натисніть Е, щоб поговорити з лісорубом"),
    DEMON("demon", "animations/demon.atlas", 1f, 1f, "Демон", "animations/demon/walk_right_01.png", "Натисніть Е, щоб поговорити з демоном"),
    NUN("nun", "animations/nun.atlas", 1f, 1f, "Монахиня", "portraits/nun.png", "Натисніть Е, щоб поговорити з монашкою"),
    GUARDCAT("guardcat", "animations/guard_cat.atlas", 0.5f, 0.5f, "Кіт охоронець", "portraits/guard_cat.png", "Натисніть Е, щоб пробувати перейти через мостикк");

    private final String id;
    private final String path, ukrName, portrait, hint;
    private final float w, h;

    NPCType(String id, String path, float w, float h, String ukrName, String portrait, String hint) {
        this.id = id;
        this.path = path;
        this.w = w;
        this.h = h;
        this.ukrName = ukrName;
        this.portrait = portrait;
        this.hint = hint;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getAnimationPath() {
        return path;
    }

    public float getWidth() {
        return w;
    }

    public float getHeight() {
        return h;
    }

    public String getName() {
        return ukrName;
    }

    public String getHint() {
        return hint;
    }

    public String getPortraitPath() {
        return portrait;
    }
}
