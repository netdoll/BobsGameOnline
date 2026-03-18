package com.bobsgame.client.engine.game.gui.stuffMenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;
import com.bobsgame.client.engine.game.gui.stuffMenu.subMenus.*;
import java.util.ArrayList;
import java.util.List;

public class StuffMenu extends Scene2DPanel {
    private Table tabTable;
    private Table subPanelContainer;
    private ButtonGroup<TextButton> tabGroup;
    private List<Scene2DSubPanel> subPanels = new ArrayList<>();

    // Compatibility fields
    public StatusPanel statusPanel;
    public ItemsPanel itemsPanel;
    public MessagesPanel messagesPanel;
    public FriendsPanel friendsPanel;
    public LogsPanel logsPanel;
    public ControlsPanel controlsPanel;
    public SettingsPanel settingsPanel;
    public DebugInfoPanel debugInfoPanel;
    public GameEditorPanel gameEditorPanel;

    public Table mainPanelLayout;
    public Table insideScrollPaneLayout;
    public TextButton[] stuffMenuTabs;

    public float subPanelScreenWidthPercent = 0.80f;
    public float subPanelScreenHeightPercent = 0.86f;

    public StuffMenu(Engine engine) {
        super(engine);
        mainPanelLayout = this; // Alias for compatibility
        insideScrollPaneLayout = content; // Alias for compatibility
        buildUI();
    }

    private void buildUI() {
        content.clear();
        tabTable = new Table();
        subPanelContainer = new Table();
        tabGroup = new ButtonGroup<>();

        statusPanel = new StatusPanel(engine);
        itemsPanel = new ItemsPanel(engine);
        messagesPanel = new MessagesPanel(engine);
        friendsPanel = new FriendsPanel(engine);
        logsPanel = new LogsPanel(engine);
        controlsPanel = new ControlsPanel(engine);
        settingsPanel = new SettingsPanel(engine);
        debugInfoPanel = new DebugInfoPanel(engine);
        gameEditorPanel = new GameEditorPanel(engine);

        subPanels.add(statusPanel);
        subPanels.add(itemsPanel);
        subPanels.add(messagesPanel);
        subPanels.add(friendsPanel);
        subPanels.add(logsPanel);
        subPanels.add(controlsPanel);
        subPanels.add(settingsPanel);
        subPanels.add(debugInfoPanel);
        subPanels.add(gameEditorPanel);

        String[] tabNames = {
            "Status", "Items", "Messages", "Friends", 
            "Logs", "Controls", "Settings", "Debug", "Editor"
        };

        stuffMenuTabs = new TextButton[tabNames.length];
        for (int i = 0; i < tabNames.length; i++) {
            final int index = i;
            TextButton tabButton = new TextButton(tabNames[i], engine.uiSkin, "toggle");
            tabTable.add(tabButton).pad(2);
            tabGroup.add(tabButton);
            stuffMenuTabs[i] = tabButton;
            
            tabButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showSubPanel(index);
                }
            });
            
            if (i == 3 || i == 7) tabTable.row();
        }

        content.add(tabTable).pad(10).row();
        content.add(subPanelContainer).grow().pad(10).row();
    }

    private void showSubPanel(int index) {
        subPanelContainer.clear();
        if (index >= 0 && index < subPanels.size()) {
            for (Scene2DSubPanel p : subPanels) p.setVisible(false);
            Scene2DSubPanel panel = subPanels.get(index);
            panel.setVisible(true);
            subPanelContainer.add(panel).grow();
        }
    }

    public void openSubMenu(Scene2DSubPanel panel) {
        int index = subPanels.indexOf(panel);
        if (index != -1) {
            tabGroup.getButtons().get(index).setChecked(true);
            showSubPanel(index);
        }
    }

    public void init() {
        for (Scene2DSubPanel panel : subPanels) panel.init();
    }

    @Override
    public void update(long deltaTicks) {
        super.update(deltaTicks);
        if (isActivated) {
            for (Scene2DSubPanel panel : subPanels) {
                if (panel.isVisible()) panel.update(deltaTicks);
            }
        }
    }
}
