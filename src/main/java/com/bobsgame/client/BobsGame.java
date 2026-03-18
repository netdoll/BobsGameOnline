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
import com.bobsgame.puzzle.GameType.GameState;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BobsGame extends NDGameEngine implements GameManager {
    public static final Logger log = LoggerFactory.getLogger(BobsGame.class);

    public GameLogic ME;
    public GameLogic opponentGame;
    public ArrayList<GameLogic> games = new ArrayList<>();
    
    public static boolean doneInitializingSprites = false;
    public static int shaderCount = 0;

    public Room currentRoom = new Room();
    private PuzzleRenderer puzzleRenderer = new PuzzleRenderer();
    private NetworkManager networkManager = null;
    private boolean isNetworkGame = false;
    private int frameCount = 0;

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
            opponentGame = new GameLogic(this, 0);
            opponentGame.isNetworkPlayer = true;
            games.add(opponentGame);

            networkManager = new NetworkManager(ME);
            networkManager.setOpponentGame(opponentGame);
            
            networkManager.on("gameStart", args -> {
                if (args.length > 0) {
                    try {
                        String json = args[0].toString();
                        // Depending on server implementation, it might be an object or string
                        // For now assume it's an object with seed
                        com.google.gson.JsonObject data = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
                        long seed = data.get("seed").getAsLong();
                        log.info("Network Game starting with seed: " + seed);
                        ME.randomSeed = seed;
                        ME.initGame();
                        ME.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            networkManager.on("joinedRoom", args -> {
                log.info("Joined room, waiting for other player...");
            });

            networkManager.connect("http://localhost:6065");
            networkManager.createRoom("JavaRoom");
        }

        // Load default game type for testing
        ME.currentGameType = new GameType();
        if (opponentGame != null) opponentGame.currentGameType = ME.currentGameType;
        log.info("BobsGame initialized");
    }

    public void update() {
        // This is called by ND or Engine
        if (ME != null) ME.update(0, games.size());
        
        if (isNetworkGame && ME != null && ME.state == GameState.PLAYING) {
            frameCount++;
            if (frameCount % 5 == 0) {
                networkManager.sendFrame(ME.getState());
            }
        }
    }

    public void render() {
        // This is called by ND or Engine
        if (ME != null) {
            if (isNetworkGame && opponentGame != null) {
                puzzleRenderer.render(ME, 100, 100, 32);
                puzzleRenderer.render(opponentGame, 500, 150, 20);
            } else {
                puzzleRenderer.render(ME, 100, 100, 32);
            }
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
