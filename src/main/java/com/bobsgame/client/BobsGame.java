package com.bobsgame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.bobsgame.client.engine.game.nd.ND;
import com.bobsgame.client.engine.game.nd.NDGameEngine;
import com.bobsgame.client.engine.entity.SpriteManager;
import com.bobsgame.client.renderer.PuzzleRenderer;
import com.bobsgame.net.NetworkManager;
import com.bobsgame.shared.BobColor;
import com.bobsgame.puzzle.*;
import com.bobsgame.puzzle.GameType.GameState;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BobsGame extends NDGameEngine implements GameManager {
    public static final Logger log = LoggerFactory.getLogger(BobsGame.class);

    public GameLogic ME;
    public GameLogic opponentGame;
    public ArrayList<GameLogic> games = new ArrayList<>();
    
    public static boolean doneInitializingSprites = false;
    public static int shaderCount = 0;

    public Room currentRoom = new Room();
    private PuzzleRenderer puzzleRenderer = new PuzzleRenderer();
    private NetworkManager networkManager = null;
    private boolean isNetworkGame = false;
    private int frameCount = 0;

    private com.badlogic.gdx.scenes.scene2d.ui.Table chatTable;
    private com.badlogic.gdx.scenes.scene2d.ui.Table chatLogTable;
    private com.badlogic.gdx.scenes.scene2d.ui.TextField chatInputField;
    private boolean chatActive = false;

    public BobsGame(ND nD) {
        super(nD);
    }

    public void setNetworkGame(boolean network) {
        this.isNetworkGame = network;
    }

    private void setupGameEvents() {
        ME.addListener(new GameLogicListener() {
            @Override
            public void onGarbageSent(int amount) {}

            @Override
            public void onAnnouncement(String text, BobColor color) {
                if (text.equals("GAME OVER")) {
                    if (isNetworkGame && networkManager != null) {
                        com.google.gson.JsonObject data = new com.google.gson.JsonObject();
                        data.addProperty("mode", "marathon");
                        String playerName = "JavaPlayer_" + (System.currentTimeMillis() % 1000);
                        data.addProperty("name", playerName);
                        data.addProperty("score", ME.score);
                        data.addProperty("lines", ME.linesClearedTotal);
                        data.addProperty("time", (int)(System.currentTimeMillis() - startTime));
                        networkManager.reportScore(data);
                    }
                }
            }
        });
    }

    private long startTime = 0;

    public void init() {
        ME = new GameLogic(this, System.currentTimeMillis());
        games.add(ME);
        startTime = System.currentTimeMillis();
        
        setupGameEvents();
        
        if (isNetworkGame) {
            opponentGame = new GameLogic(this, 0);
            opponentGame.isNetworkPlayer = true;
            games.add(opponentGame);

            networkManager = new NetworkManager(ME);
            networkManager.setOpponentGame(opponentGame);
            
            networkManager.on("gameStart", args -> {
                if (args.length > 0) {
                    try {
                        String json = args[0].toString();
                        com.google.gson.JsonObject data = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                        long seed = data.get("seed").getAsLong();
                        String mode = data.get("gameMode").getAsString();
                        int level = data.get("startLevel").getAsInt();
                        
                        log.info("Network Game starting with seed: " + seed + " Mode: " + mode + " Level: " + level);
                        
                        ME.randomSeed = seed;
                        ME.currentLevel = level;
                        // TODO: Set game mode logic
                        
                        ME.initGame();
                        ME.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            networkManager.on("joinedRoom", args -> {
                log.info("Joined room, waiting for other player...");
            });

            networkManager.on("chatMessage", args -> {
                if (args.length > 0) {
                    try {
                        String json = args[0].toString();
                        NetworkManager.ChatMessage msg = new com.google.gson.Gson().fromJson(json, NetworkManager.ChatMessage.class);
                        Gdx.app.postRunnable(() -> addChatMessage(msg));
                    } catch (Exception e) {}
                }
            });

            networkManager.connect("http://localhost:6065");
            networkManager.createRoom("JavaRoom");
            
            Gdx.app.postRunnable(this::initChatUI);
        }

        // Load default game type for testing
        ME.currentGameType = new GameType();
        if (opponentGame != null) opponentGame.currentGameType = ME.currentGameType;
        log.info("BobsGame initialized");
    }

    public void update() {
        if (isNetworkGame && Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.T)) {
            toggleChat();
        }

        if (chatActive) return;

        // This is called by ND or Engine
        if (ME != null) ME.update(0, games.size());
        
        if (isNetworkGame && ME != null && ME.state == GameState.PLAYING) {
            frameCount++;
            if (frameCount % 5 == 0) {
                networkManager.sendFrame(ME.getState());
            }
        }
    }

    public void render() {
        // This is called by ND or Engine
        if (ME != null) {
            if (isNetworkGame && opponentGame != null) {
                puzzleRenderer.render(ME, 100, 100, 32);
                puzzleRenderer.render(opponentGame, 500, 150, 20);
            } else {
                puzzleRenderer.render(ME, 100, 100, 32);
            }
        }

        if (chatActive && chatTable != null) {
            // Need a stage to render chatTable
            // For now assume ND stage or Engine stage
            com.bobsgame.client.engine.Engine.GUIManager().render();
        }
    }

    private void initChatUI() {
        com.badlogic.gdx.scenes.scene2d.ui.Skin skin = com.bobsgame.client.engine.Engine.GUIManager().e.uiSkin;
        chatTable = new com.badlogic.gdx.scenes.scene2d.ui.Table(skin);
        chatTable.setFillParent(true);
        chatTable.bottom().left().pad(10);

        chatLogTable = new com.badlogic.gdx.scenes.scene2d.ui.Table(skin);
        chatLogTable.bottom().left();
        com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scroll = new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane(chatLogTable, skin);
        
        chatInputField = new com.badlogic.gdx.scenes.scene2d.ui.TextField("", skin);
        chatInputField.setMessageText("Press T to chat...");
        chatInputField.setVisible(false);

        chatTable.add(scroll).width(300).height(200).left().row();
        chatTable.add(chatInputField).width(300).left();

        com.bobsgame.client.engine.Engine.GUIManager().e.uiStage.addActor(chatTable);
    }

    private void toggleChat() {
        chatActive = !chatActive;
        chatInputField.setVisible(chatActive);
        if (chatActive) {
            com.bobsgame.client.engine.Engine.GUIManager().e.uiStage.setKeyboardFocus(chatInputField);
            chatInputField.setText("");
            chatInputField.setTextFieldListener((textField, c) -> {
                if (c == '\n' || c == '\r') {
                    sendChat();
                    toggleChat();
                }
            });
        } else {
            com.bobsgame.client.engine.Engine.GUIManager().e.uiStage.setKeyboardFocus(null);
        }
    }

    private void sendChat() {
        String text = chatInputField.getText().trim();
        if (!text.isEmpty()) {
            networkManager.sendChat(text, "JavaPlayer");
        }
    }

    private void addChatMessage(NetworkManager.ChatMessage msg) {
        if (chatLogTable == null) return;
        com.badlogic.gdx.scenes.scene2d.ui.Label label = new com.badlogic.gdx.scenes.scene2d.ui.Label(msg.name + ": " + msg.message, chatLogTable.getSkin());
        label.setWrap(true);
        chatLogTable.add(label).width(280).left().row();
    }

    public static void initSprites(SpriteManager sm) {
        // TODO: Load sprites
        doneInitializingSprites = true;
    }

    public void setGameSequence(Object seq) {
        // TODO
    }

    @Override
    public boolean isNetworkGame() {
        return isNetworkGame;
    }

    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }

    @Override
    public ArrayList<GameLogic> getGames() {
        return games;
    }

    @Override
    public SpriteManager getSpriteManager() {
        return spriteManager;
    }
}
