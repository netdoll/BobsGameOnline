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
import com.bobsgame.client.engine.game.gui.Scene2DStringDialog;
import com.bobsgame.client.engine.game.gui.Scene2DYesNoDialog;
import com.bobsgame.net.BobNet;
import com.bobsgame.puzzle.GameType;
import com.bobsgame.puzzle.Piece;
import com.bobsgame.puzzle.PieceType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CustomGameEditor extends Scene2DPanel {
    private final Table mainTable;
    private final TextButton saveSlot1Btn;
    private final TextButton loadSlot1Btn;
    private final TextButton importBtn;
    private final TextButton shareBtn;
    private final TextButton saveSlot2Btn;
    private final TextButton loadSlot2Btn;
    private final TextButton saveSlot3Btn;
    private final TextButton loadSlot3Btn;
    private final TextButton presetClassicBtn;
    private final TextButton presetCascadeBtn;
    private final TextButton presetStackBtn;
    private final TextButton addPieceBtn;
    private final TextButton duplicatePieceBtn;
    private final TextButton removePieceBtn;
    private final TextButton addRotationBtn;
    private final TextButton duplicateRotationBtn;
    private final TextButton normalizeRotationBtn;
    private final TextButton centerRotationBtn;
    private final TextButton centerAllRotationsBtn;
    private final TextButton normalizeAllRotationsBtn;
    private final TextButton removeDuplicateRotationsBtn;
    private final TextButton removeEmptyRotationsBtn;
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
    private final Label recentHistoryLabel;
    private final Table rotationOverviewTable;
    private final Table recentHistoryTable;
    private final CheckBox cascadeGravityCheckbox;
    private final CheckBox disconnectedGravityCheckbox;
    private final CheckBox chainRowCheckbox;
    private final CheckBox chainColumnCheckbox;
    private final CheckBox chainDiagonalCheckbox;
    private final CheckBox recursiveChainCheckbox;
    private final CheckBox nextPieceEnabledCheckbox;
    private final CheckBox holdPieceEnabledCheckbox;
    private final CheckBox bagRandomizerCheckbox;
    private final CheckBox hardDropPunchCheckbox;
    private final CheckBox twoSpaceWallKickCheckbox;
    private final CheckBox diagonalWallKickCheckbox;
    private final CheckBox pieceClimbingCheckbox;
    private final CheckBox flip180Checkbox;
    private final CheckBox floorKickCheckbox;

    private GameType currentGameType = new GameType();
    private final GameType[] presetSlots = new GameType[3];
    private final java.util.ArrayList<RecentGameHistoryEntry> recentHistory = new java.util.ArrayList<RecentGameHistoryEntry>();
    private int selectedPieceIndex = -1;
    private int selectedRotationIndex = 0;

    private static class RecentGameHistoryEntry {
        public String source;
        public String payload;
        public String gameName;
        public int pieceCount;
        public int rotationCount;
        public long timestamp;
    }

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
        recentHistoryLabel = new Label("Recent Share / Import History", skin);
        rotationOverviewTable = new Table();
        recentHistoryTable = new Table();
        cascadeGravityCheckbox = new CheckBox(" Cascade gravity", skin);
        disconnectedGravityCheckbox = new CheckBox(" Disconnected-only gravity", skin);
        chainRowCheckbox = new CheckBox(" Chain rows", skin);
        chainColumnCheckbox = new CheckBox(" Chain columns", skin);
        chainDiagonalCheckbox = new CheckBox(" Chain diagonals", skin);
        recursiveChainCheckbox = new CheckBox(" Recursive chain search", skin);
        nextPieceEnabledCheckbox = new CheckBox(" Show next pieces", skin);
        holdPieceEnabledCheckbox = new CheckBox(" Enable hold piece", skin);
        bagRandomizerCheckbox = new CheckBox(" Use bag randomizer", skin);
        hardDropPunchCheckbox = new CheckBox(" Hard-drop punch-through", skin);
        twoSpaceWallKickCheckbox = new CheckBox(" Two-space wall kick", skin);
        diagonalWallKickCheckbox = new CheckBox(" Diagonal wall kick", skin);
        pieceClimbingCheckbox = new CheckBox(" Piece climbing", skin);
        flip180Checkbox = new CheckBox(" Allow 180 flip", skin);
        floorKickCheckbox = new CheckBox(" Allow floor kick", skin);
        hintLabel.setWrap(true);
        summaryLabel.setWrap(true);

        saveSlot1Btn = new TextButton("Save Slot 1", skin);
        loadSlot1Btn = new TextButton("Load Slot 1", skin);
        importBtn = new TextButton("Import", skin);
        shareBtn = new TextButton("Share", skin);
        saveSlot2Btn = new TextButton("Save Slot 2", skin);
        loadSlot2Btn = new TextButton("Load Slot 2", skin);
        saveSlot3Btn = new TextButton("Save Slot 3", skin);
        loadSlot3Btn = new TextButton("Load Slot 3", skin);
        presetClassicBtn = new TextButton("Classic Drop", skin);
        presetCascadeBtn = new TextButton("Cascade Puzzle", skin);
        presetStackBtn = new TextButton("Stack Arcade", skin);
        addPieceBtn = new TextButton("Add Piece Type", skin);
        duplicatePieceBtn = new TextButton("Duplicate Piece", skin);
        removePieceBtn = new TextButton("Remove Piece", skin);
        addRotationBtn = new TextButton("Add Rotation", skin);
        duplicateRotationBtn = new TextButton("Duplicate Rotation", skin);
        normalizeRotationBtn = new TextButton("Normalize Rotation", skin);
        centerRotationBtn = new TextButton("Center Rotation", skin);
        centerAllRotationsBtn = new TextButton("Center All", skin);
        normalizeAllRotationsBtn = new TextButton("Normalize All", skin);
        removeDuplicateRotationsBtn = new TextButton("Clear Duplicates", skin);
        removeEmptyRotationsBtn = new TextButton("Clear Empty", skin);
        removeRotationBtn = new TextButton("Remove Rotation", skin);
        prevPieceBtn = new TextButton("< Piece", skin);
        nextPieceBtn = new TextButton("Piece >", skin);
        prevRotationBtn = new TextButton("< Rot", skin);
        nextRotationBtn = new TextButton("Rot >", skin);
        clearRotationBtn = new TextButton("Clear Rotation", skin);

        Table presetRow = new Table();
        presetRow.defaults().pad(4);
        presetRow.add(saveSlot1Btn);
        presetRow.add(loadSlot1Btn);
        presetRow.add(saveSlot2Btn);
        presetRow.add(loadSlot2Btn);
        presetRow.add(saveSlot3Btn);
        presetRow.add(loadSlot3Btn);
        presetRow.add(importBtn);
        presetRow.add(shareBtn);

        Table presetQuickRow = new Table();
        presetQuickRow.defaults().pad(4);
        presetQuickRow.add(presetClassicBtn);
        presetQuickRow.add(presetCascadeBtn);
        presetQuickRow.add(presetStackBtn);

        Table controlsRow1 = new Table();
        controlsRow1.defaults().pad(4);
        controlsRow1.add(addPieceBtn);
        controlsRow1.add(duplicatePieceBtn);
        controlsRow1.add(removePieceBtn);
        controlsRow1.add(addRotationBtn);
        controlsRow1.add(duplicateRotationBtn);
        controlsRow1.add(normalizeRotationBtn);
        controlsRow1.add(centerRotationBtn);
        controlsRow1.add(centerAllRotationsBtn);
        controlsRow1.add(normalizeAllRotationsBtn);
        controlsRow1.add(removeDuplicateRotationsBtn);
        controlsRow1.add(removeEmptyRotationsBtn);
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
        advancedRulesTable.add(nextPieceEnabledCheckbox).left();
        advancedRulesTable.add(holdPieceEnabledCheckbox).left().row();
        advancedRulesTable.add(bagRandomizerCheckbox).left();
        advancedRulesTable.add(hardDropPunchCheckbox).left().row();
        advancedRulesTable.add(twoSpaceWallKickCheckbox).left();
        advancedRulesTable.add(diagonalWallKickCheckbox).left().row();
        advancedRulesTable.add(pieceClimbingCheckbox).left();
        advancedRulesTable.add(flip180Checkbox).left().row();
        advancedRulesTable.add(floorKickCheckbox).left().row();

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

        saveSlot1Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                savePresetSlot(0);
            }
        });
        loadSlot1Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadPresetSlot(0);
            }
        });
        importBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showImportDialog();
            }
        });
        shareBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shareGame();
            }
        });
        saveSlot2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                savePresetSlot(1);
            }
        });
        loadSlot2Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadPresetSlot(1);
            }
        });
        saveSlot3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                savePresetSlot(2);
            }
        });
        loadSlot3Btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                loadPresetSlot(2);
            }
        });
        presetClassicBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("classic");
            }
        });
        presetCascadeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("cascade");
            }
        });
        presetStackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("stack");
            }
        });

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

        normalizeRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                normalizeCurrentRotation();
            }
        });

        centerRotationBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                centerCurrentRotation();
            }
        });

        centerAllRotationsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                centerAllRotations();
            }
        });

        normalizeAllRotationsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                normalizeAllRotations();
            }
        });

        removeDuplicateRotationsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeDuplicateRotations();
            }
        });

        removeEmptyRotationsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeEmptyRotations();
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
        nextPieceEnabledCheckbox.addListener(toggleRefreshListener);
        holdPieceEnabledCheckbox.addListener(toggleRefreshListener);
        bagRandomizerCheckbox.addListener(toggleRefreshListener);
        hardDropPunchCheckbox.addListener(toggleRefreshListener);
        twoSpaceWallKickCheckbox.addListener(toggleRefreshListener);
        diagonalWallKickCheckbox.addListener(toggleRefreshListener);
        pieceClimbingCheckbox.addListener(toggleRefreshListener);
        flip180Checkbox.addListener(toggleRefreshListener);
        floorKickCheckbox.addListener(toggleRefreshListener);

        mainTable.defaults().left().padBottom(8);
        mainTable.add(titleLabel).left().row();
        mainTable.add(hintLabel).width(520).left().row();
        mainTable.add(presetRow).left().row();
        mainTable.add(presetQuickRow).left().row();
        mainTable.add(controlsRow1).left().row();
        mainTable.add(controlsRow2).left().row();
        mainTable.add(advancedRulesTable).left().row();
        mainTable.add(pieceLabel).left().row();
        mainTable.add(rotationLabel).left().row();
        mainTable.add(gridTable).left().padTop(8).row();
        mainTable.add(rotationOverviewLabel).left().padTop(8).row();
        mainTable.add(rotationOverviewTable).left().row();
        mainTable.add(recentHistoryLabel).left().padTop(8).row();
        mainTable.add(recentHistoryTable).left().row();
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

    private GameType deepCloneGameType(GameType source) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(source);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            return (GameType) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone GameType preset", e);
        }
    }

    private void savePresetSlot(int slotIndex) {
        applyRuleCheckboxes();
        presetSlots[slotIndex] = deepCloneGameType(currentGameType);
        summaryLabel.setText("Saved current ruleset to preset slot " + (slotIndex + 1));
    }

    private void applyPreset(String preset) {
        applyRuleCheckboxes();
        if ("classic".equals(preset)) {
            currentGameType.name = "Classic Drop";
            currentGameType.gameMode = GameType.GameMode.DROP;
            currentGameType.gridWidth = 10;
            currentGameType.gridHeight = 20;
            currentGameType.chainRule_AmountPerChain = 4;
            currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = false;
            currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = false;
            currentGameType.chainRule_CheckRow = true;
            currentGameType.chainRule_CheckColumn = false;
            currentGameType.chainRule_CheckDiagonal = false;
            currentGameType.chainRule_CheckRecursiveConnections = false;
            currentGameType.nextPieceEnabled = true;
            currentGameType.holdPieceEnabled = true;
            currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = true;
            currentGameType.hardDropPunchThroughToLowestValidGridPosition = false;
            currentGameType.twoSpaceWallKickAllowed = true;
            currentGameType.diagonalWallKickAllowed = true;
            currentGameType.pieceClimbingAllowed = true;
            currentGameType.flip180Allowed = true;
            currentGameType.floorKickAllowed = true;
        } else if ("cascade".equals(preset)) {
            currentGameType.name = "Cascade Puzzle";
            currentGameType.gameMode = GameType.GameMode.DROP;
            currentGameType.gridWidth = 8;
            currentGameType.gridHeight = 16;
            currentGameType.chainRule_AmountPerChain = 3;
            currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = true;
            currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = true;
            currentGameType.chainRule_CheckRow = true;
            currentGameType.chainRule_CheckColumn = true;
            currentGameType.chainRule_CheckDiagonal = true;
            currentGameType.chainRule_CheckRecursiveConnections = true;
            currentGameType.nextPieceEnabled = true;
            currentGameType.holdPieceEnabled = false;
            currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = false;
            currentGameType.hardDropPunchThroughToLowestValidGridPosition = false;
            currentGameType.twoSpaceWallKickAllowed = false;
            currentGameType.diagonalWallKickAllowed = false;
            currentGameType.pieceClimbingAllowed = false;
            currentGameType.flip180Allowed = false;
            currentGameType.floorKickAllowed = false;
        } else if ("stack".equals(preset)) {
            currentGameType.name = "Stack Arcade";
            currentGameType.gameMode = GameType.GameMode.STACK;
            currentGameType.gridWidth = 6;
            currentGameType.gridHeight = 12;
            currentGameType.chainRule_AmountPerChain = 3;
            currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = false;
            currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = false;
            currentGameType.chainRule_CheckRow = true;
            currentGameType.chainRule_CheckColumn = true;
            currentGameType.chainRule_CheckDiagonal = false;
            currentGameType.chainRule_CheckRecursiveConnections = false;
            currentGameType.nextPieceEnabled = true;
            currentGameType.holdPieceEnabled = false;
            currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = false;
            currentGameType.hardDropPunchThroughToLowestValidGridPosition = false;
            currentGameType.twoSpaceWallKickAllowed = true;
            currentGameType.diagonalWallKickAllowed = false;
            currentGameType.pieceClimbingAllowed = false;
            currentGameType.flip180Allowed = false;
            currentGameType.floorKickAllowed = false;
        }
        selectedPieceIndex = currentGameType.pieceTypes.isEmpty() ? -1 : 0;
        selectedRotationIndex = 0;
        refreshEditorState();
    }

    private void loadPresetSlot(int slotIndex) {
        if (presetSlots[slotIndex] == null) {
            summaryLabel.setText("Preset slot " + (slotIndex + 1) + " is empty.");
            return;
        }
        currentGameType = deepCloneGameType(presetSlots[slotIndex]);
        selectedPieceIndex = currentGameType.pieceTypes.isEmpty() ? -1 : 0;
        selectedRotationIndex = 0;
        refreshEditorState();
    }

    private void showImportDialog() {
        Engine.GUIManager().showStringDialog(
            "Paste full share URL or raw #play payload",
            "",
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                    importSharedGame(text);
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void importSharedGame(String input) {
        if (input == null) return;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return;
        int playIndex = trimmed.indexOf("#play=");
        String payload = playIndex >= 0 ? trimmed.substring(playIndex + 6) : trimmed;
        try {
            Object decoded = BobNet.fromBase64GZippedGSON(payload, GameType.class);
            if (!(decoded instanceof GameType)) throw new RuntimeException("Decoded payload was empty.");
            currentGameType = (GameType) decoded;
            selectedPieceIndex = currentGameType.pieceTypes.isEmpty() ? -1 : 0;
            selectedRotationIndex = 0;
            pushRecentHistoryEntry("import", payload, currentGameType);
            refreshEditorState();
            summaryLabel.setText("Imported shared game configuration.");
        } catch (Exception e) {
            e.printStackTrace();
            summaryLabel.setText("Failed to import shared game configuration.");
        }
    }

    private void shareGame() {
        applyRuleCheckboxes();
        String payload = currentGameType.toBase64GZippedGSON();
        String url = "https://bobsgame.com/#play=" + payload;
        pushRecentHistoryEntry("share", payload, currentGameType);
        if (copyTextToClipboard(url)) {
            summaryLabel.setText("Share link copied to clipboard.");
            return;
        }
        Engine.GUIManager().showStringDialog(
            "Copy this share link",
            url,
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                }

                @Override
                public void onCancel() {
                }
            }
        );
        summaryLabel.setText("Clipboard unavailable. Share link opened for manual copy.");
    }

    private boolean copyTextToClipboard(String text) {
        try {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(text);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void pushRecentHistoryEntry(String source, String payload, GameType gameType) {
        RecentGameHistoryEntry entry = new RecentGameHistoryEntry();
        entry.source = source;
        entry.payload = payload;
        entry.gameName = gameType.name == null || gameType.name.isEmpty() ? ("share".equals(source) ? "Shared Ruleset" : "Imported Ruleset") : gameType.name;
        entry.pieceCount = gameType.pieceTypes.size();
        entry.rotationCount = getTotalRotationCount(gameType);
        entry.timestamp = System.currentTimeMillis();

        for (int i = 0; i < recentHistory.size(); i++) {
            if (payload.equals(recentHistory.get(i).payload)) {
                recentHistory.remove(i);
                break;
            }
        }
        recentHistory.add(0, entry);
        while (recentHistory.size() > 5) recentHistory.remove(recentHistory.size() - 1);
        rebuildRecentHistoryTable();
    }

    private int getTotalRotationCount(GameType gameType) {
        int total = 0;
        for (PieceType pieceType : gameType.pieceTypes) {
            if (pieceType.rotationSet != null) total += pieceType.rotationSet.size();
        }
        return total;
    }

    private void loadRecentHistoryEntry(int historyIndex) {
        if (historyIndex < 0 || historyIndex >= recentHistory.size()) return;
        RecentGameHistoryEntry entry = recentHistory.get(historyIndex);
        importSharedGame(entry.payload);
    }

    private void copyRecentHistoryEntry(int historyIndex) {
        if (historyIndex < 0 || historyIndex >= recentHistory.size()) return;
        RecentGameHistoryEntry entry = recentHistory.get(historyIndex);
        String url = "https://bobsgame.com/#play=" + entry.payload;
        if (copyTextToClipboard(url)) {
            summaryLabel.setText("Copied recent " + entry.source + " link to clipboard.");
            return;
        }
        Engine.GUIManager().showStringDialog(
            "Copy this recent link",
            url,
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                }

                @Override
                public void onCancel() {
                }
            }
        );
        summaryLabel.setText("Clipboard unavailable. Recent link opened for manual copy.");
    }

    private void rebuildRecentHistoryTable() {
        recentHistoryTable.clearChildren();
        recentHistoryTable.defaults().left().pad(4);
        if (recentHistory.isEmpty()) {
            recentHistoryTable.add(new Label("No recent shared or imported rulesets yet.", engine.uiSkin)).left().row();
            return;
        }
        for (int i = 0; i < recentHistory.size(); i++) {
            final int historyIndex = i;
            RecentGameHistoryEntry entry = recentHistory.get(i);
            String when = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(entry.timestamp));
            String title = entry.gameName + " • " + entry.source + " • " + entry.pieceCount + " pieces • " + entry.rotationCount + " rotations • " + when;
            Label label = new Label(title, engine.uiSkin);
            TextButton loadBtn = new TextButton("Load", engine.uiSkin);
            TextButton copyBtn = new TextButton("Copy Link", engine.uiSkin);
            loadBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    loadRecentHistoryEntry(historyIndex);
                }
            });
            copyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    copyRecentHistoryEntry(historyIndex);
                }
            });
            recentHistoryTable.add(label).left();
            recentHistoryTable.add(loadBtn).left();
            recentHistoryTable.add(copyBtn).left().row();
        }
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

    private void normalizeCurrentRotation() {
        Piece.Rotation rotation = getSelectedRotation();
        if (rotation == null || rotation.blockOffsets.isEmpty()) return;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            minX = Math.min(minX, offset.x);
            minY = Math.min(minY, offset.y);
        }
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            offset.x -= minX;
            offset.y -= minY;
        }
        refreshEditorState();
    }

    private void centerCurrentRotation() {
        Piece.Rotation rotation = getSelectedRotation();
        if (rotation == null || rotation.blockOffsets.isEmpty()) return;
        centerRotationInPlace(rotation);
        refreshEditorState();
    }

    private void centerAllRotations() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            Piece.Rotation rotation = pieceType.rotationSet.get(i);
            if (rotation == null || rotation.blockOffsets.isEmpty()) continue;
            centerRotationInPlace(rotation);
        }
        refreshEditorState();
    }

    private void normalizeAllRotations() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            Piece.Rotation rotation = pieceType.rotationSet.get(i);
            if (rotation == null || rotation.blockOffsets.isEmpty()) continue;
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            for (Piece.BlockOffset offset : rotation.blockOffsets) {
                minX = Math.min(minX, offset.x);
                minY = Math.min(minY, offset.y);
            }
            for (Piece.BlockOffset offset : rotation.blockOffsets) {
                offset.x -= minX;
                offset.y -= minY;
            }
        }
        refreshEditorState();
    }

    private void centerRotationInPlace(Piece.Rotation rotation) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            minX = Math.min(minX, offset.x);
            maxX = Math.max(maxX, offset.x);
            minY = Math.min(minY, offset.y);
            maxY = Math.max(maxY, offset.y);
        }
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int targetMinX = Math.max(0, (4 - width) / 2);
        int targetMinY = Math.max(0, (4 - height) / 2);
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            offset.x = offset.x - minX + targetMinX;
            offset.y = offset.y - minY + targetMinY;
        }
    }

    private void removeDuplicateRotations() {
        final PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        final java.util.ArrayList<Integer> duplicateIndices = new java.util.ArrayList<Integer>(getDuplicateRotationIndices(pieceType));
        if (duplicateIndices.isEmpty()) {
            summaryLabel.setText("No duplicate rotations to remove.");
            return;
        }
        duplicateIndices.sort(java.util.Collections.reverseOrder());
        Engine.GUIManager().showYesNoDialog(
            "Remove " + duplicateIndices.size() + " duplicate rotation(s) from '" + pieceType.name + "'?",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    for (Integer index : duplicateIndices) {
                        pieceType.rotationSet.remove(index);
                        if (selectedRotationIndex >= index && selectedRotationIndex > 0) {
                            selectedRotationIndex--;
                        }
                    }
                    if (pieceType.rotationSet.size() == 0) {
                        selectedRotationIndex = 0;
                    } else {
                        selectedRotationIndex = Math.min(selectedRotationIndex, pieceType.rotationSet.size() - 1);
                    }
                    refreshEditorState();
                }

                @Override
                public void onNo() {
                }
            }
        );
    }

    private void removeEmptyRotations() {
        final PieceType pieceType = getSelectedPiece();
        if (pieceType == null || pieceType.rotationSet == null || pieceType.rotationSet.size() == 0) return;
        final java.util.ArrayList<Integer> emptyIndices = new java.util.ArrayList<Integer>();
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            Piece.Rotation rotation = pieceType.rotationSet.get(i);
            if (rotation == null || rotation.blockOffsets.isEmpty()) emptyIndices.add(i);
        }
        if (emptyIndices.isEmpty()) {
            summaryLabel.setText("No empty rotations to remove.");
            return;
        }
        emptyIndices.sort(java.util.Collections.reverseOrder());
        Engine.GUIManager().showYesNoDialog(
            "Remove " + emptyIndices.size() + " empty rotation(s) from '" + pieceType.name + "'?",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    for (Integer index : emptyIndices) {
                        pieceType.rotationSet.remove(index);
                        if (selectedRotationIndex >= index && selectedRotationIndex > 0) {
                            selectedRotationIndex--;
                        }
                    }
                    if (pieceType.rotationSet.size() == 0) {
                        selectedRotationIndex = 0;
                    } else {
                        selectedRotationIndex = Math.min(selectedRotationIndex, pieceType.rotationSet.size() - 1);
                    }
                    refreshEditorState();
                }

                @Override
                public void onNo() {
                }
            }
        );
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

        java.util.HashSet<Integer> duplicateIndices = getDuplicateRotationIndices(pieceType);
        rotationOverviewTable.defaults().pad(4);
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            final int rotationIndex = i;
            final Piece.Rotation rotation = pieceType.rotationSet.get(i);
            String prefix = (rotationIndex == selectedRotationIndex) ? "> " : "";
            String duplicateLabel = duplicateIndices.contains(rotationIndex) ? ", dup" : "";
            TextButton button = new TextButton(prefix + "R" + rotationIndex + " (" + getFilledCellCount(rotation) + ", " + getRotationBoundingBox(rotation) + duplicateLabel + ")", engine.uiSkin);
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

    private String getRotationSignature(Piece.Rotation rotation) {
        if (rotation == null) return "empty";
        java.util.ArrayList<String> coords = new java.util.ArrayList<String>();
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            coords.add(offset.x + "," + offset.y);
        }
        java.util.Collections.sort(coords);
        return String.join("|", coords);
    }

    private String getRotationBoundingBox(Piece.Rotation rotation) {
        if (rotation == null || rotation.blockOffsets.isEmpty()) return "0x0";
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            minX = Math.min(minX, offset.x);
            maxX = Math.max(maxX, offset.x);
            minY = Math.min(minY, offset.y);
            maxY = Math.max(maxY, offset.y);
        }
        return (maxX - minX + 1) + "x" + (maxY - minY + 1);
    }

    private String getRotationSymmetry(Piece.Rotation rotation) {
        if (rotation == null || rotation.blockOffsets.isEmpty()) return "none";
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        java.util.HashSet<String> occupied = new java.util.HashSet<String>();
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            minX = Math.min(minX, offset.x);
            maxX = Math.max(maxX, offset.x);
            minY = Math.min(minY, offset.y);
            maxY = Math.max(maxY, offset.y);
            occupied.add(offset.x + "," + offset.y);
        }

        boolean horizontal = true;
        boolean vertical = true;
        for (Piece.BlockOffset offset : rotation.blockOffsets) {
            int mirrorX = maxX - (offset.x - minX);
            int mirrorY = maxY - (offset.y - minY);
            if (!occupied.contains(mirrorX + "," + offset.y)) horizontal = false;
            if (!occupied.contains(offset.x + "," + mirrorY)) vertical = false;
        }

        if (horizontal && vertical) return "horizontal + vertical";
        if (horizontal) return "horizontal";
        if (vertical) return "vertical";
        return "none";
    }

    private java.util.HashSet<Integer> getDuplicateRotationIndices(PieceType pieceType) {
        java.util.HashSet<Integer> duplicates = new java.util.HashSet<Integer>();
        if (pieceType == null || pieceType.rotationSet == null) return duplicates;
        java.util.HashMap<String, Integer> firstSeen = new java.util.HashMap<String, Integer>();
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            String signature = getRotationSignature(pieceType.rotationSet.get(i));
            if (firstSeen.containsKey(signature)) {
                duplicates.add(i);
            } else {
                firstSeen.put(signature, i);
            }
        }
        return duplicates;
    }

    private int getUniqueRotationCount(PieceType pieceType) {
        if (pieceType == null || pieceType.rotationSet == null) return 0;
        java.util.HashSet<String> signatures = new java.util.HashSet<String>();
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            signatures.add(getRotationSignature(pieceType.rotationSet.get(i)));
        }
        return signatures.size();
    }

    private void applyRuleCheckboxes() {
        currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = cascadeGravityCheckbox.isChecked();
        currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = disconnectedGravityCheckbox.isChecked();
        currentGameType.chainRule_CheckRow = chainRowCheckbox.isChecked();
        currentGameType.chainRule_CheckColumn = chainColumnCheckbox.isChecked();
        currentGameType.chainRule_CheckDiagonal = chainDiagonalCheckbox.isChecked();
        currentGameType.chainRule_CheckRecursiveConnections = recursiveChainCheckbox.isChecked();
        currentGameType.nextPieceEnabled = nextPieceEnabledCheckbox.isChecked();
        currentGameType.holdPieceEnabled = holdPieceEnabledCheckbox.isChecked();
        currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = bagRandomizerCheckbox.isChecked();
        currentGameType.hardDropPunchThroughToLowestValidGridPosition = hardDropPunchCheckbox.isChecked();
        currentGameType.twoSpaceWallKickAllowed = twoSpaceWallKickCheckbox.isChecked();
        currentGameType.diagonalWallKickAllowed = diagonalWallKickCheckbox.isChecked();
        currentGameType.pieceClimbingAllowed = pieceClimbingCheckbox.isChecked();
        currentGameType.flip180Allowed = flip180Checkbox.isChecked();
        currentGameType.floorKickAllowed = floorKickCheckbox.isChecked();
    }

    private String getEnabledRuleSummary() {
        java.util.ArrayList<String> enabled = new java.util.ArrayList<String>();
        if (currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) enabled.add("cascade gravity");
        if (currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks) enabled.add("disconnected gravity");
        if (currentGameType.chainRule_CheckRow) enabled.add("row chains");
        if (currentGameType.chainRule_CheckColumn) enabled.add("column chains");
        if (currentGameType.chainRule_CheckDiagonal) enabled.add("diagonal chains");
        if (currentGameType.chainRule_CheckRecursiveConnections) enabled.add("recursive search");
        if (currentGameType.nextPieceEnabled) enabled.add("next preview");
        if (currentGameType.holdPieceEnabled) enabled.add("hold piece");
        if (currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty) enabled.add("bag randomizer");
        if (currentGameType.hardDropPunchThroughToLowestValidGridPosition) enabled.add("hard-drop punch");
        if (currentGameType.twoSpaceWallKickAllowed) enabled.add("two-space kick");
        if (currentGameType.diagonalWallKickAllowed) enabled.add("diagonal kick");
        if (currentGameType.pieceClimbingAllowed) enabled.add("piece climbing");
        if (currentGameType.flip180Allowed) enabled.add("180 flip");
        if (currentGameType.floorKickAllowed) enabled.add("floor kick");
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
        PieceType pieceType = getSelectedPiece();
        Piece.Rotation rotation = getSelectedRotation();

        cascadeGravityCheckbox.setChecked(currentGameType.moveDownAllLinesOverBlankSpacesAtOnce);
        disconnectedGravityCheckbox.setChecked(currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks);
        chainRowCheckbox.setChecked(currentGameType.chainRule_CheckRow);
        chainColumnCheckbox.setChecked(currentGameType.chainRule_CheckColumn);
        chainDiagonalCheckbox.setChecked(currentGameType.chainRule_CheckDiagonal);
        recursiveChainCheckbox.setChecked(currentGameType.chainRule_CheckRecursiveConnections);
        nextPieceEnabledCheckbox.setChecked(currentGameType.nextPieceEnabled);
        holdPieceEnabledCheckbox.setChecked(currentGameType.holdPieceEnabled);
        bagRandomizerCheckbox.setChecked(currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty);
        hardDropPunchCheckbox.setChecked(currentGameType.hardDropPunchThroughToLowestValidGridPosition);
        twoSpaceWallKickCheckbox.setChecked(currentGameType.twoSpaceWallKickAllowed);
        diagonalWallKickCheckbox.setChecked(currentGameType.diagonalWallKickAllowed);
        pieceClimbingCheckbox.setChecked(currentGameType.pieceClimbingAllowed);
        flip180Checkbox.setChecked(currentGameType.flip180Allowed);
        floorKickCheckbox.setChecked(currentGameType.floorKickAllowed);

        pieceLabel.setText(pieceType == null ? "Piece: none" : "Piece: " + pieceType.name + " (" + (selectedPieceIndex + 1) + "/" + currentGameType.pieceTypes.size() + ")");
        rotationLabel.setText(rotation == null ? "Rotation: none" : "Rotation: " + selectedRotationIndex + " (" + getFilledCellCount(rotation) + " blocks)");
        rebuildRotationOverview(pieceType);
        rebuildRecentHistoryTable();

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
        int uniqueRotationCount = getUniqueRotationCount(pieceType);
        int duplicateRotationCount = Math.max(0, currentRotationCount - uniqueRotationCount);
        String symmetry = getRotationSymmetry(rotation);
        summaryLabel.setText(
            "Mode: " + currentGameType.gameMode
            + " | Grid: " + currentGameType.gridWidth + "x" + currentGameType.gridHeight
            + " | Pieces: " + currentGameType.pieceTypes.size()
            + " | Rotations: " + getTotalRotationCount()
            + " | Current piece rotations: " + currentRotationCount
            + " | Filled cells in current rotation: " + getFilledCellCount(rotation)
            + " | Current bbox: " + getRotationBoundingBox(rotation)
            + " | Symmetry: " + symmetry
            + " | Unique/duplicate rotations: " + uniqueRotationCount + "/" + duplicateRotationCount
            + " | Rules: " + getEnabledRuleSummary()
        );
    }
}
