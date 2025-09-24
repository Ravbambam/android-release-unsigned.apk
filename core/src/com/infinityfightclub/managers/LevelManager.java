package com.infinityfightclub.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.infinityfightclub.grid.LevelGrid;

public class LevelManager {
    public static class LevelRules { public Grid grid; public PlacementRules placementRules; public Spawn spawn; public Exit exit; public Obstacle[] obstacles; public Difficulty difficulty; public static class Grid { public int width; public int height; public int tileSize; } public static class PlacementRules { public boolean ensureConnectivity; public boolean carvePathIfBlocked; public int maxPlacementRetries = 6; public Long randomSeed; } public static class Spawn { public int margin; } public static class Exit { public int margin; } public static class Obstacle { public String id; public int[] countRange; public int[] size; public float clusterChance; public int forbiddenRadiusFromSpawn; } public static class Difficulty { public double levelScaleFactor = 1.05; } }

    public LevelRules readRules(String path) {
        FileHandle fh = Gdx.files.internal(path);
        Json json = new Json();
        json.setIgnoreUnknownFields(true);
        return json.fromJson(LevelRules.class, fh);
    }

    public LevelGrid loadFromJson(String path) {
        LevelRules rules = readRules(path);
        Long seed = rules.placementRules == null ? null : rules.placementRules.randomSeed;
        com.badlogic.gdx.Preferences p = com.badlogic.gdx.Gdx.app.getPreferences("ifc");
        long prefSeed = p.getLong("seed", 0L);
        if (seed == null) {
            if (prefSeed == 0L) {
                long newSeed = System.nanoTime();
                p.putLong("seed", newSeed);
                p.flush();
                seed = newSeed;
            } else {
                seed = prefSeed;
            }
        } else {
            if (prefSeed == 0L) {
                p.putLong("seed", seed);
                p.flush();
            }
        }
        return new LevelGenerator().generate(rules, seed);
    }
}
