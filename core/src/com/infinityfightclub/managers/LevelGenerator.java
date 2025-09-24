package com.infinityfightclub.managers;

import com.badlogic.gdx.math.MathUtils;
import com.infinityfightclub.grid.LevelGrid;
import com.infinityfightclub.grid.TileType;

import java.util.ArrayDeque;

public class LevelGenerator {
    public LevelGrid generate(LevelManager.LevelRules rules, Long seed) {
        MathUtils.random.setSeed(seed == null ? System.nanoTime() : seed);
        LevelGrid g = new LevelGrid(rules.grid.width, rules.grid.height, rules.grid.tileSize);
        int mS = rules.spawn != null ? rules.spawn.margin : 3;
        int mE = rules.exit != null ? rules.exit.margin : 3;
        g.spawnX = mS;
        g.spawnY = mS;
        g.exitX = g.width - 1 - mE;
        g.exitY = g.height - 1 - mE;
        placeObstacles(g, rules);
        clearSpawnArea(g, 1);
        if (rules.placementRules != null && rules.placementRules.ensureConnectivity) ensureConnectivity(g, rules);
        return g;
    }

    void placeObstacles(LevelGrid g, LevelManager.LevelRules rules) {
        if (rules.obstacles == null || rules.obstacles.length == 0) return;
        int forbid = 3;
        int[] remaining = new int[rules.obstacles.length];
        for (int i=0;i<rules.obstacles.length;i++) {
            LevelManager.LevelRules.Obstacle o = rules.obstacles[i];
            if (o.forbiddenRadiusFromSpawn > forbid) forbid = o.forbiddenRadiusFromSpawn;
            int c = 0;
            if (o.countRange != null && o.countRange.length == 2) c = MathUtils.random(o.countRange[0], o.countRange[1]);
            remaining[i] = Math.max(0, c);
        }
        int total = 0; for (int v: remaining) total += v;
        int tries = 0;
        int maxTries = (rules.placementRules==null?1000:rules.placementRules.maxPlacementRetries*Math.max(1,total));
        while (total > 0 && tries < maxTries) {
            int idx = MathUtils.random(0, remaining.length-1);
            if (remaining[idx] == 0) { tries++; continue; }
            int x = MathUtils.random(0, g.width-1);
            int y = MathUtils.random(0, g.height-1);
            if ((Math.abs(x-g.spawnX)+Math.abs(y-g.spawnY)) < forbid) { tries++; continue; }
            if ((x==g.spawnX && y==g.spawnY) || (x==g.exitX && y==g.exitY)) { tries++; continue; }
            if (g.tiles[x][y] == TileType.FLOOR) {
                g.tiles[x][y] = TileType.OBSTACLE;
                g.obstacleId[x][y] = rules.obstacles[idx].id;
                remaining[idx]--;
                total--;
            } else tries++;
        }
    }

    void clearSpawnArea(LevelGrid g, int radius) {
        for (int dx=-radius; dx<=radius; dx++) for (int dy=-radius; dy<=radius; dy++) {
            int sx = g.spawnX + dx, sy = g.spawnY + dy;
            if (sx<0||sy<0||sx>=g.width||sy>=g.height) continue;
            g.tiles[sx][sy] = com.infinityfightclub.grid.TileType.FLOOR;
            g.obstacleId[sx][sy] = null;
        }
        g.tiles[g.spawnX][g.spawnY] = com.infinityfightclub.grid.TileType.SPAWN;
    }

    void ensureConnectivity(LevelGrid g, LevelManager.LevelRules rules) {
        if (hasPath(g)) return;
        int x = g.spawnX, y = g.spawnY;
        while (x != g.exitX) { x += x < g.exitX ? 1 : -1; g.tiles[x][y] = TileType.FLOOR; }
        while (y != g.exitY) { y += y < g.exitY ? 1 : -1; g.tiles[x][y] = TileType.FLOOR; }
    }

    boolean hasPath(LevelGrid g) {
        boolean[][] vis = new boolean[g.width][g.height];
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{g.spawnX,g.spawnY});
        vis[g.spawnX][g.spawnY]=true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while(!q.isEmpty()){
            int[] cur = q.removeFirst();
            if (cur[0]==g.exitX && cur[1]==g.exitY) return true;
            for (int[] d: dirs){
                int nx=cur[0]+d[0], ny=cur[1]+d[1];
                if (nx<0||ny<0||nx>=g.width||ny>=g.height) continue;
                if (vis[nx][ny]) continue;
                if (g.cellBlocked(nx, ny)) continue;
                vis[nx][ny]=true;
                q.add(new int[]{nx,ny});
            }
        }
        return false;
    }
}
