package com.bobsgame.client.engine.ecs;

public abstract class Component {
    public int entityId = -1;
    public abstract String getTypeName();
}
