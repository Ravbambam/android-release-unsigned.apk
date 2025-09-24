package com.infinityfightclub.grid;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class LevelGrid {
    public final int width;
    public final int height;
    public final int tileSize;
    public final TileType[][] tiles;
    public final String[][] obstacleId;
    public int spawnX;
    public int spawnY;
    public int exitX;
    public int exitY;

    public LevelGrid(int width, int height, int tileSize) {
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.tiles = new TileType[width][height];
        this.obstacleId = new String[width][height];
        for (int x=0;x<width;x++) for (int y=0;y<height;y++) { tiles[x][y] = TileType.FLOOR; obstacleId[x][y] = null; }
    }

    public int getWorldWidth() { return width * tileSize; }
    public int getWorldHeight() { return height * tileSize; }

    public boolean cellBlocked(int x, int y) {
        if (x<0||y<0||x>=width||y>=height) return true;
        TileType t = tiles[x][y];
        return t == TileType.WALL || t == TileType.OBSTACLE;
    }

    public boolean rectBlocked(Rectangle r) {
        int x0 = Math.max(0, (int)Math.floor(r.x / tileSize));
        int y0 = Math.max(0, (int)Math.floor(r.y / tileSize));
        int x1 = Math.min(width-1, (int)Math.floor((r.x + r.width) / tileSize));
        int y1 = Math.min(height-1, (int)Math.floor((r.y + r.height) / tileSize));
        for (int x=x0;x<=x1;x++) for (int y=y0;y<=y1;y++) if (cellBlocked(x,y)) return true;
        return false;
    }

    public Rectangle exitRect() {
        return new Rectangle(exitX*tileSize+2, exitY*tileSize+2, tileSize-4, tileSize-4);
    }

    public Rectangle spawnRect(float w, float h) {
        return new Rectangle(spawnX*tileSize + (tileSize-w)/2f, spawnY*tileSize + (tileSize-h)/2f, w, h);
    }

    public void renderDebug(ShapeRenderer shapes) {
        shapes.setColor(0.25f,0.25f,0.25f,1f);
        for (int x=0;x<=width;x++) {
            float wx = x * tileSize;
            shapes.line(wx, 0, wx, getWorldHeight());
        }
        for (int y=0;y<=height;y++) {
            float wy = y * tileSize;
            shapes.line(0, wy, getWorldWidth(), wy);
        }
        shapes.setColor(0,0,1,1);
        Rectangle er = exitRect();
        shapes.rect(er.x, er.y, er.width, er.height);
    }
}
