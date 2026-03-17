package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import com.bobsgame.client.GLUtils;
import java.io.Serializable;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Piece {
    public static final Logger log = LoggerFactory.getLogger(Piece.class);

    public enum RotationType { SRS, SEGA, NES, GB, DTET }

    public static class BlockOffset implements Serializable {
        public int x = 0;
        public int y = 0;
        public BlockOffset(int x, int y) { this.x = x; this.y = y; }
        public BlockOffset() {}
    }

    public static class Rotation implements Serializable {
        public ArrayList<BlockOffset> blockOffsets = new ArrayList<>();
        public void add(BlockOffset b) { blockOffsets.add(b); }
    }

    public static class RotationSet implements Serializable {
        public String name = "";
        public ArrayList<Rotation> rotations = new ArrayList<>();
        public RotationSet(String name) { this.name = name; }
        public void add(Rotation r) { rotations.add(r); }
        public int size() { return rotations.size(); }
        public Rotation get(int i) { return rotations.get(i); }
        public void remove(int i) { rotations.remove(i); }
        public void clear() { rotations.clear(); }
    }

    public Grid grid = null;
    public GameLogic game = null;

    public int currentRotation = 0;

    public int xGrid = 0;
    public int yGrid = 0;

    public ArrayList<Block> blocks = new ArrayList<>();

    public float cursorAlphaFrom = 0.3f;
    public float cursorAlphaTo = 1.0f;
    public long cursorFadeTicksPerPhase = 200;
    private float cursorAlpha = 0.3f;
    private long cursorFadeTicks = 0;
    private boolean cursorFadeInOutToggle = false;

    public float ghostAlphaFrom = 0.5f;
    public float ghostAlphaTo = 0.8f;
    public long ghostFadeTicksPerPhase = 200;
    private float ghostAlpha = 0.5f;
    private long ghostFadeTicks = 0;
    private boolean ghostFadeInOutToggle = false;

    public Block holdingBlock = null;
    public PieceType pieceType = null;
    public boolean overrideAnySpecialBehavior = false;
    public int piecesSetSinceThisPieceSet = 0;
    public boolean setInGrid = false;

    public Piece(GameLogic gameInstance, Grid grid, PieceType pieceType, ArrayList<BlockType> blockTypes) {
        this.game = gameInstance;
        this.grid = grid;
        this.pieceType = pieceType;

        int maxNumBlocks = 0;
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            maxNumBlocks = Math.max(maxNumBlocks, pieceType.rotationSet.get(i).blockOffsets.size());
        }

        if (pieceType.overrideBlockTypes_UUID.size() > 0) {
            ArrayList<BlockType> overrideBlockTypes = new ArrayList<>();
            for (String uuid : pieceType.overrideBlockTypes_UUID) {
                overrideBlockTypes.add(gameInstance.currentGameType.getBlockTypeByUUID(uuid));
            }
            for (int b = 0; b < maxNumBlocks; b++) {
                blocks.add(new Block(gameInstance, grid, this, grid.getRandomBlockTypeDisregardingSpecialFrequency(overrideBlockTypes)));
            }
        } else {
            for (int b = 0; b < maxNumBlocks; b++) {
                blocks.add(new Block(gameInstance, grid, this, grid.getRandomBlockType(blockTypes)));
            }
        }
        setRotation(0);
    }

    public Piece(GameLogic gameInstance, Grid grid, PieceType pieceType, BlockType blockType) {
        this.game = gameInstance;
        this.grid = grid;
        this.pieceType = pieceType;

        if (pieceType.overrideBlockTypes_UUID.size() > 0) {
            ArrayList<BlockType> overrideBlockTypes = new ArrayList<>();
            for (String uuid : pieceType.overrideBlockTypes_UUID) {
                overrideBlockTypes.add(gameInstance.currentGameType.getBlockTypeByUUID(uuid));
            }
            for (int b = 0; b < pieceType.rotationSet.get(0).blockOffsets.size(); b++) {
                blocks.add(new Block(gameInstance, grid, this, grid.getRandomBlockTypeDisregardingSpecialFrequency(overrideBlockTypes)));
            }
        } else {
            for (int b = 0; b < pieceType.rotationSet.get(0).blockOffsets.size(); b++) {
                blocks.add(new Block(gameInstance, grid, this, blockType));
            }
        }
        setRotation(0);
    }

    public Piece(GameLogic gameInstance, Grid grid, PieceType pieceType, GameType.BlockTypes type) {
        this.game = gameInstance;
        this.grid = grid;
        this.pieceType = pieceType;

        ArrayList<BlockType> blockTypes = new ArrayList<>();
        if (type == GameType.BlockTypes.NORMAL) blockTypes = gameInstance.currentGameType.getNormalBlockTypes(gameInstance.getCurrentDifficulty());
        else if (type == GameType.BlockTypes.GARBAGE) blockTypes = gameInstance.currentGameType.getGarbageBlockTypes(gameInstance.getCurrentDifficulty());

        int maxNumBlocks = 0;
        if (pieceType.rotationSet != null && pieceType.rotationSet.size() > 0) {
            for (int i = 0; i < pieceType.rotationSet.size(); i++) {
                maxNumBlocks = Math.max(maxNumBlocks, pieceType.rotationSet.get(i).blockOffsets.size());
            }
        } else {
            maxNumBlocks = 1;
        }

        for (int b = 0; b < maxNumBlocks; b++) {
            blocks.add(new Block(gameInstance, grid, this, grid.getRandomBlockType(blockTypes)));
        }
        setRotation(0);
    }

    public void init() {
        for (Block b : blocks) b.setRandomBlockTypeColor();
    }

    public void update() {
        for (Block b : blocks) b.update();

        cursorFadeTicks += getGameLogic().ticks();
        if (cursorFadeTicks > cursorFadeTicksPerPhase) { cursorFadeTicks = 0; cursorFadeInOutToggle = !cursorFadeInOutToggle; }
        if (cursorFadeInOutToggle) cursorAlpha = cursorAlphaFrom + ((float) cursorFadeTicks / cursorFadeTicksPerPhase) * (cursorAlphaTo - cursorAlphaFrom);
        else cursorAlpha = cursorAlphaTo - ((float) cursorFadeTicks / cursorFadeTicksPerPhase) * (cursorAlphaTo - cursorAlphaFrom);

        ghostFadeTicks += getGameLogic().ticks();
        if (ghostFadeTicks > ghostFadeTicksPerPhase) { ghostFadeTicks = 0; ghostFadeInOutToggle = !ghostFadeInOutToggle; }
        if (ghostFadeInOutToggle) ghostAlpha = ghostAlphaFrom + ((float) ghostFadeTicks / ghostFadeTicksPerPhase) * (ghostAlphaTo - ghostAlphaFrom);
        else ghostAlpha = ghostAlphaTo - ((float) ghostFadeTicks / ghostFadeTicksPerPhase) * (ghostAlphaTo - ghostAlphaFrom);
    }

    public void setRotation(int rotation) {
        this.currentRotation = rotation;
        Rotation r = pieceType.rotationSet.get(rotation);
        for (int i = 0; i < r.blockOffsets.size(); i++) {
            if (i < blocks.size()) blocks.get(i).setXYOffsetInPiece(r.blockOffsets.get(i).x, r.blockOffsets.get(i).y);
        }
    }

    public float getScreenX() { return grid.getXInFBO() + xGrid * cellW(); }
    public float getScreenY() { return grid.getYInFBO() + yGrid * cellH() + (grid.scrollPlayingFieldY / grid.scrollBlockIncrement) * cellH(); }

    public void render(float a, float scale, boolean interpolate) {
        for (Block b : blocks) b.render(getScreenX() + b.xInPiece * cellW() * scale, getScreenY() + b.yInPiece * cellH() * scale, a, scale, interpolate, false);
    }

    public void renderGhost(float screenX, float screenY, float a) {
        for (Block b : blocks) b.render(screenX + b.xInPiece * cellW(), screenY + b.yInPiece * cellH(), a * ghostAlpha, 1.0f, true, true);
    }

    public void renderAsCurrentPiece(float screenX, float screenY) {
        render(1.0f, 1.0f, true);
    }

    public void renderCursor(float a) {
        for (Block b : blocks) {
            float x = getScreenX() + b.xInPiece * cellW();
            float y = getScreenY() + b.yInPiece * cellH();
            GLUtils.drawBox(x, x + cellW(), y, y + cellH(), 1, 1, 1);
        }
    }

    public int getHighestOffsetX() {
        int maxX = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) maxX = Math.max(maxX, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x);
        return maxX;
    }

    public int getNumBlocksInCurrentRotation() { return pieceType.rotationSet.get(currentRotation).blockOffsets.size(); }

    public int getWidth() {
        int minX = 10, maxX = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) {
            minX = Math.min(minX, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x);
            maxX = Math.max(maxX, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x);
        }
        return maxX - minX + 1;
    }

    public int getHeight() {
        int minY = 10, maxY = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) {
            minY = Math.min(minY, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).y);
            maxY = Math.max(maxY, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).y);
        }
        return maxY - minY + 1;
    }

    public int getLowestOffsetX() {
        int minX = 10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) minX = Math.min(minX, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x);
        return minX;
    }

    public int getLowestOffsetY() {
        int minY = 10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) minY = Math.min(minY, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).y);
        return minY;
    }

    public int getHighestOffsetY() {
        int maxY = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) maxY = Math.max(maxY, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).y);
        return maxY;
    }

    public void setBlocksSlamming() {
        for (Block b : blocks) {
            b.slamming = true;
            b.slamX = getScreenX() + b.xInPiece * cellW();
            b.slamY = getScreenY() + b.yInPiece * cellH();
        }
    }

    public void rotateCCW() { currentRotation = (currentRotation == 0) ? pieceType.rotationSet.size() - 1 : currentRotation - 1; setRotation(currentRotation); }
    public void rotateCW() { currentRotation = (currentRotation == pieceType.rotationSet.size() - 1) ? 0 : currentRotation + 1; setRotation(currentRotation); }

    public int cellW() { return getGameLogic().cellW(); }
    public int cellH() { return getGameLogic().cellH(); }
    public GameType getGameType() { return getGameLogic().currentGameType; }
    public GameLogic getGameLogic() { return game; }

    public static RotationSet get2BlockRotateAround00RotationSet() {
        RotationSet rotations = new RotationSet("2 Block Rotate Around 0,0");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get2BlockBottomLeftAlwaysFilledRotationSet() {
        RotationSet rotations = new RotationSet("2 Block Bottom Left Always Filled");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get1BlockCursorRotationSet() {
        RotationSet rotations = new RotationSet("1 Block Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get2BlockHorizontalCursorRotationSet() {
        RotationSet rotations = new RotationSet("2 Block Horizontal Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get2BlockVerticalCursorRotationSet() {
        RotationSet rotations = new RotationSet("2 Block Vertical Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockHorizontalCursorRotationSet() {
        RotationSet rotations = new RotationSet("3 Block Horizontal Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockVerticalCursorRotationSet() {
        RotationSet rotations = new RotationSet("3 Block Vertical Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get4BlockCursorRotationSet() {
        RotationSet rotations = new RotationSet("4 Block Cursor");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockVerticalRotationSet() {
        RotationSet rotations = new RotationSet("3 Block Vertical Swap");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, -2)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, -2)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, -2)); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockHorizontalRotationSet() {
        RotationSet rotations = new RotationSet("3 Block Horizontal Swap");
        { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockTRotationSet() {
        RotationSet rotations = new RotationSet("3 Block T");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(-1, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockLRotationSet() {
        RotationSet rotations = new RotationSet("3 Block L");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockJRotationSet() {
        RotationSet rotations = new RotationSet("3 Block J");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockIRotationSet() {
        RotationSet rotations = new RotationSet("3 Block I");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockCRotationSet() {
        RotationSet rotations = new RotationSet("3 Block C");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockDRotationSet() {
        RotationSet rotations = new RotationSet("3 Block D");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get4BlockORotationSet() {
        RotationSet rotations = new RotationSet("4 Block O");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get4BlockSolidRotationSet() {
        return get4BlockORotationSet();
    }

    public static RotationSet get9BlockSolidRotationSet() {
        RotationSet rotations = new RotationSet("9 Block Solid");
        Rotation r = new Rotation();
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) r.add(new BlockOffset(x, y));
        rotations.add(r);
        return rotations;
    }

    public static RotationSet get4BlockIRotationSet(RotationType type) {
        String name = "4 Block I";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS || type == RotationType.DTET || type == RotationType.SEGA) {
            if (type == RotationType.SRS || type == RotationType.SEGA) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-2, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0));
                rotations.add(r);
            }
            if (type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-2, 1)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1));
                rotations.add(r);
            }
            {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 2));
                rotations.add(r);
            }
            if (type == RotationType.SRS || type == RotationType.DTET) {
                { Rotation r = new Rotation(); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-2, 1)); rotations.add(r); }
                { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 2)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, -1)); rotations.add(r); }
            }
            if (type == RotationType.SEGA) {
                { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-2, 0)); rotations.add(r); }
                { Rotation r = new Rotation(); r.add(new BlockOffset(0, 2)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
            }
        }
        if (type == RotationType.GB) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(2, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, -2)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(2, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, -2)); rotations.add(r); }
        }
        if (type == RotationType.NES) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(-2, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, -2)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-2, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, -2)); rotations.add(r); }
        }
        return rotations;
    }

    public static RotationSet get4BlockJRotationSet(RotationType type) {
        String name = "4 Block J";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        }
        if (type == RotationType.SEGA || type == RotationType.GB || type == RotationType.NES || type == RotationType.DTET) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-1, 0));
                rotations.add(r);
            }
            if (type == RotationType.GB || type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, -1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
        }
        return rotations;
    }

    public static RotationSet get4BlockLRotationSet(RotationType type) {
        String name = "4 Block L";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(-1, -1)); rotations.add(r); }
        }
        if (type == RotationType.SEGA || type == RotationType.GB || type == RotationType.NES || type == RotationType.DTET) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(-1, -1));
                rotations.add(r);
            }
            if (type == RotationType.GB || type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(-1, -1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
        }
        return rotations;
    }

    public static RotationSet get4BlockSRotationSet(RotationType type) {
        String name = "4 Block S";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS || type == RotationType.DTET) {
            if (type == RotationType.SRS) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0));
                rotations.add(r);
            }
            if (type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        }
        if (type == RotationType.SEGA || type == RotationType.GB || type == RotationType.NES) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.GB) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, -1));
                rotations.add(r);
            }
            if (type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.GB) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1));
                rotations.add(r);
            }
            if (type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(1, 1));
                rotations.add(r);
            }
        }
        return rotations;
    }

    public static RotationSet get4BlockTRotationSet(RotationType type) {
        String name = "4 Block T";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        }
        if (type == RotationType.SEGA || type == RotationType.GB || type == RotationType.NES || type == RotationType.DTET) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1));
                rotations.add(r);
            }
            if (type == RotationType.GB || type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(-1, 0));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        }
        return rotations;
    }

    public static RotationSet get4BlockZRotationSet(RotationType type) {
        String name = "4 Block Z";
        if (type == RotationType.DTET) name += " (DTET)";
        if (type == RotationType.SRS) name += " (SRS)";
        if (type == RotationType.SEGA) name += " (SEGA)";
        if (type == RotationType.NES) name += " (NES)";
        if (type == RotationType.GB) name += " (GB)";
        RotationSet rotations = new RotationSet(name);

        if (type == RotationType.SRS || type == RotationType.DTET) {
            if (type == RotationType.SRS) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0));
                rotations.add(r);
            } else if (type == RotationType.DTET) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        }
        if (type == RotationType.SEGA || type == RotationType.GB || type == RotationType.NES) {
            { Rotation r = new Rotation(); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1));
                rotations.add(r);
            }
            if (type == RotationType.GB) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(-1, 1));
                rotations.add(r);
            }
            { Rotation r = new Rotation(); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
            if (type == RotationType.SEGA || type == RotationType.NES) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(1, -1));
                rotations.add(r);
            }
            if (type == RotationType.GB) {
                Rotation r = new Rotation();
                r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1));
                rotations.add(r);
            }
        }
        return rotations;
    }
}
