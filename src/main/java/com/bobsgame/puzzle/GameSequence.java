package com.bobsgame.puzzle;

import java.io.Serializable;
import java.util.ArrayList;

public class GameSequence implements Serializable {
    public String uuid = "";
    public String name = "My New Game Sequence";
    public String description = "This is an empty game sequence.";
    public ArrayList<GameType> gameTypes = new ArrayList<>();
    public boolean randomizeSequence = true;
    public String currentDifficultyName = "Beginner";

    public GameSequence() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }
}
