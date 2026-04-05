package com.bobsgame.client.engine.game.gui.customGameEditor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;
import com.bobsgame.puzzle.GameType;
import com.bobsgame.puzzle.Piece;
import com.bobsgame.puzzle.PieceType;

public class CustomGameEditor extends Scene2DPanel {
    private final Table mainTable;
    private final TextButton addPieceBtn;
    private final TextButton addRotationBtn;
    private final TextButton prevPieceBtn;
    private final TextButton nextPieceBtn;
    private final TextButton prevRotationBtn;
    private final TextButton nextRotationBtn;
    private final TextButton clearRotationBtn;
    private final TextButton[][] gridButtons = new TextButton[4][4];
    private final Label pieceLabel;
    private final Label rotationLabel;
    private final Label summaryLabel;
    private final Label hintLabel;

    private final GameType currentGameType = new GameType();
    private int selectedPieceIndex = -1;
    private int selectedRotationIndex = 0;

    public CustomGameEditor(Engine engine) {
        super(engine);

        Skin skin = engine.uiSkin;

        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left().pad(16);
        this.addActor(mainTable);

        Label titleLabel = new Label("Custom Game Editor", skin);
        pieceLabel = new Label("Piece: none", skin);
        rotationLabel = new Label("Rotation: none", skin);
        summaryLabel = new Label("No custom piece data yet.", skin);
        hintLabel = new Label("Build piece shapes with the 4x4 grid. Add pieces and rotations to sketch rules live.", skin);
        hintLabel.setWrap(true);
        summaryLabel.setWrap(true);

        addPieceBtn = new TextButton("Add Piece Type", skin);
        addRotationBtn = new TextButton("Add Rotation", skin);
        prevPieceBtn = new TextButton("< Piece", skin);
        nextPieceBtn = new TextButton("Piece >", skin);
        prevRotationBtn = new TextButton("< Rot", skin);
        nextRotationBtn = new TextButton("Rot >", skin);
        clearRotationBtn = new TextButton("Clear Rotation", skin);

        Table controlsRow1 = new Table();
        controlsRow1.defaults().pad(4);
        controlsRow1.add(addPieceBtn);
        controlsRow1.add(addRotationBtn);
        controlsRow1.add(clearRotationBtn);

        Table controlsRow2 = new Table();
        controlsRow2.defaults().pad(4);
        controlsRow2.add(prevPieceBtn);
        controlsRow2.add(nextPieceBtn);
        controlsRow2.add(prevRotationBtn);
        controlsRow2.add(nextRotationBtn);

        Table gridTable = new Table();
        gridTable.defaults().width(44).height(44).pad(2);
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                final int cellX = x;
                final int cellY = y;
                TextButton btn = new TextButton("", skin);
                btn.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        toggleCell(cellX, cellY);
                    }
                });
                gridButtons[y][x] = btn;
                gridTable.add(btn);
            }
            gridTable.row();
        }

        addPieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addPiece();
            }
        });

        addRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addRotation();
            }
        });

        clearRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearRotation();
            }
        });

        prevPieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cyclePiece(-1);
            }
        });

        nextPieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cyclePiece(1);
            }
        });

        prevRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleRotation(-1);
            }
        });

        nextRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleRotation(1);
            }
        });

        mainTable.defaults().left().padBottom(8);
        mainTable.add(titleLabel).left().row();
        mainTable.add(hintLabel).width(520).left().row();
        mainTable.add(controlsRow1).left().row();
        mainTable.add(controlsRow2).left().row();
        mainTable.add(pieceLabel).left().row();
        mainTable.add(rotationLabel).left().row();
        mainTable.add(gridTable).left().padTop(8).row();
        mainTable.add(summaryLabel).width(520).left().padTop(10).row();

        addPiece();
        refreshEditorState();
    }

    private void addPiece() {
        PieceType pieceType = new PieceType();
        pieceType.name = "Piece " + (currentGameType.pieceTypes.size() + 1);
        currentGameType.pieceTypes.add(pieceType);
        selectedPieceIndex = currentGameType.pieceTypes.size() - 1;
        selectedRotationIndex = 0;
        refreshEditorState();
    }

    private void addRotation() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null) {
            addPiece();
            pieceType = getSelectedPiece();
        }
        if (pieceType == null) return;
        if (pieceType.rotationSet == null) pieceType.rotationSet = new Piece.RotationSet("");
        pieceType.rotationSet.add(new Piece.Rotation());
        selectedRotationIndex = pieceType.rotationSet.size() - 1;
        refreshEditorState();
    }

    private void clearRotation() {
        Piece.Rotation rotation = getSelectedRotation();
        if (rotation == null) return;
        rotation.blockOffsets.clear();
        refreshEditorState();
    }

    private void cyclePiece(int delta) {
        if (currentGameType.pieceTypes.isEmpty()) return;
        if (selectedPieceIndex < 0) selectedPieceIndex = 0;
        selectedPieceIndex = (selectedPieceIndex + delta + currentGameType.pieceTypes.size()) % currentGameType.pieceTypes.size();
        selectedRotationIndex = 0;
        refreshEditorState();
    }

    private void cycleRotation(int delta) {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        selectedRotationIndex = (selectedRotationIndex + delta + pieceType.rotationSet.size()) % pieceType.rotationSet.size();
        refreshEditorState();
    }

    private void toggleCell(int x, int y) {
        Piece.Rotation rotation = getSelectedRotation();
        if (rotation == null) {
            addRotation();
            rotation = getSelectedRotation();
        }
        if (rotation == null) return;

        for (int i = 0; i < rotation.blockOffsets.size(); i++) {
            Piece.BlockOffset existing = rotation.blockOffsets.get(i);
            if (existing.x == x && existing.y == y) {
                rotation.blockOffsets.remove(i);
                refreshEditorState();
                return;
            }
        }

        rotation.blockOffsets.add(new Piece.BlockOffset(x, y));
        refreshEditorState();
    }

    private PieceType getSelectedPiece() {
        if (selectedPieceIndex < 0 || selectedPieceIndex >= currentGameType.pieceTypes.size()) return null;
        return currentGameType.pieceTypes.get(selectedPieceIndex);
    }

    private Piece.Rotation getSelectedRotation() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return null;
        if (selectedRotationIndex < 0) selectedRotationIndex = 0;
        if (selectedRotationIndex >= pieceType.rotationSet.size()) selectedRotationIndex = pieceType.rotationSet.size() - 1;
        return pieceType.rotationSet.get(selectedRotationIndex);
    }

    private int getFilledCellCount(Piece.Rotation rotation) {
        return rotation == null ? 0 : rotation.blockOffsets.size();
    }

    private int getTotalRotationCount() {
        int total = 0;
        for (PieceType pieceType : currentGameType.pieceTypes) {
            if (pieceType.rotationSet != null) total += pieceType.rotationSet.size();
        }
        return total;
    }

    private void refreshEditorState() {
        PieceType pieceType = getSelectedPiece();
        Piece.Rotation rotation = getSelectedRotation();

        pieceLabel.setText(pieceType == null ? "Piece: none" : "Piece: " + pieceType.name + " (" + (selectedPieceIndex + 1) + "/" + currentGameType.pieceTypes.size() + ")");
        rotationLabel.setText(rotation == null ? "Rotation: none" : "Rotation: " + selectedRotationIndex + " (" + getFilledCellCount(rotation) + " blocks)");

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                boolean filled = false;
                if (rotation != null) {
                    for (Piece.BlockOffset blockOffset : rotation.blockOffsets) {
                        if (blockOffset.x == x && blockOffset.y == y) {
                            filled = true;
                            break;
                        }
                    }
                }
                gridButtons[y][x].setText(filled ? "■" : "·");
            }
        }

        int currentRotationCount = pieceType != null && pieceType.rotationSet != null ? pieceType.rotationSet.size() : 0;
        summaryLabel.setText(
            "Mode: " + currentGameType.gameMode
            + " | Grid: " + currentGameType.gridWidth + "x" + currentGameType.gridHeight
            + " | Pieces: " + currentGameType.pieceTypes.size()
            + " | Rotations: " + getTotalRotationCount()
            + " | Current piece rotations: " + currentRotationCount
            + " | Filled cells in current rotation: " + getFilledCellCount(rotation)
        );
    }
}
