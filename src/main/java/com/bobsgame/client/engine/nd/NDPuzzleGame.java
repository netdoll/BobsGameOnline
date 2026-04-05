package com.bobsgame.client.engine.nd;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bobsgame.client.engine.entity.SpriteManager;
import com.bobsgame.puzzle.GameLogic;
import com.bobsgame.puzzle.GameManager;
import com.bobsgame.puzzle.PuzzlePlayer;
import com.bobsgame.puzzle.Room;

import java.util.ArrayList;

public class NDPuzzleGame extends NDGameEngine {
    private GameLogic puzzleGame;
    private PuzzlePlayer puzzlePlayer;
    private final ArrayList<GameLogic> localGames = new ArrayList<>();
    private final Room localRoom = new Room();
    private final GameManager localManager = new GameManager() {
        @Override
        public Room getCurrentRoom() {
            return localRoom;
        }

        @Override
        public ArrayList<GameLogic> getGames() {
            return localGames;
        }

        @Override
        public boolean isNetworkGame() {
            return false;
        }

        @Override
        public SpriteManager getSpriteManager() {
            return null;
        }
    };

    public NDPuzzleGame(ND nd) {
        super(nd);
    }

    @Override
    public void init() {
        localGames.clear();
        puzzleGame = new GameLogic(localManager, System.currentTimeMillis());
        localGames.add(puzzleGame);
        puzzlePlayer = new PuzzlePlayer(puzzleGame);
        puzzleGame.player = puzzlePlayer;
        puzzleGame.initGame();
        puzzleGame.start();
    }

    @Override
    public void cleanup() {
        localGames.clear();
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

            puzzleGame.update(0, 1);
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
