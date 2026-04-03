package com.bobsgame.client.engine.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class World {
    private Map<Integer, Map<String, Component>> entities = new HashMap<>();
    private ArrayList<ECSSystem> systems = new ArrayList<>();

    public int createEntity() {
        Entity entity = new Entity();
        entities.put(entity.id, new HashMap<>());
        return entity.id;
    }

    public void addComponent(int entityId, Component component) {
        Map<String, Component> entityComponents = entities.get(entityId);
        if (entityComponents != null) {
            component.entityId = entityId;
            entityComponents.put(component.getTypeName(), component);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(int entityId, String typeName) {
        Map<String, Component> entityComponents = entities.get(entityId);
        if (entityComponents != null) {
            return (T) entityComponents.get(typeName);
        }
        return null;
    }

    public void addSystem(ECSSystem system) {
        systems.add(system);
    }

    public void update(float dt) {
        for (ECSSystem system : systems) {
            system.update(dt, entities);
        }
    }
}
