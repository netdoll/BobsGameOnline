package com.bobsgame.client.engine.game.gui;

import java.util.ArrayList;

import com.bobsgame.client.engine.EnginePart;
import com.bobsgame.client.engine.game.ClientGameEngine;
import com.bobsgame.client.engine.game.FriendCharacter;
import com.bobsgame.client.engine.game.gui.gameStore.GameStore;
import com.bobsgame.client.engine.game.gui.stuffMenu.StuffMenu;
import com.bobsgame.client.state.KeyboardScreen;
import com.bobsgame.client.state.LobbyScreen;

public class GUIManager extends EnginePart
{//=========================================================================================================================

	public ClientGameEngine engine;

	public StuffMenu stuffMenu = null;
	public GameStore gameStore = null;
	public PlayerEditMenu playerEditMenu = null;
    public com.bobsgame.client.engine.game.gui.gameSequenceEditor.GameSequenceEditor gameSequenceEditor = null;
    public com.bobsgame.client.engine.game.gui.customGameEditor.CustomGameEditor customGameEditor = null;
    public com.bobsgame.client.engine.game.gui.GameSelector gameSelector = null;
	public LobbyScreen lobbyScreen = null;
	public KeyboardScreen keyboardScreen;

	public ArrayList<GameChallengeNotificationPanel> gameChallenges = new ArrayList<GameChallengeNotificationPanel>();

	//this panel (with plate)
	static public String lightThemeString = "lightMenu";
	static public String darkThemeString = "darkMenu";

	static public String buttonTheme = "oppositeThemeButton";
	static public String checkboxTheme = "checkbox";
	static public String scrollPaneTheme = "themedScrollPane";

	public boolean lightTheme = false;

	//=========================================================================================================================
	public GUIManager(ClientGameEngine g)
	{//=========================================================================================================================
		super(g);
		this.engine = g;

		stuffMenu = new StuffMenu(g);
		gameStore = new GameStore(g);
		playerEditMenu = new PlayerEditMenu(g);
        gameSequenceEditor = new com.bobsgame.client.engine.game.gui.gameSequenceEditor.GameSequenceEditor(g);
        customGameEditor = new com.bobsgame.client.engine.game.gui.customGameEditor.CustomGameEditor(g);
        gameSelector = new com.bobsgame.client.engine.game.gui.GameSelector(g);
		lobbyScreen = new LobbyScreen(g);
		keyboardScreen = new KeyboardScreen(g);
	}

	//=========================================================================================================================
	public void init()
	{//=========================================================================================================================
		stuffMenu.init();
		playerEditMenu.init();
		lobbyScreen.init();
		keyboardScreen.init();
	}

	//=========================================================================================================================
	public void update()
	{//=========================================================================================================================
		long delta = engine.engineTicksPassed();
		stuffMenu.update(delta);
		gameStore.update(delta);
		playerEditMenu.update(delta);
		keyboardScreen.update(delta);
        gameSequenceEditor.update(delta);
        customGameEditor.update(delta);
        gameSelector.update(delta);
		lobbyScreen.update(delta);

		for(int i=0;i<gameChallenges.size();i++)
		{
			gameChallenges.get(i).update();
		}
	}

	//=========================================================================================================================
	public void render()
	{//=========================================================================================================================
		// Rendering is now handled by Engine.uiStage.draw()
	}

	//=========================================================================================================================
	public synchronized GameChallengeNotificationPanel makeGameChallengeNotification(FriendCharacter friend, String gameName)
	{//=========================================================================================================================
		GameChallengeNotificationPanel g = new GameChallengeNotificationPanel(friend,gameName);
		gameChallenges.add(g);
		g.setActivated(true);
		return g;
	}

	//=========================================================================================================================
	public void removeGameNotification(GameChallengeNotificationPanel g)
	{//=========================================================================================================================
		gameChallenges.remove(g);
	}

	//=========================================================================================================================
	public void cleanup()
	{//=========================================================================================================================
	}

	//=========================================================================================================================
	public void setDarkTheme()
	{//=========================================================================================================================
		lightTheme=false;
		// TODO: Update LibGDX Skin
	}

	//=========================================================================================================================
	public void setLightTheme()
	{//=========================================================================================================================
		lightTheme=true;
		// TODO: Update LibGDX Skin
	}

	public void openND() {
		closeAllMenusAndND();
		ND().setActivated(true);
	}

	public void closeND() {
		if(ND().isActivated()) ND().toggleActivated();
	}

	public void openGameStore() {
		closeAllMenusAndND();
		gameStore.setActivated(true);
	}

	public void openSettingsMenu() {
		openStuffMenu();
		stuffMenu.openSubMenu(stuffMenu.settingsPanel);
	}

	public void openFriendsMenu() {
		openStuffMenu();
		stuffMenu.openSubMenu(stuffMenu.friendsPanel);
	}

	public void openStatusMenu() {
		openStuffMenu();
		stuffMenu.openSubMenu(stuffMenu.statusPanel);
	}

	public void openLogMenu() {
		openStuffMenu();
		stuffMenu.openSubMenu(stuffMenu.logsPanel);
	}

	public void openItemsMenu() {
		openStuffMenu();
		stuffMenu.openSubMenu(stuffMenu.itemsPanel);
	}

	public void openStuffMenu() {
		closeAllMenusAndND();
		stuffMenu.setActivated(true);
	}

    public void openGameSequenceEditor() {
        closeAllMenusAndND();
        gameSequenceEditor.setActivated(true);
    }

    public void openCustomGameEditor() {
        closeAllMenusAndND();
        customGameEditor.setActivated(true);
    }

    public void openGameSelector() {
        closeAllMenusAndND();
        gameSelector.setActivated(true);
    }

	public void openLobbyScreen() {
		closeAllMenusAndND();
		lobbyScreen.setActivated(true);
	}

	public void enableAllMenusAndND() {
		keyboardScreen.setEnabled(true);
		ND().setEnabled(true);
		gameStore.setEnabled(true);
		stuffMenu.setEnabled(true);
	}

	public void disableAllMenusAndND() {
		closeAllMenusAndND();
		keyboardScreen.setEnabled(false);
		ND().setEnabled(false);
		gameStore.setEnabled(false);
		stuffMenu.setEnabled(false);
	}

	public void closeAllMenusAndND() {
		keyboardScreen.setActivated(false);
		closeND();
		gameStore.setActivated(false);
		stuffMenu.setActivated(false);
        gameSequenceEditor.setActivated(false);
        customGameEditor.setActivated(false);
        gameSelector.setActivated(false);
		lobbyScreen.setActivated(false);
	}

    public void showMessage(String message) {
        System.out.println("GUI Message: " + message);
    }

    public void addChatMessage(String msg) {
        if(stuffMenu != null && stuffMenu.messagesPanel != null) {
            stuffMenu.messagesPanel.addMessage(msg);
        }
    }
}
