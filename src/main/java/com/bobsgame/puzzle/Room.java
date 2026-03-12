package com.bobsgame.puzzle;

import java.io.Serializable;

public class Room implements Serializable {
    public String uuid = "";
    public boolean endlessMode = false;
    public float gameSpeedStart = 0.01f;
    public float gameSpeedChangeRate = 0.02f;
    public float gameSpeedMaximum = 1.0f;
    public float levelUpMultiplier = 1.0f;
    public float levelUpCompoundMultiplier = 1.0f;

    public int floorSpinLimit = -1;
    public int totalYLockDelayLimit = -1;
    public float lockDelayDecreaseRate = 0;
    public int lockDelayMinimum = 0;

    public int stackWaitLimit = -1;
    public int spawnDelayLimit = -1;
    public float spawnDelayDecreaseRate = 0;
    public int spawnDelayMinimum = 0;
    public int dropDelayMinimum = 0;

    public Room() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }
}
