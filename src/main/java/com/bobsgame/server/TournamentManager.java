package com.bobsgame.server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import com.bobsgame.net.BobsGameRoom;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

public class TournamentManager {
    public static Logger log = (Logger) LoggerFactory.getLogger(TournamentManager.class);

    public static class TournamentMatch {
        public String matchID;
        public long player1ID = -1;
        public long player2ID = -1;
        public long winnerID = -1;
        public String nextMatchID;
        public boolean isFinal = false;
        public int round = 0;

        public TournamentMatch(String id, int round) {
            this.matchID = id;
            this.round = round;
        }

        public String encode() {
            return matchID + ":" + player1ID + ":" + player2ID + ":" + winnerID + ":" + (nextMatchID != null ? nextMatchID : "null") + ":" + (isFinal ? "1" : "0") + ":" + round;
        }
    }

    public static class Tournament {
        public String tournamentID;
        public String roomUUID;
        public List<TournamentMatch> matches = new ArrayList<>();
        public Map<Long, String> playerNames = new HashMap<>();
        public boolean isActive = true;

        public Tournament(String id, String roomUUID) {
            this.tournamentID = id;
            this.roomUUID = roomUUID;
        }

        public String encode() {
            StringBuilder sb = new StringBuilder();
            sb.append(tournamentID).append("|").append(roomUUID).append("|");
            for (TournamentMatch m : matches) {
                sb.append(m.encode()).append(";");
            }
            return sb.toString();
        }
    }

    public static ConcurrentHashMap<String, Tournament> activeTournaments = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> roomToTournamentMap = new ConcurrentHashMap<>();

    public static Tournament createBracket(String roomUUID, List<Long> playerIDs, Map<Long, String> names) {
        String tournamentID = UUID.randomUUID().toString();
        Tournament t = new Tournament(tournamentID, roomUUID);
        t.playerNames.putAll(names);

        int numPlayers = playerIDs.size();
        if (numPlayers < 2) return null;

        // Calculate rounds needed
        int rounds = (int) Math.ceil(Math.log(numPlayers) / Math.log(2));
        int totalMatches = (int) Math.pow(2, rounds) - 1;

        // Create match placeholders
        List<TournamentMatch> allMatches = new ArrayList<>();
        for (int i = 0; i < totalMatches; i++) {
            TournamentMatch m = new TournamentMatch("M" + i, 0);
            allMatches.add(m);
        }

        // Link matches (binary tree structure)
        for (int i = 0; i < totalMatches / 2; i++) {
            allMatches.get(i * 2).nextMatchID = allMatches.get(totalMatches / 2 + i).matchID;
            allMatches.get(i * 2 + 1).nextMatchID = allMatches.get(totalMatches / 2 + i).matchID;
        }
        allMatches.get(totalMatches - 1).isFinal = true;

        // Assign players to first round
        int firstRoundMatchCount = (int) Math.pow(2, rounds - 1);
        for (int i = 0; i < numPlayers; i++) {
            int matchIdx = i / 2;
            if (i % 2 == 0) allMatches.get(matchIdx).player1ID = playerIDs.get(i);
            else allMatches.get(matchIdx).player2ID = playerIDs.get(i);
        }

        t.matches.addAll(allMatches);
        activeTournaments.put(tournamentID, t);
        roomToTournamentMap.put(roomUUID, tournamentID);
        
        log.info("Tournament " + tournamentID + " created for room " + roomUUID + " with " + numPlayers + " players.");
        return t;
    }

    public static void updateMatchWinner(String tournamentID, String matchID, long winnerID) {
        Tournament t = activeTournaments.get(tournamentID);
        if (t == null) return;

        for (TournamentMatch m : t.matches) {
            if (m.matchID.equals(matchID)) {
                m.winnerID = winnerID;
                if (m.nextMatchID != null) {
                    advanceWinner(t, m.nextMatchID, winnerID);
                } else if (m.isFinal) {
                    t.isActive = false;
                    log.info("Tournament " + tournamentID + " finished! Winner: " + winnerID);
                }
                break;
            }
        }
    }

    private static void advanceWinner(Tournament t, String nextMatchID, long winnerID) {
        for (TournamentMatch m : t.matches) {
            if (m.matchID.equals(nextMatchID)) {
                if (m.player1ID == -1) m.player1ID = winnerID;
                else if (m.player2ID == -1) m.player2ID = winnerID;
                break;
            }
        }
    }
}
