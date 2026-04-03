package com.bobsgame.client.engine.nd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bobsgame.puzzle.GameLogic;
import com.bobsgame.puzzle.PuzzlePlayer;

public class NDPuzzleGame extends NDGameEngine {
    private GameLogic puzzleGame;
    private PuzzlePlayer puzzlePlayer;

    public NDPuzzleGame(ND nd) {
        super(nd);
    }

    @Override
    public void init() {
        puzzleGame = new GameLogic();
        puzzlePlayer = new PuzzlePlayer(puzzleGame);
        puzzleGame.initGame();
        puzzleGame.start();
    }

    @Override
    public void cleanup() {
        puzzleGame = null;
        puzzlePlayer = null;
    }

    @Override
    public void update(float dt) {
        if (puzzleGame != null && puzzlePlayer != null) {
            puzzlePlayer.UP_HELD = nd.isButtonPressed(ND.Button.UP);
            puzzlePlayer.DOWN_HELD = nd.isButtonPressed(ND.Button.DOWN);
            puzzlePlayer.LEFT_HELD = nd.isButtonPressed(ND.Button.LEFT);
            puzzlePlayer.RIGHT_HELD = nd.isButtonPressed(ND.Button.RIGHT);
            puzzlePlayer.ROTATECW_HELD = nd.isButtonPressed(ND.Button.A);
            puzzlePlayer.ROTATECCW_HELD = nd.isButtonPressed(ND.Button.B);
            puzzlePlayer.SLAM_HELD = nd.isButtonPressed(ND.Button.X);
            puzzlePlayer.HOLDRAISE_HELD = nd.isButtonPressed(ND.Button.Y);

            puzzleGame.update();
        }
    }

    @Override
    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        if (puzzleGame != null) {
            // Render logic translated to LibGDX
            // This is a stub for rendering blocks within the ND bounds
            float topScreenY = nd.y + nd.SCREEN_HEIGHT + nd.SCREEN_GAP;
            // renderer.render(batch, nd.x, topScreenY);
        }
    }

    @Override
    public void titleMenuUpdate() {}
}
