package com.bobsgame.client.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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
    private Table leaderboardTable;
    private Label statusLabel;
    private TextField roomNameField;
    private TextField roomPasswordField;
    private SelectBox<String> gameModeSelect;
    private TextField startLevelField;
    private CheckBox privateCheckbox;
    private TextField joinPasswordField;
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

        roomNameField = new TextField("Java Room", engine.uiSkin);
        roomPasswordField = new TextField("", engine.uiSkin);
        roomPasswordField.setPasswordMode(true);
        roomPasswordField.setPasswordCharacter('*');
        roomPasswordField.setMessageText("Password (Optional)");
        
        gameModeSelect = new SelectBox<>(engine.uiSkin);
        gameModeSelect.setItems("marathon", "sprint", "ultra");
        
        startLevelField = new TextField("1", engine.uiSkin);
        startLevelField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        privateCheckbox = new CheckBox(" Private", engine.uiSkin);
        joinPasswordField = new TextField("", engine.uiSkin);
        joinPasswordField.setMessageText("Join Password");

        TextButton createRoomBtn = new TextButton("Create Room", engine.uiSkin);
        createRoomBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                NetworkManager.CreateRoomOptions opts = new NetworkManager.CreateRoomOptions(
                    roomNameField.getText(), 
                    privateCheckbox.isChecked(), 
                    roomPasswordField.getText()
                );
                opts.gameMode = gameModeSelect.getSelected();
                opts.startLevel = Integer.parseInt(startLevelField.getText());
                ClientMain.clientMain.networkManager.createRoom(opts);
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
        leaderboardTable = new Table(engine.uiSkin);

        Table listsTable = new Table(engine.uiSkin);
        listsTable.add(roomListTable).expand().fill().pad(10);
        listsTable.add(leaderboardTable).expand().fill().pad(10);

        mainTable.add(titleLabel).colspan(2).pad(20).row();
        mainTable.add(statusLabel).colspan(2).pad(10).row();
        
        Table optionsTable = new Table(engine.uiSkin);
        optionsTable.add(new Label("Name:", engine.uiSkin)).right().pad(5);
        optionsTable.add(roomNameField).width(150).pad(5);
        optionsTable.add(new Label("Password:", engine.uiSkin)).right().pad(5);
        optionsTable.add(roomPasswordField).width(100).pad(5).row();
        
        optionsTable.add(new Label("Mode:", engine.uiSkin)).right().pad(5);
        optionsTable.add(gameModeSelect).width(150).pad(5);
        optionsTable.add(new Label("Level:", engine.uiSkin)).right().pad(5);
        optionsTable.add(startLevelField).width(50).pad(5).row();
        
        mainTable.add(optionsTable).colspan(2).row();
        mainTable.add(privateCheckbox).colspan(2).pad(5).row();
        mainTable.add(createRoomBtn).colspan(2).pad(10).row();
        
        mainTable.add(new Label("Join Password:", engine.uiSkin)).right();
        mainTable.add(joinPasswordField).pad(5).left().row();

        mainTable.add(listsTable).colspan(2).expand().fill().pad(20).row();
        mainTable.add(backBtn).colspan(2).pad(20).row();

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

        nm.on("leaderboard", args -> {
            if (args.length > 0) {
                try {
                    String json = args[0].toString();
                    com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    com.google.gson.JsonArray scores = data.getAsJsonArray("scores");
                    Gdx.app.postRunnable(() -> updateLeaderboard(scores));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        nm.on("joinedRoom", args -> {
            Gdx.app.postRunnable(() -> statusLabel.setText("Joined room. Waiting for players..."));
        });

        nm.on("gameStart", args -> {
            if (args.length > 0) {
                try {
                    String json = args[0].toString();
                    com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                    long seed = data.get("seed").getAsLong();
                    String mode = data.get("gameMode").getAsString();
                    int level = data.get("startLevel").getAsInt();
                    Gdx.app.postRunnable(() -> startNetworkGame(seed, mode, level));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateRoomList(List<NetworkManager.LobbyRoom> rooms) {
        roomListTable.clear();
        for (NetworkManager.LobbyRoom room : rooms) {
            String lockStr = room.hasPassword ? " 🔒" : "";
            Label nameLabel = new Label(room.name + lockStr, engine.uiSkin);
            Label playersLabel = new Label(room.players + "/" + room.maxPlayers, engine.uiSkin);
            TextButton joinBtn = new TextButton("Join", engine.uiSkin);
            
            joinBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    NetworkManager.JoinRoomOptions opts = new NetworkManager.JoinRoomOptions(
                        room.id,
                        joinPasswordField.getText()
                    );
                    ClientMain.clientMain.networkManager.joinRoom(opts);
                }
            });

            roomListTable.add(nameLabel).pad(10);
            roomListTable.add(playersLabel).pad(10);
            roomListTable.add(joinBtn).pad(10).row();
        }
    }

    private void updateLeaderboard(com.google.gson.JsonArray scores) {
        leaderboardTable.clear();
        Label title = new Label("Top Scores (Marathon)", engine.uiSkin, "bigLabel");
        title.setFontScale(0.7f);
        leaderboardTable.add(title).pad(10).row();

        for (int i = 0; i < scores.size(); i++) {
            com.google.gson.JsonObject entry = scores.get(i).getAsJsonObject();
            String name = entry.get("name").getAsString();
            long score = entry.get("score").getAsLong();
            Label scoreLabel = new Label((i + 1) + ". " + name + ": " + score + " pts", engine.uiSkin);
            leaderboardTable.add(scoreLabel).left().pad(5).row();
        }
    }

    private void startNetworkGame(long seed, String mode, int level) {
        BobsGame bobsGame = new BobsGame(Engine.ND());
        bobsGame.setNetworkGame(true);
        
        // Manual override of init() to use the settings
        bobsGame.ME = new com.bobsgame.puzzle.GameLogic(bobsGame, System.currentTimeMillis());
        bobsGame.games.add(bobsGame.ME);
        bobsGame.ME.randomSeed = seed;
        bobsGame.ME.currentLevel = level;
        
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
            ClientMain.clientMain.networkManager.getLeaderboard("marathon");
        }
    }
}
