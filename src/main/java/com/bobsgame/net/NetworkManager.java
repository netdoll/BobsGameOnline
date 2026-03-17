package com.bobsgame.net;

import io.socket.client.IO;
import io.socket.client.Socket;
import com.bobsgame.puzzle.GameLogic;
import com.bobsgame.puzzle.GameLogicListener;
import com.bobsgame.shared.BobColor;
import java.net.URISyntaxException;

public class NetworkManager implements GameLogicListener {
    private Socket socket;
    private GameLogic game;

    public NetworkManager(GameLogic game) {
        this.game = game;
        this.game.addListener(this);
    }

    public void connect(String url) {
        try {
            socket = IO.socket(url);
            setupHandlers();
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
            if (args.length > 0) {
                Object arg = args[0];
                int amount = 0;
                if (arg instanceof Integer) amount = (int) arg;
                else if (arg instanceof Double) amount = ((Double) arg).intValue();
                else if (arg instanceof Long) amount = ((Long) arg).intValue();
                
                final int finalAmount = amount;
                // GameLogic.update should handle this, but we need to ensure thread safety
                // For now just add to queue
                game.gotVSGarbageFromOtherPlayer(finalAmount);
            }
        });
    }

    @Override
    public void onGarbageSent(int amount) {
        if (socket != null && socket.connected()) {
            socket.emit("garbage", amount);
        }
    }

    @Override
    public void onAnnouncement(String text, BobColor color) {
        // Handle announcement if needed
    }

    public void disconnect() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }
}
