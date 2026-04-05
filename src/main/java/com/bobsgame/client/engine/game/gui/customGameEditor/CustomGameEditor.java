package com.bobsgame.client.engine.game.gui.customGameEditor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;
import com.bobsgame.client.engine.game.gui.Scene2DYesNoDialog;
import com.bobsgame.puzzle.GameType;
import com.bobsgame.puzzle.Piece;
import com.bobsgame.puzzle.PieceType;

public class CustomGameEditor extends Scene2DPanel {
    private final Table mainTable;
    private final TextButton addPieceBtn;
    private final TextButton duplicatePieceBtn;
    private final TextButton removePieceBtn;
    private final TextButton addRotationBtn;
    private final TextButton duplicateRotationBtn;
    private final TextButton removeRotationBtn;
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
    private final Label rotationOverviewLabel;
    private final Table rotationOverviewTable;
    private final CheckBox cascadeGravityCheckbox;
    private final CheckBox disconnectedGravityCheckbox;
    private final CheckBox chainRowCheckbox;
    private final CheckBox chainColumnCheckbox;
    private final CheckBox chainDiagonalCheckbox;
    private final CheckBox recursiveChainCheckbox;

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
        rotationOverviewLabel = new Label("Rotation Overview", skin);
        rotationOverviewTable = new Table();
        cascadeGravityCheckbox = new CheckBox(" Cascade gravity", skin);
        disconnectedGravityCheckbox = new CheckBox(" Disconnected-only gravity", skin);
        chainRowCheckbox = new CheckBox(" Chain rows", skin);
        chainColumnCheckbox = new CheckBox(" Chain columns", skin);
        chainDiagonalCheckbox = new CheckBox(" Chain diagonals", skin);
        recursiveChainCheckbox = new CheckBox(" Recursive chain search", skin);
        hintLabel.setWrap(true);
        summaryLabel.setWrap(true);

        addPieceBtn = new TextButton("Add Piece Type", skin);
        duplicatePieceBtn = new TextButton("Duplicate Piece", skin);
        removePieceBtn = new TextButton("Remove Piece", skin);
        addRotationBtn = new TextButton("Add Rotation", skin);
        duplicateRotationBtn = new TextButton("Duplicate Rotation", skin);
        removeRotationBtn = new TextButton("Remove Rotation", skin);
        prevPieceBtn = new TextButton("< Piece", skin);
        nextPieceBtn = new TextButton("Piece >", skin);
        prevRotationBtn = new TextButton("< Rot", skin);
        nextRotationBtn = new TextButton("Rot >", skin);
        clearRotationBtn = new TextButton("Clear Rotation", skin);

        Table controlsRow1 = new Table();
        controlsRow1.defaults().pad(4);
        controlsRow1.add(addPieceBtn);
        controlsRow1.add(duplicatePieceBtn);
        controlsRow1.add(removePieceBtn);
        controlsRow1.add(addRotationBtn);
        controlsRow1.add(duplicateRotationBtn);
        controlsRow1.add(removeRotationBtn);
        controlsRow1.add(clearRotationBtn);

        Table controlsRow2 = new Table();
        controlsRow2.defaults().pad(4);
        controlsRow2.add(prevPieceBtn);
        controlsRow2.add(nextPieceBtn);
        controlsRow2.add(prevRotationBtn);
        controlsRow2.add(nextRotationBtn);

        Table advancedRulesTable = new Table();
        advancedRulesTable.defaults().left().pad(4);
        advancedRulesTable.add(cascadeGravityCheckbox).left();
        advancedRulesTable.add(disconnectedGravityCheckbox).left().row();
        advancedRulesTable.add(chainRowCheckbox).left();
        advancedRulesTable.add(chainColumnCheckbox).left().row();
        advancedRulesTable.add(chainDiagonalCheckbox).left();
        advancedRulesTable.add(recursiveChainCheckbox).left().row();

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

        duplicatePieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                duplicatePiece();
            }
        });

        removePieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removePiece();
            }
        });

        addRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addRotation();
            }
        });

        duplicateRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                duplicateRotation();
            }
        });

        removeRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeRotation();
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

        ClickListener toggleRefreshListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyRuleCheckboxes();
                refreshEditorState();
            }
        };
        cascadeGravityCheckbox.addListener(toggleRefreshListener);
        disconnectedGravityCheckbox.addListener(toggleRefreshListener);
        chainRowCheckbox.addListener(toggleRefreshListener);
        chainColumnCheckbox.addListener(toggleRefreshListener);
        chainDiagonalCheckbox.addListener(toggleRefreshListener);
        recursiveChainCheckbox.addListener(toggleRefreshListener);

        mainTable.defaults().left().padBottom(8);
        mainTable.add(titleLabel).left().row();
        mainTable.add(hintLabel).width(520).left().row();
        mainTable.add(controlsRow1).left().row();
        mainTable.add(controlsRow2).left().row();
        mainTable.add(advancedRulesTable).left().row();
        mainTable.add(pieceLabel).left().row();
        mainTable.add(rotationLabel).left().row();
        mainTable.add(gridTable).left().padTop(8).row();
        mainTable.add(rotationOverviewLabel).left().padTop(8).row();
        mainTable.add(rotationOverviewTable).left().row();
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

    private Piece.Rotation cloneRotation(Piece.Rotation source) {
        Piece.Rotation clone = new Piece.Rotation();
        for (Piece.BlockOffset offset : source.blockOffsets) {
            clone.blockOffsets.add(new Piece.BlockOffset(offset.x, offset.y));
        }
        return clone;
    }

    private PieceType clonePiece(PieceType source) {
        PieceType clone = new PieceType();
        clone.name = (source.name == null || source.name.isEmpty()) ? "Piece Copy" : source.name + " Copy";
        clone.description = source.description;
        clone.color = source.color == null ? null : source.color.clone();
        clone.rotationSet = new Piece.RotationSet(source.rotationSet == null ? "" : source.rotationSet.name);
        if (source.rotationSet != null) {
            for (int i = 0; i < source.rotationSet.size(); i++) {
                clone.rotationSet.add(cloneRotation(source.rotationSet.get(i)));
            }
        }
        clone.frequencySpecialPieceTypeOnceEveryNPieces = source.frequencySpecialPieceTypeOnceEveryNPieces;
        clone.randomSpecialPieceChanceOneOutOf = source.randomSpecialPieceChanceOneOutOf;
        clone.flashingSpecialType = source.flashingSpecialType;
        clone.clearEveryRowPieceIsOnIfAnySingleRowCleared = source.clearEveryRowPieceIsOnIfAnySingleRowCleared;
        clone.turnBackToNormalPieceAfterNPiecesLock = source.turnBackToNormalPieceAfterNPiecesLock;
        clone.fadeOutOnceSetInsteadOfAddedToGrid = source.fadeOutOnceSetInsteadOfAddedToGrid;
        clone.useAsNormalPiece = source.useAsNormalPiece;
        clone.useAsGarbagePiece = source.useAsGarbagePiece;
        clone.useAsPlayingFieldFillerPiece = source.useAsPlayingFieldFillerPiece;
        clone.disallowAsFirstPiece = source.disallowAsFirstPiece;
        clone.spriteName = source.spriteName;
        clone.bombPiece = source.bombPiece;
        clone.weightPiece = source.weightPiece;
        clone.pieceRemovalShooterPiece = source.pieceRemovalShooterPiece;
        clone.pieceShooterPiece = source.pieceShooterPiece;
        clone.overrideBlockTypes_UUID = new java.util.ArrayList<String>(source.overrideBlockTypes_UUID);
        return clone;
    }

    private void duplicatePiece() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null) return;
        PieceType duplicate = clonePiece(pieceType);
        currentGameType.pieceTypes.add(selectedPieceIndex + 1, duplicate);
        selectedPieceIndex = selectedPieceIndex + 1;
        selectedRotationIndex = 0;
        refreshEditorState();
    }

    private void removePiece() {
        if (selectedPieceIndex < 0 || selectedPieceIndex >= currentGameType.pieceTypes.size()) return;
        final int removeIndex = selectedPieceIndex;
        final String pieceName = currentGameType.pieceTypes.get(removeIndex).name;
        Engine.GUIManager().showYesNoDialog(
            "Remove piece '" + pieceName + "' and all of its rotations?",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    currentGameType.pieceTypes.remove(removeIndex);
                    if (currentGameType.pieceTypes.isEmpty()) {
                        selectedPieceIndex = -1;
                        selectedRotationIndex = 0;
                    } else {
                        selectedPieceIndex = Math.min(removeIndex, currentGameType.pieceTypes.size() - 1);
                        selectedRotationIndex = 0;
                    }
                    refreshEditorState();
                }

                @Override
                public void onNo() {
                }
            }
        );
    }

    private void duplicateRotation() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        Piece.Rotation duplicate = cloneRotation(pieceType.rotationSet.get(selectedRotationIndex));
        pieceType.rotationSet.rotations.add(selectedRotationIndex + 1, duplicate);
        selectedRotationIndex = selectedRotationIndex + 1;
        refreshEditorState();
    }

    private void removeRotation() {
        final PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        final int removeIndex = selectedRotationIndex;
        Engine.GUIManager().showYesNoDialog(
            "Remove rotation " + removeIndex + " from '" + pieceType.name + "'?",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    pieceType.rotationSet.remove(removeIndex);
                    if (pieceType.rotationSet.size() == 0) {
                        selectedRotationIndex = 0;
                    } else {
                        selectedRotationIndex = Math.min(removeIndex, pieceType.rotationSet.size() - 1);
                    }
                    refreshEditorState();
                }

                @Override
                public void onNo() {
                }
            }
        );
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

    private void rebuildRotationOverview(PieceType pieceType) {
        rotationOverviewTable.clearChildren();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) {
            rotationOverviewTable.add(new Label("No rotations yet.", engine.uiSkin)).left();
            return;
        }

        rotationOverviewTable.defaults().pad(4);
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            final int rotationIndex = i;
            final Piece.Rotation rotation = pieceType.rotationSet.get(i);
            String prefix = (rotationIndex == selectedRotationIndex) ? "> " : "";
            TextButton button = new TextButton(prefix + "R" + rotationIndex + " (" + getFilledCellCount(rotation) + ")", engine.uiSkin);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedRotationIndex = rotationIndex;
                    refreshEditorState();
                }
            });
            rotationOverviewTable.add(button).left();
            if ((i + 1) % 4 == 0) {
                rotationOverviewTable.row();
            }
        }
    }

    private int getFilledCellCount(Piece.Rotation rotation) {
        return rotation == null ? 0 : rotation.blockOffsets.size();
    }

    private void applyRuleCheckboxes() {
        currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = cascadeGravityCheckbox.isChecked();
        currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = disconnectedGravityCheckbox.isChecked();
        currentGameType.chainRule_CheckRow = chainRowCheckbox.isChecked();
        currentGameType.chainRule_CheckColumn = chainColumnCheckbox.isChecked();
        currentGameType.chainRule_CheckDiagonal = chainDiagonalCheckbox.isChecked();
        currentGameType.chainRule_CheckRecursiveConnections = recursiveChainCheckbox.isChecked();
    }

    private String getEnabledRuleSummary() {
        java.util.ArrayList<String> enabled = new java.util.ArrayList<String>();
        if (currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) enabled.add("cascade gravity");
        if (currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks) enabled.add("disconnected gravity");
        if (currentGameType.chainRule_CheckRow) enabled.add("row chains");
        if (currentGameType.chainRule_CheckColumn) enabled.add("column chains");
        if (currentGameType.chainRule_CheckDiagonal) enabled.add("diagonal chains");
        if (currentGameType.chainRule_CheckRecursiveConnections) enabled.add("recursive search");
        return enabled.isEmpty() ? "none" : String.join(", ", enabled);
    }

    private int getTotalRotationCount() {
        int total = 0;
        for (PieceType pieceType : currentGameType.pieceTypes) {
            if (pieceType.rotationSet != null) total += pieceType.rotationSet.size();
        }
        return total;
    }

    private void refreshEditorState() {
        applyRuleCheckboxes();
        PieceType pieceType = getSelectedPiece();
        Piece.Rotation rotation = getSelectedRotation();

        cascadeGravityCheckbox.setChecked(currentGameType.moveDownAllLinesOverBlankSpacesAtOnce);
        disconnectedGravityCheckbox.setChecked(currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks);
        chainRowCheckbox.setChecked(currentGameType.chainRule_CheckRow);
        chainColumnCheckbox.setChecked(currentGameType.chainRule_CheckColumn);
        chainDiagonalCheckbox.setChecked(currentGameType.chainRule_CheckDiagonal);
        recursiveChainCheckbox.setChecked(currentGameType.chainRule_CheckRecursiveConnections);

        pieceLabel.setText(pieceType == null ? "Piece: none" : "Piece: " + pieceType.name + " (" + (selectedPieceIndex + 1) + "/" + currentGameType.pieceTypes.size() + ")");
        rotationLabel.setText(rotation == null ? "Rotation: none" : "Rotation: " + selectedRotationIndex + " (" + getFilledCellCount(rotation) + " blocks)");
        rebuildRotationOverview(pieceType);

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
                gridButtons[y][x].setDisabled(pieceType == null);
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
            + " | Rules: " + getEnabledRuleSummary()
        );
    }
}
