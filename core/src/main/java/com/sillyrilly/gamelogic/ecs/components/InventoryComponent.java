package com.sillyrilly.gamelogic.ecs.components;

import com.badlogic.ashley.core.Component;
import com.sillyrilly.gamelogic.ecs.utils.WeaponType;

import java.util.ArrayList;
import java.util.ListIterator;

public class InventoryComponent implements Component {
    public ArrayList<WeaponType> weapons = new ArrayList<>();
    public ListIterator<WeaponType> iterator = weapons.listIterator();
    public ArrayList<String> items = new ArrayList<>();

    public void addWeapon(WeaponType weapon, WeaponComponent wc) {
        weapons.add(weapon);
        refresh(wc.type);
    }

    public void addItem(String item) {
        items.add(item);
    }

    private void getNextWeapon() {
        if (iterator.hasNext()) iterator.next();
        else while (iterator.hasPrevious()) iterator.previous();

    }

    private void getPreviousWeapon() {
        if (iterator.hasPrevious()) iterator.previous();
        else while (iterator.hasPrevious()) iterator.next();
    }

    private void refresh(WeaponType wt) {
        // Перестворити ітератор зі збереженням позиції
        int index = weapons.indexOf(wt);
        iterator = weapons.listIterator(Math.max(index, 0));
    }
}
