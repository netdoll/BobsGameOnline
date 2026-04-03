package com.bobsgame.client.engine.ecs.systems;

import com.bobsgame.client.engine.ecs.Component;
import com.bobsgame.client.engine.ecs.ECSSystem;
import com.bobsgame.client.engine.ecs.components.SpriteComponent;
import com.bobsgame.client.engine.ecs.components.TransformComponent;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.Map;

public class RenderSystem extends ECSSystem {
    private SpriteBatch batch;

    public RenderSystem(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void update(float dt, Map<Integer, Map<String, Component>> entities) {
        for (Map<String, Component> components : entities.values()) {
            TransformComponent transform = (TransformComponent) components.get("Transform");
            SpriteComponent spriteComp = (SpriteComponent) components.get("Sprite");

            if (transform != null && spriteComp != null && spriteComp.sprite != null) {
                spriteComp.sprite.setPosition(transform.x, transform.y);
                spriteComp.sprite.setRotation(transform.rotation);
                spriteComp.sprite.setScale(transform.scaleX, transform.scaleY);
                spriteComp.sprite.setAlpha(spriteComp.alpha);
                
                if (spriteComp.visible) {
                    spriteComp.sprite.draw(batch);
                }
            }
        }
    }
}
