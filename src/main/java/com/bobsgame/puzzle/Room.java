package com.bobsgame.puzzle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Room implements Serializable {
    public String uuid = "";

    public String room_IsGameSequenceOrType = "";
    public String room_GameTypeName = "";
    public String room_GameSequenceName = "";
    public String room_GameTypeUUID = "";
    public String room_GameSequenceUUID = "";

    public String room_DifficultyName = "Beginner";

    public boolean singleplayer_RandomizeSequence = true;

    public int multiplayer_NumPlayers = 0;
    public long multiplayer_HostUserID = 0;

    public int multiplayer_MaxPlayers = 0;
    public boolean multiplayer_PrivateRoom = false;
    public boolean multiplayer_TournamentRoom = false;
    public boolean multiplayer_AllowDifferentDifficulties = true;
    public boolean multiplayer_AllowDifferentGameSequences = true;

    public boolean endlessMode = false;
    public boolean multiplayer_GameEndsWhenOnePlayerRemains = true;
    public boolean multiplayer_GameEndsWhenSomeoneCompletesCreditsLevel = true;
    public boolean multiplayer_DisableVSGarbage = false;

    public float gameSpeedStart = 0.01f;
    public float gameSpeedChangeRate = 0.02f;
    public float gameSpeedMaximum = 1.0f;
    public float levelUpMultiplier = 1.0f;
    public float levelUpCompoundMultiplier = 1.0f;

    public boolean multiplayer_AllowNewPlayersDuringGame = false;
    public boolean multiplayer_UseTeams = false;

    public float multiplayer_GarbageMultiplier = 1.0f;
    public int multiplayer_GarbageLimit = 0;
    public boolean multiplayer_GarbageScaleByDifficulty = true;
    public GameType.SendGarbageToRule multiplayer_SendGarbageTo = GameType.SendGarbageToRule.SEND_GARBAGE_TO_ALL_PLAYERS;

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
        this.uuid = UUID.randomUUID().toString();
    }
}
