package com.bobsgame.client.engine.nd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;

public class ND {
    public NDGameEngine activeGame = null;
    public float zoom = 1.0f;
    public float alpha = 1.0f;

    public final int SCREEN_WIDTH = 256;
    public final int SCREEN_HEIGHT = 192;
    public final int SCREEN_GAP = 90;

    public float x = 0;
    public float y = 0;

    private boolean[] buttons = new boolean[12]; // UP, DOWN, LEFT, RIGHT, A, B, X, Y, L, R, START, SELECT

    public enum Button {
        UP(0), DOWN(1), LEFT(2), RIGHT(3), A(4), B(5), X(6), Y(7), L(8), R(9), START(10), SELECT(11);
        public final int id;
        Button(int id) { this.id = id; }
    }

    public void init() {
    }

    public void setButtonState(Button b, boolean pressed) {
        buttons[b.id] = pressed;
    }

    public boolean isButtonPressed(Button b) {
        return buttons[b.id];
    }

    public void update(float dt) {
        if (activeGame != null) {
            activeGame.update(dt);
        }
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        // Draw the virtual console casing
        if (shapeRenderer != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.LIGHT_GRAY);
            shapeRenderer.rect(x - 20, y - 20, SCREEN_WIDTH + 40, (SCREEN_HEIGHT * 2) + SCREEN_GAP + 40);
            
            shapeRenderer.setColor(Color.BLACK);
            // Top Screen
            shapeRenderer.rect(x, y + SCREEN_HEIGHT + SCREEN_GAP, SCREEN_WIDTH, SCREEN_HEIGHT);
            // Bottom Screen
            shapeRenderer.rect(x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
            shapeRenderer.end();
        }

        if (activeGame != null) {
            activeGame.render(batch, shapeRenderer);
        }
    }

    public void setGame(NDGameEngine game) {
        if (this.activeGame != null) {
            this.activeGame.cleanup();
        }
        this.activeGame = game;
        if (game != null) {
            game.init();
        }
    }
}
