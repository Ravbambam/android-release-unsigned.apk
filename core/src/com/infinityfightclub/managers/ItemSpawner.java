package com.infinityfightclub.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.infinityfightclub.entities.Potion;
import com.infinityfightclub.grid.LevelGrid;

public class ItemSpawner {
    public static class ItemRules { public HealingPotion healingPotion; public static class HealingPotion { public String id; public int healAmount; public int[] perLevelCountRange; } }

    public ItemRules readRules() {
        FileHandle fh = Gdx.files.internal("item_rules.json");
        Json j = new Json();
        j.setIgnoreUnknownFields(true);
        return j.fromJson(ItemRules.class, fh);
    }

    public Array<Potion> spawnPotions(LevelGrid grid) {
        ItemRules r = readRules();
        int count = MathUtils.random(r.healingPotion.perLevelCountRange[0], r.healingPotion.perLevelCountRange[1]);
        Array<Potion> list = new Array<>();
        while (list.size < count) {
            int x = MathUtils.random(0, grid.width-1);
            int y = MathUtils.random(0, grid.height-1);
            if (grid.cellBlocked(x,y)) continue;
            if ((x==grid.spawnX && y==grid.spawnY) || (x==grid.exitX && y==grid.exitY)) continue;
            float wx = x*grid.tileSize+4, wy = y*grid.tileSize+4;
            list.add(new Potion(wx,wy,12,12,r.healingPotion.healAmount));
        }
        return list;
    }
}
