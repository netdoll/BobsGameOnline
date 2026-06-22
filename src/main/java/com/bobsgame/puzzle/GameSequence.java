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

    public boolean downloaded = false;
    public long creatorUserID = 0;
    public String creatorUserName = "";
    public long dateCreated = 0;
    public long lastModified = 0;
    public long howManyTimesUpdated = 0;
    public long upVotes = 0;
    public long downVotes = 0;
    public String yourVote = "";

    public GameSequence() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }
}
