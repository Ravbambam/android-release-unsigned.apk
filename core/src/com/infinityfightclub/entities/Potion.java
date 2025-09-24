package com.infinityfightclub.entities;

import com.badlogic.gdx.math.Rectangle;

public class Potion {
    public final Rectangle bounds;
    public int healAmount;

    public Potion(float x, float y, float w, float h, int healAmount) {
        this.bounds = new Rectangle(x, y, w, h);
        this.healAmount = healAmount;
    }
}
