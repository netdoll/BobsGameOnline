package com.bobsgame.client.engine.ecs.systems;

import com.bobsgame.client.engine.ecs.Component;
import com.bobsgame.client.engine.ecs.ECSSystem;
import com.bobsgame.client.engine.ecs.components.BehaviorComponent;
import com.bobsgame.client.engine.ecs.behaviors.Behavior;
import java.util.Map;

public class BehaviorSystem extends ECSSystem {
    @Override
    public void update(float dt, Map<Integer, Map<String, Component>> entities) {
        for (Map<String, Component> components : entities.values()) {
            BehaviorComponent behaviorComp = (BehaviorComponent) components.get("Behavior");
            if (behaviorComp != null) {
                for (Behavior behavior : behaviorComp.behaviors) {
                    behavior.onUpdate(dt);
                }
            }
        }
    }
}
