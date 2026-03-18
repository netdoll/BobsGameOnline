package com.bobsgame.net;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import com.bobsgame.puzzle.GameLogic;
import com.bobsgame.puzzle.GameLogicListener;
import com.bobsgame.shared.BobColor;
import com.google.gson.Gson;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager implements GameLogicListener {
    private Socket socket;
    private GameLogic game;
    private GameLogic opponentGame;
    private Map<String, Emitter.Listener> listeners = new HashMap<>();
    private Gson gson = new Gson();

    public NetworkManager() {
    }

    public NetworkManager(GameLogic game) {
        this();
        setGame(game);
    }

    public void setGame(GameLogic game) {
        if (this.game != null) {
            this.game.removeListener(this);
        }
        this.game = game;
        if (this.game != null) {
            this.game.addListener(this);
        }
    }

    public void setOpponentGame(GameLogic opponentGame) {
        this.opponentGame = opponentGame;
    }

    public void connect(String url) {
        if (socket != null) return;
        try {
            socket = IO.socket(url);
            setupHandlers();
            for (Map.Entry<String, Emitter.Listener> entry : listeners.entrySet()) {
                socket.on(entry.getKey(), entry.getValue());
            }
            listeners.clear();
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setupHandlers() {
        if (socket == null) return;

        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("Connected to game server");
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            System.out.println("Disconnected from game server");
        });

        socket.on("garbage", args -> {
            if (args.length > 0 && game != null) {
                Object arg = args[0];
                int amount = 0;
                if (arg instanceof Integer) amount = (int) arg;
                else if (arg instanceof Double) amount = ((Double) arg).intValue();
                else if (arg instanceof Long) amount = ((Long) arg).intValue();
                
                final int finalAmount = amount;
                game.gotVSGarbageFromOtherPlayer(finalAmount);
            }
        });

        socket.on("opponentFrame", args -> {
            if (args.length > 0 && opponentGame != null) {
                try {
                    String json = args[0].toString();
                    GameLogic.GameStateData state = gson.fromJson(json, GameLogic.GameStateData.class);
                    opponentGame.applyState(state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void listRooms() {
        if (socket != null) {
            socket.emit("listRooms");
        }
    }

    public void createRoom(String name) {
        if (socket != null) {
            socket.emit("createRoom", name);
        }
    }

    public void joinRoom(String id) {
        if (socket != null) {
            socket.emit("joinRoom", id);
        }
    }

    public void sendFrame(GameLogic.GameStateData state) {
        if (socket != null && socket.connected()) {
            socket.emit("frame", gson.toJson(state));
        }
    }

    public void on(String event, Emitter.Listener listener) {
        if (socket != null) {
            socket.on(event, listener);
        } else {
            // Store listener if socket is not connected yet
            listeners.put(event, listener);
        }
    }

    @Override
    public void onGarbageSent(int amount) {
        if (socket != null && socket.connected()) {
            socket.emit("garbage", amount);
        }
    }

    @Override
    public void onAnnouncement(String text, BobColor color) {
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
}
