package com.bobsgame.client.engine.ecs.behaviors;

import com.bobsgame.client.engine.ecs.World;
import com.bobsgame.client.engine.ecs.components.TransformComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class EightDirectionBehavior extends Behavior {
    private World world;
    private float speed = 200;

    public EightDirectionBehavior(World world) {
        this.world = world;
    }

    @Override
    public void onInit() {}

    @Override
    public void onUpdate(float dt) {
        TransformComponent transform = world.getComponent(entityId, "Transform");
        if (transform == null) return;

        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Keys.LEFT)) dx -= 1;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) dx += 1;
        if (Gdx.input.isKeyPressed(Keys.UP)) dy += 1; // LibGDX Y is up
        if (Gdx.input.isKeyPressed(Keys.DOWN)) dy -= 1;

        if (dx != 0 || dy != 0) {
            float mag = (float) Math.sqrt(dx * dx + dy * dy);
            transform.x += (dx / mag) * speed * dt;
            transform.y += (dy / mag) * speed * dt;
        }
    }
}
