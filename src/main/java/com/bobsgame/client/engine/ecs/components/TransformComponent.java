package com.bobsgame.client.engine.ecs.components;

import com.bobsgame.client.engine.ecs.Component;

public class TransformComponent extends Component {
    public float x = 0;
    public float y = 0;
    public float rotation = 0;
    public float scaleX = 1;
    public float scaleY = 1;

    @Override
    public String getTypeName() {
        return "Transform";
    }
}
