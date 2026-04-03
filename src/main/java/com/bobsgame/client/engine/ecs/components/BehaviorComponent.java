package com.bobsgame.client.engine.ecs.components;

import com.bobsgame.client.engine.ecs.Component;
import com.bobsgame.client.engine.ecs.behaviors.Behavior;
import java.util.ArrayList;

public class BehaviorComponent extends Component {
    public ArrayList<Behavior> behaviors = new ArrayList<>();

    @Override
    public String getTypeName() {
        return "Behavior";
    }
}
