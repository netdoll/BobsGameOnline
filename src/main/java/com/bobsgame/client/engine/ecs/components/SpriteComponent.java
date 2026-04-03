package com.bobsgame.client.engine.ecs.components;

import com.bobsgame.client.engine.ecs.Component;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class SpriteComponent extends Component {
    public Sprite sprite = null;
    public String assetId = "";
    public boolean visible = true;
    public float alpha = 1.0f;

    @Override
    public String getTypeName() {
        return "Sprite";
    }
}
