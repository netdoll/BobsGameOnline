package com.bobsgame.client.engine.ecs.systems;

import com.bobsgame.client.engine.ecs.Component;
import com.bobsgame.client.engine.ecs.ECSSystem;
import com.bobsgame.client.engine.ecs.components.EventSheetComponent;
import com.bobsgame.shared.eventsheet.EventSheet;
import java.util.Map;

public class VisualScriptSystem extends ECSSystem {
    @Override
    public void update(float dt, Map<Integer, Map<String, Component>> entities) {
        for (Map<String, Component> components : entities.values()) {
            EventSheetComponent esComp = (EventSheetComponent) components.get("EventSheet");
            if (esComp != null) {
                for (EventSheet sheet : esComp.eventSheets) {
                    for (EventSheet.EventBlock block : sheet.blocks) {
                        if (checkConditions(block, esComp)) {
                            runActions(block, esComp);
                        }
                    }
                }
            }
        }
    }

    private boolean checkConditions(EventSheet.EventBlock block, EventSheetComponent comp) {
        for (EventSheet.EventCondition cond : block.conditions) {
            if (cond.type.equals("Always")) continue;
            if (cond.type.equals("VariableEquals")) {
                String name = (String) cond.params.get("name");
                Object val = cond.params.get("value");
                if (!val.equals(comp.variables.get(name))) return false;
            }
            // More logic here
        }
        return true;
    }

    private void runActions(EventSheet.EventBlock block, EventSheetComponent comp) {
        for (EventSheet.EventAction action : block.actions) {
            if (action.type.equals("Log")) {
                System.out.println("[ECS Event] " + action.params.get("message"));
            } else if (action.type.equals("SetVariable")) {
                comp.variables.put((String) action.params.get("name"), action.params.get("value"));
            }
        }
    }
}
