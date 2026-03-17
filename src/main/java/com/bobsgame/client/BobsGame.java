package com.bobsgame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.bobsgame.client.engine.game.nd.ND;
import com.bobsgame.client.engine.game.nd.NDGameEngine;
import com.bobsgame.client.engine.entity.SpriteManager;
import com.bobsgame.client.renderer.PuzzleRenderer;
import com.bobsgame.net.NetworkManager;
import com.bobsgame.puzzle.*;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BobsGame extends NDGameEngine implements GameManager {
    public static final Logger log = LoggerFactory.getLogger(BobsGame.class);

    public GameLogic ME;
    public ArrayList<GameLogic> games = new ArrayList<>();
    
    public static boolean doneInitializingSprites = false;
    public static int shaderCount = 0;

    public Room currentRoom = new Room();
    private PuzzleRenderer puzzleRenderer = new PuzzleRenderer();
    private NetworkManager networkManager = null;
    private boolean isNetworkGame = false;

    public BobsGame(ND nD) {
        super(nD);
    }

    public void setNetworkGame(boolean network) {
        this.isNetworkGame = network;
    }

    public void init() {
        ME = new GameLogic(this, System.currentTimeMillis());
        games.add(ME);
        
        if (isNetworkGame) {
            networkManager = new NetworkManager(ME);
            networkManager.connect("http://localhost:6065");
        }

        // Load default game type for testing
        ME.currentGameType = new GameType();
        log.info("BobsGame initialized");
    }

    public void update() {
        // This is called by ND or Engine
        if (ME != null) ME.update(0, 1);
    }

    public void render() {
        // This is called by ND or Engine
        if (ME != null) {
            puzzleRenderer.render(ME, 100, 100, 32); 
        }
    }

    public static void initSprites(SpriteManager sm) {
        // TODO: Load sprites
        doneInitializingSprites = true;
    }

    public void setGameSequence(Object seq) {
        // TODO
    }

    @Override
    public boolean isNetworkGame() {
        return isNetworkGame;
    }

    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public ArrayList<GameLogic> getGames() {
        return games;
    }

    @Override
    public SpriteManager getSpriteManager() {
        return spriteManager;
    }
}
