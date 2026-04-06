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
import com.bobsgame.puzzle.BlockType;
import com.bobsgame.puzzle.GameType;
import com.bobsgame.puzzle.Piece;
import com.bobsgame.puzzle.PieceType;
import com.bobsgame.shared.BobColor;

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
    private final TextButton presetSprintBtn;
    private final TextButton presetCascadeBtn;
    private final TextButton presetZenBtn;
    private final TextButton presetStackBtn;
    private final TextButton presetMicroBtn;
    private final TextButton addBlockBtn;
    private final TextButton removeBlockBtn;
    private final TextButton prevBlockBtn;
    private final TextButton nextBlockBtn;
    private final TextButton renameBlockBtn;
    private final TextButton recolorBlockBtn;
    private final TextButton addBlockColorBtn;
    private final TextButton removeBlockColorBtn;
    private final TextButton recolorSpecialBlockBtn;
    private final TextButton blockSpecialChanceBtn;
    private final TextButton blockSpecialFrequencyBtn;
    private final TextButton assignBlockToPieceBtn;
    private final TextButton clearPieceBlockBtn;
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
    private final Label blockLabel;
    private final Label blockDetailsLabel;
    private final Label pieceLabel;
    private final Label rotationLabel;
    private final Label summaryLabel;
    private final Label hintLabel;
    private final Label rotationOverviewLabel;
    private final Label recentHistoryLabel;
    private final Label recentActionsLabel;
    private final Label presetSlotsLabel;
    private final Label templateCatalogLabel;
    private final Table rotationOverviewTable;
    private final Table recentHistoryTable;
    private final Table recentActionsTable;
    private final Table presetSlotsTable;
    private final Table templateCatalogTable;
    private final CheckBox blockUseNormalCheckbox;
    private final CheckBox blockUseGarbageCheckbox;
    private final CheckBox blockUseFillerCheckbox;
    private final CheckBox blockFlashingCheckbox;
    private final CheckBox blockMatchAnyColorCheckbox;
    private final CheckBox blockCounterCheckbox;
    private final CheckBox blockClearEveryOtherLineCheckbox;
    private final CheckBox blockIgnoreChainConnectionsCheckbox;
    private final CheckBox blockIgnoreMovingDownCheckbox;
    private final CheckBox blockRequireChainPresenceCheckbox;
    private final CheckBox blockAddToExplodingChainCheckbox;
    private final CheckBox blockRemoveColorFieldCheckbox;
    private final CheckBox blockDiamondColorFieldCheckbox;
    private final TextButton assignBlockRewardBtn;
    private final TextButton clearBlockRewardBtn;
    private final TextButton addBlockConversionBtn;
    private final TextButton clearBlockConversionsBtn;
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
    private final java.util.ArrayList<RecentEditorActionEntry> recentActions = new java.util.ArrayList<RecentEditorActionEntry>();
    private int selectedBlockIndex = -1;
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

    private static class RecentEditorActionEntry {
        public String label;
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
        blockLabel = new Label("Block: none", skin);
        blockDetailsLabel = new Label("No custom block data yet.", skin);
        pieceLabel = new Label("Piece: none", skin);
        rotationLabel = new Label("Rotation: none", skin);
        summaryLabel = new Label("No custom piece data yet.", skin);
        hintLabel = new Label("Build piece shapes with the 4x4 grid. Add pieces and rotations to sketch rules live.", skin);
        rotationOverviewLabel = new Label("Rotation Overview", skin);
        recentHistoryLabel = new Label("Recent Share / Import History", skin);
        recentActionsLabel = new Label("Recent Actions", skin);
        presetSlotsLabel = new Label("Saved Template Slots", skin);
        templateCatalogLabel = new Label("Template Browser", skin);
        rotationOverviewTable = new Table();
        recentHistoryTable = new Table();
        recentActionsTable = new Table();
        presetSlotsTable = new Table();
        templateCatalogTable = new Table();
        blockUseNormalCheckbox = new CheckBox(" Use in normal pieces", skin);
        blockUseGarbageCheckbox = new CheckBox(" Use as garbage", skin);
        blockUseFillerCheckbox = new CheckBox(" Use as filler", skin);
        blockFlashingCheckbox = new CheckBox(" Flashing special", skin);
        blockMatchAnyColorCheckbox = new CheckBox(" Match any color", skin);
        blockCounterCheckbox = new CheckBox(" Counter type", skin);
        blockClearEveryOtherLineCheckbox = new CheckBox(" Clear every other line", skin);
        blockIgnoreChainConnectionsCheckbox = new CheckBox(" Ignore chain connections", skin);
        blockIgnoreMovingDownCheckbox = new CheckBox(" Ignore moving down", skin);
        blockRequireChainPresenceCheckbox = new CheckBox(" Required in chain", skin);
        blockAddToExplodingChainCheckbox = new CheckBox(" Add to exploding chain", skin);
        blockRemoveColorFieldCheckbox = new CheckBox(" Remove same-color field blocks", skin);
        blockDiamondColorFieldCheckbox = new CheckBox(" Diamond-color field swap", skin);
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
        blockDetailsLabel.setWrap(true);
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
        presetSprintBtn = new TextButton("Sprint Drop", skin);
        presetCascadeBtn = new TextButton("Cascade Puzzle", skin);
        presetZenBtn = new TextButton("Zen Garden", skin);
        presetStackBtn = new TextButton("Stack Arcade", skin);
        presetMicroBtn = new TextButton("Micro Stack", skin);
        addBlockBtn = new TextButton("Add Block", skin);
        removeBlockBtn = new TextButton("Remove Block", skin);
        prevBlockBtn = new TextButton("< Block", skin);
        nextBlockBtn = new TextButton("Block >", skin);
        renameBlockBtn = new TextButton("Rename Block", skin);
        recolorBlockBtn = new TextButton("Set Block Color", skin);
        addBlockColorBtn = new TextButton("Add Palette Color", skin);
        removeBlockColorBtn = new TextButton("Remove Palette Color", skin);
        recolorSpecialBlockBtn = new TextButton("Set Special Color", skin);
        blockSpecialChanceBtn = new TextButton("Special Chance", skin);
        blockSpecialFrequencyBtn = new TextButton("Special Frequency", skin);
        assignBlockToPieceBtn = new TextButton("Assign Block To Piece", skin);
        clearPieceBlockBtn = new TextButton("Clear Piece Block", skin);
        assignBlockRewardBtn = new TextButton("Use Selected Piece as Reward", skin);
        clearBlockRewardBtn = new TextButton("Clear Reward", skin);
        addBlockConversionBtn = new TextButton("Add Conversion Pair", skin);
        clearBlockConversionsBtn = new TextButton("Clear Conversions", skin);
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

        Label presetCompetitiveLabel = new Label("Preset Family — Competitive Drop", skin);
        Table presetCompetitiveRow = new Table();
        presetCompetitiveRow.defaults().pad(4);
        presetCompetitiveRow.add(presetClassicBtn);
        presetCompetitiveRow.add(presetSprintBtn);

        Label presetPuzzleLabel = new Label("Preset Family — Puzzle Chainers", skin);
        Table presetPuzzleRow = new Table();
        presetPuzzleRow.defaults().pad(4);
        presetPuzzleRow.add(presetCascadeBtn);
        presetPuzzleRow.add(presetZenBtn);

        Label presetArcadeLabel = new Label("Preset Family — Arcade Stackers", skin);
        Table presetArcadeRow = new Table();
        presetArcadeRow.defaults().pad(4);
        presetArcadeRow.add(presetStackBtn);
        presetArcadeRow.add(presetMicroBtn);

        Table templateCatalogRow = new Table();
        templateCatalogRow.defaults().left().pad(4);
        templateCatalogRow.add(templateCatalogTable).left();

        Table presetStatusRow = new Table();
        presetStatusRow.defaults().left().pad(4);
        presetStatusRow.add(presetSlotsTable).left();

        Table blockControlsRow1 = new Table();
        blockControlsRow1.defaults().pad(4);
        blockControlsRow1.add(addBlockBtn);
        blockControlsRow1.add(removeBlockBtn);
        blockControlsRow1.add(prevBlockBtn);
        blockControlsRow1.add(nextBlockBtn);
        blockControlsRow1.add(renameBlockBtn);
        blockControlsRow1.add(recolorBlockBtn);
        blockControlsRow1.add(addBlockColorBtn);
        blockControlsRow1.add(removeBlockColorBtn);
        blockControlsRow1.add(recolorSpecialBlockBtn);
        blockControlsRow1.add(blockSpecialChanceBtn);
        blockControlsRow1.add(blockSpecialFrequencyBtn);

        Table blockControlsRow2 = new Table();
        blockControlsRow2.defaults().pad(4);
        blockControlsRow2.add(assignBlockToPieceBtn);
        blockControlsRow2.add(clearPieceBlockBtn);
        blockControlsRow2.add(blockUseNormalCheckbox);
        blockControlsRow2.add(blockUseGarbageCheckbox);
        blockControlsRow2.add(blockUseFillerCheckbox);
        blockControlsRow2.add(blockFlashingCheckbox);
        blockControlsRow2.add(blockMatchAnyColorCheckbox);
        blockControlsRow2.add(blockCounterCheckbox);
        blockControlsRow2.add(blockClearEveryOtherLineCheckbox);
        blockControlsRow2.add(blockIgnoreChainConnectionsCheckbox);
        blockControlsRow2.add(blockIgnoreMovingDownCheckbox);
        blockControlsRow2.add(blockRequireChainPresenceCheckbox);
        blockControlsRow2.add(blockAddToExplodingChainCheckbox);
        blockControlsRow2.add(blockRemoveColorFieldCheckbox);
        blockControlsRow2.add(blockDiamondColorFieldCheckbox);

        Table blockRewardRow = new Table();
        blockRewardRow.defaults().pad(4);
        blockRewardRow.add(assignBlockRewardBtn);
        blockRewardRow.add(clearBlockRewardBtn);
        blockRewardRow.add(addBlockConversionBtn);
        blockRewardRow.add(clearBlockConversionsBtn);

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
        presetSprintBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("sprint");
            }
        });
        presetCascadeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("cascade");
            }
        });
        presetZenBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("zen");
            }
        });
        presetStackBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("stack");
            }
        });
        presetMicroBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applyPreset("micro");
            }
        });
        addBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addBlock();
            }
        });
        removeBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeBlock();
            }
        });
        prevBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleBlock(-1);
            }
        });
        nextBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleBlock(1);
            }
        });
        renameBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                renameCurrentBlock();
            }
        });
        recolorBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                recolorCurrentBlock();
            }
        });
        addBlockColorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addPaletteColorToCurrentBlock();
            }
        });
        removeBlockColorBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removePaletteColorFromCurrentBlock();
            }
        });
        recolorSpecialBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                recolorCurrentSpecialBlock();
            }
        });
        blockSpecialChanceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editCurrentBlockSpecialChance();
            }
        });
        blockSpecialFrequencyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                editCurrentBlockSpecialFrequency();
            }
        });
        assignBlockToPieceBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assignSelectedBlockToPiece();
            }
        });
        clearPieceBlockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearSelectedPieceBlockOverride();
            }
        });
        blockUseNormalCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockUseGarbageCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockUseFillerCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockFlashingCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockMatchAnyColorCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockCounterCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockClearEveryOtherLineCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockIgnoreChainConnectionsCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockIgnoreMovingDownCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockRequireChainPresenceCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockAddToExplodingChainCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockRemoveColorFieldCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        blockDiamondColorFieldCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                applySelectedBlockUsage();
            }
        });
        assignBlockRewardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assignSelectedPieceAsBlockReward();
            }
        });
        clearBlockRewardBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearSelectedBlockReward();
            }
        });
        addBlockConversionBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addConversionPairToSelectedBlock();
            }
        });
        clearBlockConversionsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clearConversionPairsFromSelectedBlock();
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
        mainTable.add(presetCompetitiveLabel).left().row();
        mainTable.add(presetCompetitiveRow).left().row();
        mainTable.add(presetPuzzleLabel).left().row();
        mainTable.add(presetPuzzleRow).left().row();
        mainTable.add(presetArcadeLabel).left().row();
        mainTable.add(presetArcadeRow).left().row();
        mainTable.add(templateCatalogLabel).left().row();
        mainTable.add(templateCatalogRow).left().row();
        mainTable.add(presetSlotsLabel).left().row();
        mainTable.add(presetStatusRow).left().row();
        mainTable.add(blockControlsRow1).left().row();
        mainTable.add(blockControlsRow2).left().row();
        mainTable.add(blockRewardRow).left().row();
        mainTable.add(blockLabel).left().row();
        mainTable.add(blockDetailsLabel).width(520).left().row();
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
        mainTable.add(recentActionsLabel).left().padTop(8).row();
        mainTable.add(recentActionsTable).left().row();
        mainTable.add(summaryLabel).width(520).left().padTop(10).row();

        addPiece();
        if (currentGameType.blockTypes.isEmpty()) addBlock();
        refreshEditorState();
    }

    private BlockType getSelectedBlock() {
        if (selectedBlockIndex < 0 || selectedBlockIndex >= currentGameType.blockTypes.size()) return null;
        return currentGameType.blockTypes.get(selectedBlockIndex);
    }

    private void addBlock() {
        BlockType blockType = new BlockType();
        blockType.name = "Block " + (currentGameType.blockTypes.size() + 1);
        blockType.colors.add(new BobColor(128, 128, 128));
        blockType.useInNormalPieces = true;
        currentGameType.blockTypes.add(blockType);
        selectedBlockIndex = currentGameType.blockTypes.size() - 1;
        refreshEditorState();
        pushRecentAction("Added block: " + blockType.name);
    }

    private void removeBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        final String blockName = blockType.name == null || blockType.name.isEmpty() ? "Unnamed Block" : blockType.name;
        Engine.GUIManager().showYesNoDialog(
            "Remove block '" + blockName + "'? Any piece overrides using it will be cleared.",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    currentGameType.blockTypes.remove(blockType);
                    for (PieceType pieceType : currentGameType.pieceTypes) {
                        pieceType.overrideBlockTypes_UUID.remove(blockType.uuid);
                    }
                    if (currentGameType.blockTypes.isEmpty()) {
                        selectedBlockIndex = -1;
                    } else {
                        selectedBlockIndex = Math.min(selectedBlockIndex, currentGameType.blockTypes.size() - 1);
                    }
                    refreshEditorState();
                    pushRecentAction("Removed block: " + blockName);
                }

                @Override
                public void onNo() {
                }
            }
        );
    }

    private void cycleBlock(int delta) {
        if (currentGameType.blockTypes.isEmpty()) return;
        if (selectedBlockIndex < 0) selectedBlockIndex = 0;
        selectedBlockIndex = (selectedBlockIndex + delta + currentGameType.blockTypes.size()) % currentGameType.blockTypes.size();
        refreshEditorState();
    }

    private void renameCurrentBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        Engine.GUIManager().showStringDialog(
            "Rename block",
            blockType.name == null ? "" : blockType.name,
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                    blockType.name = text == null ? "" : text.trim();
                    refreshEditorState();
                    pushRecentAction("Renamed block to " + (blockType.name == null || blockType.name.isEmpty() ? "Unnamed Block" : blockType.name) + ".");
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void recolorCurrentBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        final BobColor currentColor = blockType.colors.isEmpty() ? new BobColor(128, 128, 128) : blockType.colors.get(0);
        Engine.GUIManager().showStringDialog(
            "Set block color as hex (#RRGGBB)",
            bobColorToHex(currentColor),
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                    BobColor updated = parseHexColor(text, currentColor);
                    if (blockType.colors.isEmpty()) blockType.colors.add(updated); else blockType.colors.set(0, updated);
                    refreshEditorState();
                    pushRecentAction("Updated block color for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void recolorCurrentSpecialBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        final BobColor currentColor = blockType.specialColor == null ? (blockType.colors.isEmpty() ? new BobColor(255, 0, 255) : blockType.colors.get(0)) : blockType.specialColor;
        Engine.GUIManager().showStringDialog(
            "Set special color as hex (#RRGGBB)",
            bobColorToHex(currentColor),
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(String text) {
                    blockType.specialColor = parseHexColor(text, currentColor);
                    refreshEditorState();
                    pushRecentAction("Updated special block color for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void addPaletteColorToCurrentBlock() {
        BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        BobColor sourceColor = blockType.colors.isEmpty() ? new BobColor(128, 128, 128) : blockType.colors.get(0);
        blockType.colors.add(sourceColor.clone());
        refreshEditorState();
        pushRecentAction("Added palette color to " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
    }

    private void removePaletteColorFromCurrentBlock() {
        BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        if (blockType.colors.size() <= 1) return;
        blockType.colors.remove(blockType.colors.size() - 1);
        refreshEditorState();
        pushRecentAction("Removed palette color from " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
    }

    private void editCurrentBlockSpecialChance() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        Engine.GUIManager().showNumberDialog(
            "Set special chance (1 in N, 0 disables)",
            blockType.randomSpecialBlockChanceOneOutOf,
            new com.bobsgame.client.engine.game.gui.Scene2DNumberDialog.NumberDialogListener() {
                @Override
                public void onResult(int value) {
                    blockType.randomSpecialBlockChanceOneOutOf = Math.max(0, value);
                    refreshEditorState();
                    pushRecentAction("Updated special block chance for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void editCurrentBlockSpecialFrequency() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        Engine.GUIManager().showNumberDialog(
            "Set special frequency (every N pieces, 0 disables)",
            blockType.frequencySpecialBlockTypeOnceEveryNPieces,
            new com.bobsgame.client.engine.game.gui.Scene2DNumberDialog.NumberDialogListener() {
                @Override
                public void onResult(int value) {
                    blockType.frequencySpecialBlockTypeOnceEveryNPieces = Math.max(0, value);
                    refreshEditorState();
                    pushRecentAction("Updated special block frequency for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private String bobColorToHex(BobColor color) {
        return String.format("#%02x%02x%02x", color.ri(), color.gi(), color.bi());
    }

    private BobColor parseHexColor(String text, BobColor fallback) {
        if (text == null) return fallback == null ? new BobColor(128, 128, 128) : fallback.clone();
        String normalized = text.trim().replace("#", "");
        if (!normalized.matches("[0-9a-fA-F]{6}")) return fallback == null ? new BobColor(128, 128, 128) : fallback.clone();
        int r = Integer.parseInt(normalized.substring(0, 2), 16);
        int g = Integer.parseInt(normalized.substring(2, 4), 16);
        int b = Integer.parseInt(normalized.substring(4, 6), 16);
        return new BobColor(r, g, b);
    }

    private void applySelectedBlockUsage() {
        BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        blockType.useInNormalPieces = blockUseNormalCheckbox.isChecked();
        blockType.useAsGarbage = blockUseGarbageCheckbox.isChecked();
        blockType.useAsGarbageBlock = blockUseGarbageCheckbox.isChecked();
        blockType.isGarbageBlockType = blockUseGarbageCheckbox.isChecked();
        blockType.useAsPlayingFieldFiller = blockUseFillerCheckbox.isChecked();
        blockType.useAsPlayingFieldFillerBlock = blockUseFillerCheckbox.isChecked();
        blockType.flashingSpecialType = blockFlashingCheckbox.isChecked();
        blockType.matchAnyColor = blockMatchAnyColorCheckbox.isChecked();
        blockType.counterType = blockCounterCheckbox.isChecked();
        blockType.clearEveryOtherLineOnGridWhenCleared = blockClearEveryOtherLineCheckbox.isChecked();
        blockType.ignoreWhenCheckingChainConnections = blockIgnoreChainConnectionsCheckbox.isChecked();
        blockType.ignoreWhenMovingDownBlocks = blockIgnoreMovingDownCheckbox.isChecked();
        blockType.chainConnectionsMustContainAtLeastOneBlockWithThisTrue = blockRequireChainPresenceCheckbox.isChecked();
        blockType.addToChainIfConnectedUpDownLeftRightToExplodingChainBlocks = blockAddToExplodingChainCheckbox.isChecked();
        blockType.removeAllBlocksOfColorOnFieldBlockIsSetOn = blockRemoveColorFieldCheckbox.isChecked();
        blockType.changeAllBlocksOfColorOnFieldBlockIsSetOnToDiamondColor = blockDiamondColorFieldCheckbox.isChecked();
        refreshEditorState();
        pushRecentAction("Updated block behavior flags for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
    }

    private void assignSelectedBlockToPiece() {
        BlockType blockType = getSelectedBlock();
        PieceType pieceType = getSelectedPiece();
        if (blockType == null || pieceType == null) return;
        pieceType.overrideBlockTypes_UUID.clear();
        pieceType.overrideBlockTypes_UUID.add(blockType.uuid);
        refreshEditorState();
        pushRecentAction("Assigned block " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + " to " + (pieceType.name == null || pieceType.name.isEmpty() ? "selected piece" : pieceType.name) + ".");
    }

    private void clearSelectedPieceBlockOverride() {
        PieceType pieceType = getSelectedPiece();
        if (pieceType == null) return;
        pieceType.overrideBlockTypes_UUID.clear();
        refreshEditorState();
        pushRecentAction("Cleared block override for " + (pieceType.name == null || pieceType.name.isEmpty() ? "selected piece" : pieceType.name) + ".");
    }

    private void assignSelectedPieceAsBlockReward() {
        BlockType blockType = getSelectedBlock();
        PieceType pieceType = getSelectedPiece();
        if (blockType == null || pieceType == null) return;
        blockType.makePieceTypeWhenCleared_UUID.clear();
        blockType.makePieceTypeWhenCleared_UUID.add(pieceType.uuid);
        refreshEditorState();
        pushRecentAction("Assigned clear reward piece " + (pieceType.name == null || pieceType.name.isEmpty() ? "selected piece" : pieceType.name) + " to " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
    }

    private void clearSelectedBlockReward() {
        BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        blockType.makePieceTypeWhenCleared_UUID.clear();
        refreshEditorState();
        pushRecentAction("Cleared reward piece for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
    }

    private void addConversionPairToSelectedBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        Engine.GUIManager().showStringDialog(
            "From block name",
            "",
            new Scene2DStringDialog.StringDialogListener() {
                @Override
                public void onResult(final String fromText) {
                    final BlockType fromBlock = currentGameType.getBlockTypeByName(fromText == null ? "" : fromText.trim());
                    if (fromBlock == null) {
                        pushRecentAction("Conversion pair cancelled: unknown source block.");
                        refreshEditorState();
                        return;
                    }
                    Engine.GUIManager().showStringDialog(
                        "To block name",
                        "",
                        new Scene2DStringDialog.StringDialogListener() {
                            @Override
                            public void onResult(String toText) {
                                BlockType toBlock = currentGameType.getBlockTypeByName(toText == null ? "" : toText.trim());
                                if (toBlock == null) {
                                    pushRecentAction("Conversion pair cancelled: unknown target block.");
                                    refreshEditorState();
                                    return;
                                }
                                BlockType.TurnFromBlockTypeToType pair = new BlockType.TurnFromBlockTypeToType();
                                pair.fromType_UUID = fromBlock.uuid;
                                pair.toType_UUID = toBlock.uuid;
                                blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut.add(pair);
                                refreshEditorState();
                                pushRecentAction("Added conversion pair " + fromBlock.name + " -> " + toBlock.name + " for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                            }

                            @Override
                            public void onCancel() {
                            }
                        }
                    );
                }

                @Override
                public void onCancel() {
                }
            }
        );
    }

    private void clearConversionPairsFromSelectedBlock() {
        final BlockType blockType = getSelectedBlock();
        if (blockType == null) return;
        if (blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut.isEmpty()) return;
        Engine.GUIManager().showYesNoDialog(
            "Clear all conversion pairs from '" + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + "'?",
            new Scene2DYesNoDialog.YesNoDialogListener() {
                @Override
                public void onYes() {
                    blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut.clear();
                    refreshEditorState();
                    pushRecentAction("Cleared conversion pairs for " + (blockType.name == null || blockType.name.isEmpty() ? "selected block" : blockType.name) + ".");
                }

                @Override
                public void onNo() {
                }
            }
        );
    }

    private void addPiece() {
        PieceType pieceType = new PieceType();
        pieceType.name = "Piece " + (currentGameType.pieceTypes.size() + 1);
        currentGameType.pieceTypes.add(pieceType);
        selectedPieceIndex = currentGameType.pieceTypes.size() - 1;
        selectedRotationIndex = 0;
        refreshEditorState();
        pushRecentAction("Added piece: " + pieceType.name);
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
        pushRecentAction("Added rotation " + selectedRotationIndex + " to " + (pieceType.name == null ? "selected piece" : pieceType.name) + ".");
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
        rebuildPresetSlotTable();
        pushRecentAction("Saved the current ruleset to preset slot " + (slotIndex + 1) + ".");
    }

    private void applyPreset(String preset) {
        applyRuleCheckboxes();
        if ("classic".equals(preset)) {
            currentGameType.name = "Classic Drop";
            currentGameType.gameMode = GameType.GameMode.DROP;
            currentGameType.gridWidth = 10;
            currentGameType.gridHeight = 20;
            currentGameType.numberOfNextPiecesToShow = 3;
            currentGameType.maxLockDelayTicks = 500;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 100;
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
        } else if ("sprint".equals(preset)) {
            currentGameType.name = "Sprint Drop";
            currentGameType.gameMode = GameType.GameMode.DROP;
            currentGameType.gridWidth = 10;
            currentGameType.gridHeight = 20;
            currentGameType.numberOfNextPiecesToShow = 5;
            currentGameType.maxLockDelayTicks = 240;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 40;
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
            currentGameType.numberOfNextPiecesToShow = 3;
            currentGameType.maxLockDelayTicks = 450;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 120;
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
        } else if ("zen".equals(preset)) {
            currentGameType.name = "Zen Garden";
            currentGameType.gameMode = GameType.GameMode.DROP;
            currentGameType.gridWidth = 10;
            currentGameType.gridHeight = 18;
            currentGameType.numberOfNextPiecesToShow = 5;
            currentGameType.maxLockDelayTicks = 900;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 220;
            currentGameType.chainRule_AmountPerChain = 4;
            currentGameType.moveDownAllLinesOverBlankSpacesAtOnce = true;
            currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks = false;
            currentGameType.chainRule_CheckRow = true;
            currentGameType.chainRule_CheckColumn = true;
            currentGameType.chainRule_CheckDiagonal = false;
            currentGameType.chainRule_CheckRecursiveConnections = true;
            currentGameType.nextPieceEnabled = true;
            currentGameType.holdPieceEnabled = true;
            currentGameType.currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = true;
            currentGameType.hardDropPunchThroughToLowestValidGridPosition = false;
            currentGameType.twoSpaceWallKickAllowed = false;
            currentGameType.diagonalWallKickAllowed = false;
            currentGameType.pieceClimbingAllowed = false;
            currentGameType.flip180Allowed = true;
            currentGameType.floorKickAllowed = false;
        } else if ("stack".equals(preset)) {
            currentGameType.name = "Stack Arcade";
            currentGameType.gameMode = GameType.GameMode.STACK;
            currentGameType.gridWidth = 6;
            currentGameType.gridHeight = 12;
            currentGameType.numberOfNextPiecesToShow = 3;
            currentGameType.maxLockDelayTicks = 350;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 90;
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
        } else if ("micro".equals(preset)) {
            currentGameType.name = "Micro Stack";
            currentGameType.gameMode = GameType.GameMode.STACK;
            currentGameType.gridWidth = 5;
            currentGameType.gridHeight = 10;
            currentGameType.numberOfNextPiecesToShow = 2;
            currentGameType.maxLockDelayTicks = 220;
            currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 70;
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
        pushRecentAction("Applied preset: " + currentGameType.name);
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
        rebuildPresetSlotTable();
        pushRecentAction("Loaded preset slot " + (slotIndex + 1) + ".");
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
            pushRecentAction("Imported shared ruleset: " + (currentGameType.name == null || currentGameType.name.isEmpty() ? "Imported Ruleset" : currentGameType.name) + ".");
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
        pushRecentAction("Generated a share link for " + (currentGameType.name == null || currentGameType.name.isEmpty() ? "current ruleset" : currentGameType.name) + ".");
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

    private void pushRecentAction(String label) {
        RecentEditorActionEntry entry = new RecentEditorActionEntry();
        entry.label = label;
        entry.timestamp = System.currentTimeMillis();
        recentActions.add(0, entry);
        while (recentActions.size() > 8) recentActions.remove(recentActions.size() - 1);
        rebuildRecentActionsTable();
    }

    private static class PresetCatalogEntry {
        public final String key;
        public final String family;
        public final String title;
        public final String description;
        public final String mode;
        public final String grid;
        public final String gravityLock;
        public final String preview;
        public final String chain;

        private PresetCatalogEntry(String key, String family, String title, String description, String mode, String grid, String gravityLock, String preview, String chain) {
            this.key = key;
            this.family = family;
            this.title = title;
            this.description = description;
            this.mode = mode;
            this.grid = grid;
            this.gravityLock = gravityLock;
            this.preview = preview;
            this.chain = chain;
        }
    }

    private PresetCatalogEntry[] getPresetCatalogEntries() {
        return new PresetCatalogEntry[] {
            new PresetCatalogEntry("classic", "Competitive Drop", "Classic Drop", "Balanced modern drop rules with hold and bag randomizer enabled.", "DROP", "10x20", "100 / 500", "3 next • hold on", "4-chain • row focus"),
            new PresetCatalogEntry("sprint", "Competitive Drop", "Sprint Drop", "Fast preview-heavy drop tuning for speed clears and quick retries.", "DROP", "10x20", "40 / 240", "5 next • hold on", "4-chain • row focus"),
            new PresetCatalogEntry("cascade", "Puzzle Chainers", "Cascade Puzzle", "Compact chain-oriented board with recursive cascade checks enabled.", "DROP", "8x16", "120 / 450", "3 next • hold off", "3-chain • row/column/diag"),
            new PresetCatalogEntry("zen", "Puzzle Chainers", "Zen Garden", "Slower forgiving chain sandbox for calm experimentation and pattern setup.", "DROP", "10x18", "220 / 900", "5 next • hold on", "4-chain • recursive"),
            new PresetCatalogEntry("stack", "Arcade Stackers", "Stack Arcade", "Compact stack rules tuned for quick arcade rounds and pressure play.", "STACK", "6x12", "90 / 350", "3 next • hold off", "3-chain • row/column"),
            new PresetCatalogEntry("micro", "Arcade Stackers", "Micro Stack", "Tiny-grid stack challenge for dense short-form sessions.", "STACK", "5x10", "70 / 220", "2 next • hold off", "3-chain • row/column")
        };
    }

    private void rebuildTemplateCatalogTable() {
        templateCatalogTable.clearChildren();
        templateCatalogTable.defaults().left().pad(3);
        for (PresetCatalogEntry entry : getPresetCatalogEntries()) {
            Label meta = new Label(entry.family + " — " + entry.title + "\n" + entry.description + "\n" + entry.mode + " • Grid " + entry.grid + " • Gravity/Lock " + entry.gravityLock + "\n" + entry.preview + " • " + entry.chain, engine.uiSkin);
            meta.setWrap(true);
            TextButton applyBtn = new TextButton("Apply", engine.uiSkin);
            final String presetKey = entry.key;
            applyBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    applyPreset(presetKey);
                }
            });
            templateCatalogTable.add(meta).width(360).left();
            templateCatalogTable.add(applyBtn).left().row();
        }
    }

    private void rebuildPresetSlotTable() {
        presetSlotsTable.clearChildren();
        presetSlotsTable.defaults().left().pad(2);
        for (int i = 0; i < presetSlots.length; i++) {
            GameType preset = presetSlots[i];
            String label = preset == null
                ? "Slot " + (i + 1) + ": Empty"
                : "Slot " + (i + 1) + ": " + (preset.name == null || preset.name.isEmpty() ? "Unnamed Ruleset" : preset.name)
                    + " • " + preset.gameMode
                    + " • " + preset.pieceTypes.size() + " pieces"
                    + " • " + getTotalRotationCount(preset) + " rotations";
            presetSlotsTable.add(new Label(label, engine.uiSkin)).left().row();
        }
    }

    private void rebuildRecentActionsTable() {
        recentActionsTable.clearChildren();
        recentActionsTable.defaults().left().pad(4);
        if (recentActions.isEmpty()) {
            recentActionsTable.add(new Label("No recent editor actions yet.", engine.uiSkin)).left().row();
            return;
        }
        for (int i = 0; i < recentActions.size(); i++) {
            RecentEditorActionEntry entry = recentActions.get(i);
            String when = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(entry.timestamp));
            recentActionsTable.add(new Label(entry.label + " • " + when, engine.uiSkin)).left().row();
        }
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
        pushRecentAction("Duplicated piece: " + duplicate.name);
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
                    pushRecentAction("Removed piece: " + pieceName);
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
        pushRecentAction("Duplicated rotation for " + (pieceType.name == null ? "selected piece" : pieceType.name) + ".");
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
        pushRecentAction("Normalized the current rotation.");
    }

    private void centerCurrentRotation() {
        Piece.Rotation rotation = getSelectedRotation();
        if (rotation == null || rotation.blockOffsets.isEmpty()) return;
        centerRotationInPlace(rotation);
        refreshEditorState();
        pushRecentAction("Centered the current rotation.");
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
        pushRecentAction("Centered all rotations for " + (pieceType.name == null ? "selected piece" : pieceType.name) + ".");
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
        pushRecentAction("Normalized all rotations for " + (pieceType.name == null ? "selected piece" : pieceType.name) + ".");
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
                    pushRecentAction("Cleared " + duplicateIndices.size() + " duplicate rotation(s).");
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
                    pushRecentAction("Cleared " + emptyIndices.size() + " empty rotation(s).");
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
                    pushRecentAction("Removed a rotation from " + (pieceType.name == null ? "selected piece" : pieceType.name) + ".");
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
        pushRecentAction("Cleared the current rotation.");
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
        if (currentGameType.blockTypes.isEmpty()) {
            selectedBlockIndex = -1;
        } else if (selectedBlockIndex < 0 || selectedBlockIndex >= currentGameType.blockTypes.size()) {
            selectedBlockIndex = 0;
        }

        BlockType blockType = getSelectedBlock();
        PieceType pieceType = getSelectedPiece();
        Piece.Rotation rotation = getSelectedRotation();

        blockUseNormalCheckbox.setChecked(blockType != null && blockType.useInNormalPieces);
        blockUseGarbageCheckbox.setChecked(blockType != null && (blockType.useAsGarbage || blockType.useAsGarbageBlock || blockType.isGarbageBlockType));
        blockUseFillerCheckbox.setChecked(blockType != null && (blockType.useAsPlayingFieldFiller || blockType.useAsPlayingFieldFillerBlock));
        blockFlashingCheckbox.setChecked(blockType != null && blockType.flashingSpecialType);
        blockMatchAnyColorCheckbox.setChecked(blockType != null && blockType.matchAnyColor);
        blockCounterCheckbox.setChecked(blockType != null && blockType.counterType);
        blockClearEveryOtherLineCheckbox.setChecked(blockType != null && blockType.clearEveryOtherLineOnGridWhenCleared);
        blockIgnoreChainConnectionsCheckbox.setChecked(blockType != null && blockType.ignoreWhenCheckingChainConnections);
        blockIgnoreMovingDownCheckbox.setChecked(blockType != null && blockType.ignoreWhenMovingDownBlocks);
        blockRequireChainPresenceCheckbox.setChecked(blockType != null && blockType.chainConnectionsMustContainAtLeastOneBlockWithThisTrue);
        blockAddToExplodingChainCheckbox.setChecked(blockType != null && blockType.addToChainIfConnectedUpDownLeftRightToExplodingChainBlocks);
        blockRemoveColorFieldCheckbox.setChecked(blockType != null && blockType.removeAllBlocksOfColorOnFieldBlockIsSetOn);
        blockDiamondColorFieldCheckbox.setChecked(blockType != null && blockType.changeAllBlocksOfColorOnFieldBlockIsSetOnToDiamondColor);

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

        String selectedPieceBlockOverride = "default/random pool";
        if (pieceType != null && pieceType.overrideBlockTypes_UUID != null && !pieceType.overrideBlockTypes_UUID.isEmpty()) {
            BlockType overrideBlock = currentGameType.getBlockTypeByUUID(pieceType.overrideBlockTypes_UUID.get(0));
            if (overrideBlock != null && overrideBlock.name != null && !overrideBlock.name.isEmpty()) {
                selectedPieceBlockOverride = overrideBlock.name;
            }
        }

        String clearReward = "none";
        if (blockType != null && blockType.makePieceTypeWhenCleared_UUID != null && !blockType.makePieceTypeWhenCleared_UUID.isEmpty()) {
            PieceType rewardPiece = currentGameType.getPieceTypeByUUID(blockType.makePieceTypeWhenCleared_UUID.get(0));
            if (rewardPiece != null && rewardPiece.name != null && !rewardPiece.name.isEmpty()) {
                clearReward = rewardPiece.name;
            } else {
                clearReward = "custom reward piece";
            }
        }

        String conversionSummary = "none";
        if (blockType != null && blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut != null && !blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut.isEmpty()) {
            java.util.ArrayList<String> conversionLabels = new java.util.ArrayList<String>();
            for (BlockType.TurnFromBlockTypeToType pair : blockType.whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut) {
                BlockType fromBlock = currentGameType.getBlockTypeByUUID(pair.fromType_UUID);
                BlockType toBlock = currentGameType.getBlockTypeByUUID(pair.toType_UUID);
                conversionLabels.add((fromBlock != null && fromBlock.name != null && !fromBlock.name.isEmpty() ? fromBlock.name : "Unknown From") + "->" + (toBlock != null && toBlock.name != null && !toBlock.name.isEmpty() ? toBlock.name : "Unknown To"));
            }
            conversionSummary = String.join(", ", conversionLabels);
        }

        blockLabel.setText(blockType == null ? "Block: none" : "Block: " + (blockType.name == null || blockType.name.isEmpty() ? "Unnamed Block" : blockType.name) + " (" + (selectedBlockIndex + 1) + "/" + currentGameType.blockTypes.size() + ")" + " | Reward: " + clearReward);
        String palette = "";
        if (blockType != null && blockType.colors != null) {
            StringBuilder paletteBuilder = new StringBuilder();
            for (int i = 0; i < blockType.colors.size(); i++) {
                if (i > 0) paletteBuilder.append(", ");
                paletteBuilder.append(bobColorToHex(blockType.colors.get(i)));
            }
            palette = paletteBuilder.toString();
        }

        blockDetailsLabel.setText(blockType == null
            ? "Add a block to start configuring block types."
            : "Color: " + bobColorToHex(blockType.colors.isEmpty() ? new BobColor(128, 128, 128) : blockType.colors.get(0))
                + " | Palette: [" + palette + "]"
                + " | Special: " + bobColorToHex(blockType.specialColor == null ? (blockType.colors.isEmpty() ? new BobColor(255, 0, 255) : blockType.colors.get(0)) : blockType.specialColor)
                + " | Usage: " + (blockType.useInNormalPieces ? "normal " : "") + (blockType.useAsGarbage || blockType.useAsGarbageBlock ? "garbage " : "") + (blockType.useAsPlayingFieldFiller || blockType.useAsPlayingFieldFillerBlock ? "filler " : "")
                + "| Flags: " + (blockType.flashingSpecialType ? "flashing " : "") + (blockType.matchAnyColor ? "match-any " : "") + (blockType.counterType ? "counter " : "")
                + (blockType.clearEveryOtherLineOnGridWhenCleared ? "clear-alt-lines " : "")
                + (blockType.ignoreWhenCheckingChainConnections ? "ignore-chain " : "")
                + (blockType.ignoreWhenMovingDownBlocks ? "ignore-moving-down " : "")
                + (blockType.chainConnectionsMustContainAtLeastOneBlockWithThisTrue ? "required-in-chain " : "")
                + (blockType.addToChainIfConnectedUpDownLeftRightToExplodingChainBlocks ? "exploding-chain-link " : "")
                + (blockType.removeAllBlocksOfColorOnFieldBlockIsSetOn ? "remove-color-field " : "")
                + (blockType.changeAllBlocksOfColorOnFieldBlockIsSetOnToDiamondColor ? "diamond-color-field " : "")
                + "| Chance/Frequency: " + blockType.randomSpecialBlockChanceOneOutOf + "/" + blockType.frequencySpecialBlockTypeOnceEveryNPieces
                + " | Conversions: " + conversionSummary
        );
        pieceLabel.setText(pieceType == null ? "Piece: none" : "Piece: " + pieceType.name + " (" + (selectedPieceIndex + 1) + "/" + currentGameType.pieceTypes.size() + ")" + " | Block override: " + selectedPieceBlockOverride);
        rotationLabel.setText(rotation == null ? "Rotation: none" : "Rotation: " + selectedRotationIndex + " (" + getFilledCellCount(rotation) + " blocks)");
        rebuildRotationOverview(pieceType);
        rebuildTemplateCatalogTable();
        rebuildPresetSlotTable();
        rebuildRecentHistoryTable();
        rebuildRecentActionsTable();

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
            + " | Block reward: " + clearReward
            + " | Block conversions: " + conversionSummary
            + " | Rules: " + getEnabledRuleSummary()
        );
    }
}
