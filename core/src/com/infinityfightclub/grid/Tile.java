package com.infinityfightclub.grid;

public class Tile {
    public int x;
    public int y;
    public TileType type = TileType.FLOOR;

    public Tile(int x, int y) {
        this.x = x; this.y = y;
    }
}
