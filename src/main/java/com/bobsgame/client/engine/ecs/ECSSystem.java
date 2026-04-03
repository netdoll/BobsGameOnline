package com.bobsgame.client.engine.ecs;

import java.util.Map;

public abstract class ECSSystem {
    public abstract void update(float dt, Map<Integer, Map<String, Component>> entities);
}
