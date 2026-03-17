package com.bobsgame.puzzle;

import com.bobsgame.client.engine.entity.SpriteManager;
import java.util.ArrayList;

public interface GameManager {
    Room getCurrentRoom();
    ArrayList<GameLogic> getGames();
    boolean isNetworkGame();
    SpriteManager getSpriteManager();
}
