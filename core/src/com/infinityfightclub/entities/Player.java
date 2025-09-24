package com.infinityfightclub.entities;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    public final Rectangle bounds;
    public int hp = 100;
    public int maxHp = 100;

    public Player(float x, float y, float w, float h) {
        this.bounds = new Rectangle(x, y, w, h);
    }
}
