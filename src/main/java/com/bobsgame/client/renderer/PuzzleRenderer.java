package com.bobsgame.client.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bobsgame.client.GLUtils;
import com.bobsgame.puzzle.*;
import com.bobsgame.shared.BobColor;

public class PuzzleRenderer {
    public void render(GameLogic game, float x, float y, float cellSize) {
        ShapeRenderer shape = GLUtils.shapeRenderer;
        if (shape == null) return;

        boolean wasDrawing = GLUtils.batch.isDrawing();
        if (wasDrawing) GLUtils.batch.end();

        // Background
        shape.setProjectionMatrix(GLUtils.batch.getProjectionMatrix());
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(Color.BLACK);
        shape.rect(x, y, game.grid.getWidth() * cellSize, (game.grid.getHeight() - 5) * cellSize);
        shape.end();

        // Grid Lines
        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= game.grid.getWidth(); i++) {
            shape.line(x + i * cellSize, y, x + i * cellSize, y + (game.grid.getHeight() - 5) * cellSize);
        }
        for (int i = 0; i <= game.grid.getHeight() - 5; i++) {
            shape.line(x, y + i * cellSize, x + game.grid.getWidth() * cellSize, y + i * cellSize);
        }
        shape.end();

        if (wasDrawing) GLUtils.batch.begin();

        // Blocks
        for (int gy = 5; gy < game.grid.getHeight(); gy++) {
            for (int gx = 0; gx < game.grid.getWidth(); gx++) {
                Block b = game.grid.get(gx, gy);
                if (b != null) {
                    b.render(x + gx * cellSize, y + (gy - 5) * cellSize, 1.0f, cellSize / game.grid.cellW(), true, false);
                }
            }
        }
        
        // Current Piece
        if (game.currentPiece != null) {
            for (Block b : game.currentPiece.blocks) {
                int gx = game.currentPiece.xGrid + b.xInPiece;
                int gy = game.currentPiece.yGrid + b.yInPiece;
                if (gy >= 5 && gy < game.grid.getHeight() && gx >= 0 && gx < game.grid.getWidth()) {
                    b.render(x + gx * cellSize, y + (gy - 5) * cellSize, 1.0f, cellSize / game.grid.cellW(), true, false);
                }
            }
        }
    }
}
