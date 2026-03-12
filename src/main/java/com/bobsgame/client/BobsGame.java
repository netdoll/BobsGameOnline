package com.bobsgame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.bobsgame.client.engine.game.nd.ND;
import com.bobsgame.client.engine.game.nd.NDGameEngine;
import com.bobsgame.client.engine.entity.SpriteManager;
import com.bobsgame.puzzle.GameLogic;
import com.bobsgame.puzzle.GameType;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BobsGame extends NDGameEngine {
    public static final Logger log = LoggerFactory.getLogger(BobsGame.class);

    public GameLogic ME;
    public ArrayList<GameLogic> games = new ArrayList<>();
    
    public static boolean doneInitializingSprites = false;
    public static int shaderCount = 0;

    public BobsGame(ND nD) {
        super(nD);
    }

    public void init() {
        ME = new GameLogic(this, System.currentTimeMillis());
        games.add(ME);
        // Load default game type for testing
        ME.currentGameType = new GameType();
        log.info("BobsGame initialized");
    }

    public void update() {
        // This is called by ND or Engine
        ME.update(0, 1);
    }

    public void render() {
        // This is called by ND or Engine
        ME.render(); 
    }

    public static void initSprites(SpriteManager sm) {
        // TODO: Load sprites
        doneInitializingSprites = true;
    }

    public void setGameSequence(Object seq) {
        // TODO
    }
}
