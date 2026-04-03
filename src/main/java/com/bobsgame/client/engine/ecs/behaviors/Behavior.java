package com.bobsgame.client.engine.ecs.behaviors;

import com.bobsgame.client.engine.ecs.Component;

public abstract class Behavior extends Component {
    public abstract void onInit();
    public abstract void onUpdate(float dt);

    @Override
    public String getTypeName() {
        return "Behavior";
    }
}
