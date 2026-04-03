package com.bobsgame.client.engine.game.gui.customGameEditor;

import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomGameEditor extends Scene2DPanel {
    private Table mainTable;
    private TextButton addBlockBtn;
    private TextButton addPieceBtn;
    private TextButton addRotationBtn;
    private TextButton[][] gridButtons = new TextButton[4][4];

    public CustomGameEditor(Engine engine) {
        super(engine);
        
        Skin skin = engine.guiManager.skin; // Assume global skin exists

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();
        this.addActor(mainTable);

        // 3-Port Parity: Custom Piece Editor
        addPieceBtn = new TextButton("Add Piece Type", skin);
        addRotationBtn = new TextButton("Add Rotation Phase", skin);

        Table gridTable = new Table();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                TextButton btn = new TextButton("", skin);
                gridButtons[y][x] = btn;
                gridTable.add(btn).width(40).height(40).pad(2);
            }
            gridTable.row();
        }

        mainTable.add(addPieceBtn).pad(10);
        mainTable.add(addRotationBtn).pad(10);
        mainTable.row();
        mainTable.add(gridTable).colspan(2).padTop(20);
    }
}
