package com.bobsgame.client.engine.nd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class NDGameEngine {
    public ND nd;

    public NDGameEngine(ND nd) {
        this.nd = nd;
    }

    public void init() {}
    public void cleanup() {}
    public void update(float dt) {}
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {}
    public abstract void titleMenuUpdate();
}
