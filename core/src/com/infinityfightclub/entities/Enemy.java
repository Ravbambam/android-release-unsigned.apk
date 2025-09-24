package com.infinityfightclub.entities;

import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    public final Rectangle bounds;
    public int hp;
    public int damage;
    public float speed;
    public float attackCooldown = 0.6f;
    public float attackTimer = 0f;

    public Enemy(float x, float y, float w, float h, int hp, int damage, float speed) {
        this.bounds = new Rectangle(x, y, w, h);
        this.hp = hp; this.damage = damage; this.speed = speed;
    }
}
