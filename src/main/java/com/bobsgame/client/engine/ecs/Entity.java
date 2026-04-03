package com.bobsgame.client.engine.ecs;

public class Entity {
    public final int id;
    private static int nextId = 0;

    public Entity() {
        this.id = ++nextId;
    }
}
