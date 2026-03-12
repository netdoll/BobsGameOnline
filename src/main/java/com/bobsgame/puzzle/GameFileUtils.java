package com.bobsgame.puzzle;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.bobsgame.net.BobNet;

public class GameFileUtils {
    public static List<GameType> getGameTypeList() { return new ArrayList<>(); }
    public static List<GameSequence> getGameSequenceList() { return new ArrayList<>(); }
    public static void saveGameType(GameType gt) {}
    public static void saveGameSequence(GameSequence gs) {}
    public static com.bobsgame.client.engine.game.nd.bobsgame.stats.BobsGameUserStats loadUserStats() { return null; }
}
