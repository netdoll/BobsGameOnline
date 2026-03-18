package com.bobsgame.client.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.ClientMain;
import com.bobsgame.client.BobsGame;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;
import com.bobsgame.net.NetworkManager;
import com.bobsgame.puzzle.GameType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LobbyScreen extends Scene2DPanel {
    private Table roomListTable;
    private Label statusLabel;
    private Gson gson = new Gson();
    private float refreshTimer = 0;

    public LobbyScreen(Engine engine) {
        super(engine);
        buildUI();
        setupNetwork();
    }

    private void buildUI() {
        content.clear();
        Table mainTable = new Table(engine.uiSkin);
        mainTable.setFillParent(true);

        Label titleLabel = new Label("Multiplayer Lobby", engine.uiSkin, "bigLabel");
        statusLabel = new Label("Connecting...", engine.uiSkin);

        TextButton createRoomBtn = new TextButton("Create Room", engine.uiSkin);
        createRoomBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ClientMain.clientMain.networkManager.createRoom("Java Room");
            }
        });

        TextButton backBtn = new TextButton("Back", engine.uiSkin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setActivated(false);
            }
        });

        roomListTable = new Table(engine.uiSkin);

        mainTable.add(titleLabel).pad(20).row();
        mainTable.add(statusLabel).pad(10).row();
        mainTable.add(createRoomBtn).pad(10).row();
        mainTable.add(roomListTable).expand().fill().pad(20).row();
        mainTable.add(backBtn).pad(20).row();

        add(mainTable);
    }

    private void setupNetwork() {
        NetworkManager nm = ClientMain.clientMain.networkManager;
        nm.connect("http://localhost:6065");

        nm.on("roomList", args -> {
            if (args.length > 0) {
                String json = args[0].toString();
                java.lang.reflect.Type listType = new TypeToken<ArrayList<NetworkManager.LobbyRoom>>(){}.getType();
                List<NetworkManager.LobbyRoom> rooms = gson.fromJson(json, listType);
                Gdx.app.postRunnable(() -> updateRoomList(rooms));
            }
        });

        nm.on("joinedRoom", args -> {
            Gdx.app.postRunnable(() -> statusLabel.setText("Joined room. Waiting for players..."));
        });

        nm.on("gameStart", args -> {
            if (args.length > 0) {
                try {
                    String json = args[0].toString();
                    com.google.gson.JsonObject data = new com.google.gson.JsonParser().parse(json).getAsJsonObject();
                    long seed = data.get("seed").getAsLong();
                    Gdx.app.postRunnable(() -> startNetworkGame(seed));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateRoomList(List<NetworkManager.LobbyRoom> rooms) {
        roomListTable.clear();
        for (NetworkManager.LobbyRoom room : rooms) {
            Label nameLabel = new Label(room.name, engine.uiSkin);
            Label playersLabel = new Label(room.players + "/" + room.maxPlayers, engine.uiSkin);
            TextButton joinBtn = new TextButton("Join", engine.uiSkin);
            
            joinBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ClientMain.clientMain.networkManager.joinRoom(room.id);
                }
            });

            roomListTable.add(nameLabel).pad(10);
            roomListTable.add(playersLabel).pad(10);
            roomListTable.add(joinBtn).pad(10).row();
        }
    }

    private void startNetworkGame(long seed) {
        BobsGame bobsGame = new BobsGame(Engine.ND());
        bobsGame.setNetworkGame(true);
        
        // Manual override of init() to use the seed
        bobsGame.ME = new com.bobsgame.puzzle.GameLogic(bobsGame, System.currentTimeMillis());
        bobsGame.games.add(bobsGame.ME);
        bobsGame.ME.randomSeed = seed;
        
        bobsGame.opponentGame = new com.bobsgame.puzzle.GameLogic(bobsGame, 0);
        bobsGame.opponentGame.isNetworkPlayer = true;
        bobsGame.games.add(bobsGame.opponentGame);

        bobsGame.ME.currentGameType = new GameType();
        bobsGame.opponentGame.currentGameType = bobsGame.ME.currentGameType;

        ClientMain.clientMain.networkManager.setGame(bobsGame.ME);
        ClientMain.clientMain.networkManager.setOpponentGame(bobsGame.opponentGame);
        
        bobsGame.ME.initGame();
        bobsGame.ME.start();

        Engine.ND().setGame(bobsGame);
        Engine.ND().setActivated(true);
        setActivated(false);
    }

    @Override
    public void update(long deltaTicks) {
        super.update(deltaTicks);
        refreshTimer += deltaTicks / 1000.0f;
        if (refreshTimer > 5.0f) {
            refreshTimer = 0;
            ClientMain.clientMain.networkManager.listRooms();
        }
    }
}
