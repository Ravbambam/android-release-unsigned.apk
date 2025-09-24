package com.infinityfightclub.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.infinityfightclub.entities.Enemy;
import com.infinityfightclub.grid.LevelGrid;

public class EnemySpawner {
    public static class EnemyRules { public EnemyType[] enemyTypes; public SpawnRules spawnRules; public static class EnemyType { public String id; public int hp; public int damage; public float speed; public int spawnWeight; public float attackCooldown = 0.6f; } public static class SpawnRules { public int[] perLevelCountRange; public int avoidSpawnRadiusFromPlayer=3; public boolean randomCountPerLevel=true; } }

    public EnemyRules readRules() {
        FileHandle fh = Gdx.files.internal("enemy_rules.json");
        Json j = new Json();
        j.setIgnoreUnknownFields(true);
        return j.fromJson(EnemyRules.class, fh);
    }

    public Array<Enemy> spawn(LevelGrid grid, int level) {
        EnemyRules r = readRules();
        int min = r.spawnRules.perLevelCountRange[0];
        int max = r.spawnRules.perLevelCountRange[1];
        int count = MathUtils.random(min, max);
        float scale = 1.0f;
        try {
            com.infinityfightclub.managers.LevelManager lm = new com.infinityfightclub.managers.LevelManager();
            com.infinityfightclub.managers.LevelManager.LevelRules lr = lm.readRules("level_rules.json");
            if (lr != null && lr.difficulty != null) scale = (float)Math.pow(lr.difficulty.levelScaleFactor <= 0 ? 1.0 : lr.difficulty.levelScaleFactor, Math.max(0, level-1));
        } catch (Exception ignored) {}
        int totalWeight = 0;
        for (EnemyRules.EnemyType t: r.enemyTypes) totalWeight += t.spawnWeight;
        Array<Enemy> list = new Array<>();
        while (list.size < count) {
            int roll = MathUtils.random(1, totalWeight);
            EnemyRules.EnemyType pick = null;
            int acc = 0;
            for (EnemyRules.EnemyType t: r.enemyTypes) { acc += t.spawnWeight; if (roll <= acc) { pick = t; break; } }
            int x = MathUtils.random(0, grid.width-1);
            int y = MathUtils.random(0, grid.height-1);
            if (grid.cellBlocked(x,y)) continue;
            if (Math.abs(x-grid.spawnX)+Math.abs(y-grid.spawnY) < r.spawnRules.avoidSpawnRadiusFromPlayer) continue;
            float wx = x*grid.tileSize+2, wy = y*grid.tileSize+2;
            list.add(new Enemy(wx,wy,16,16,Math.round(pick.hp*scale),Math.round(pick.damage*scale),pick.speed*scale));
        }
        return list;
    }
}
