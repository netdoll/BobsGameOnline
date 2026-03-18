package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.BobsGame;

public class GameSelector extends Scene2DPanel {
    public GameSelector(Engine engine) {
        super(engine);
        
        Table table = new Table(engine.uiSkin);
        
        TextButton singlePlayerBtn = new TextButton("Single Player", engine.uiSkin);
        singlePlayerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startBobsGame(false);
            }
        });
        
        TextButton multiplayerBtn = new TextButton("Multiplayer", engine.uiSkin);
        multiplayerBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Engine.GUIManager().openLobbyScreen();
            }
        });
        
        table.add(singlePlayerBtn).pad(10).row();
        table.add(multiplayerBtn).pad(10).row();
        
        content.add(table).center();
    }
    
    private void startBobsGame(boolean network) {
        BobsGame bobsGame = new BobsGame(Engine.ND());
        bobsGame.setNetworkGame(network);
        bobsGame.init();
        Engine.ND().setGame(bobsGame);
        Engine.ND().setActivated(true);
        setActivated(false);
    }
}
