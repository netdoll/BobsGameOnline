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
        public ArrayList<Rotation> rotationSet = new ArrayList<>();
        public RotationSet(String name) { this.name = name; }
        public void add(Rotation r) { rotationSet.add(r); }
        public int size() { return rotationSet.size(); }
        public Rotation get(int i) { return rotationSet.get(i); }
        public void remove(int i) { rotationSet.remove(i); }
        public void clear() { rotationSet.clear(); }
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
            blockType = grid.getRandomBlockTypeDisregardingSpecialFrequency(overrideBlockTypes);
        }

        int maxNumBlocks = 0;
        for (int i = 0; i < pieceType.rotationSet.size(); i++) {
            maxNumBlocks = Math.max(maxNumBlocks, pieceType.rotationSet.get(i).blockOffsets.size());
        }

        for (int b = 0; b < maxNumBlocks; b++) {
            blocks.add(new Block(gameInstance, grid, this, blockType));
        }
        setRotation(0);
    }

    public void init() {
        for (Block b : blocks) b.piece = this;
        initColors();
        setPieceBlockConnections();
        setBlockColorConnectionsInPiece();
    }

    public void initColors() {
        for (Block b : blocks) {
            if (b.blockType.colors.size() > 0) b.setRandomBlockTypeColor();
            else if (pieceType.color != null) b.setColor(pieceType.color);
        }
    }

    public void setPieceBlockConnections() {
        for (Block b : blocks) b.connectedBlocksByPiece.clear();
        for (Block b : blocks) for (Block c : blocks) if (c != b && !b.connectedBlocksByPiece.contains(c)) b.connectedBlocksByPiece.add(c);
    }

    public void setBlockColorConnectionsInPiece() {
        for (Block b : blocks) b.connectedBlocksByColor.clear();
        for (Block b : blocks) for (Block c : blocks) if (c != b && b.getColor() == c.getColor() && !b.connectedBlocksByColor.contains(c)) b.connectedBlocksByColor.add(c);
    }

    public int getNumBlocksInCurrentRotation() { return pieceType.rotationSet.get(currentRotation).blockOffsets.size(); }

    public void update() {
        cursorFadeTicks += getGameLogic().ticks();
        if (cursorFadeTicks > cursorFadeTicksPerPhase) { cursorFadeTicks = 0; cursorFadeInOutToggle = !cursorFadeInOutToggle; }
        cursorAlpha = cursorFadeInOutToggle ? cursorAlphaFrom + ((float) cursorFadeTicks / cursorFadeTicksPerPhase) * (cursorAlphaTo - cursorAlphaFrom) : cursorAlphaTo - ((float) cursorFadeTicks / cursorFadeTicksPerPhase) * (cursorAlphaTo - cursorAlphaFrom);

        ghostFadeTicks += getGameLogic().ticks();
        if (ghostFadeTicks > ghostFadeTicksPerPhase) { ghostFadeTicks = 0; ghostFadeInOutToggle = !ghostFadeInOutToggle; }
        ghostAlpha = ghostFadeInOutToggle ? ghostAlphaFrom + ((float) ghostFadeTicks / ghostFadeTicksPerPhase) * (ghostAlphaTo - ghostAlphaFrom) : ghostAlphaTo - ((float) ghostFadeTicks / ghostFadeTicksPerPhase) * (ghostAlphaTo - ghostAlphaFrom);

        for (Block b : blocks) b.update();
        if (holdingBlock != null) holdingBlock.update();
    }

    public float getScreenX() { return grid.getXInFBO() + xGrid * cellW(); }
    public float getScreenY() { return grid.getYInFBO() + yGrid * cellH() + (grid.scrollPlayingFieldY / grid.scrollBlockIncrement) * cellH(); }

    public void renderAsCurrentPiece() { renderAsCurrentPiece(getScreenX(), getScreenY()); }

    public void renderAsCurrentPiece(float x, float y) {
        float w = cellW();
        float h = cellH();
        if (getGameType().gameMode == GameType.GameMode.STACK && getGameType().stackCursorType == GameType.CursorType.ONE_BLOCK_PICK_UP) {
            y -= (float) cellH() / 3;
        }

        if (getGameType().gameMode == GameType.GameMode.DROP) {
            if (getGameLogic().pieceSetAtBottom == false) {
                grid.renderGhostPiece(this);
            }
            render(x, y);
        }

        if (getGameType().currentPieceRule_OutlineBlockAtZeroZero) {
            renderOutlineBlockZeroZero(x, y, cursorAlpha, false);
        }

        if (getGameType().currentPieceOutlineFirstBlockRegardlessOfPosition) {
            renderOutlineFirstBlock(x, y, cursorAlpha, false);
        }

        if (getGameType().gameMode == GameType.GameMode.STACK && getGameType().stackCursorType == GameType.CursorType.ONE_BLOCK_PICK_UP) {
            if (holdingBlock != null) {
                holdingBlock.render(x, y, 1.0f, 1.0f, true, false);
            }
        }

        if (getGameType().gameMode == GameType.GameMode.STACK) {
            for (int i = 0; i < getNumBlocksInCurrentRotation() && i < blocks.size(); i++) {
                Block b = blocks.get(i);
                float bx = x + b.xInPiece * w + b.xInPiece * getGameType().gridPixelsBetweenColumns;
                float by = y + b.yInPiece * h + b.yInPiece * getGameType().gridPixelsBetweenRows;
                drawOutlineBox(bx, by, cursorAlpha);
            }
        }
    }

    public void render(float x, float y) {
        for (int i = 0; i < getNumBlocksInCurrentRotation() && i < blocks.size(); i++) {
            Block b = blocks.get(i);
            b.render(x + b.xInPiece * cellW(), y + b.yInPiece * cellH(), 1.0f, 1.0f, true, false);
        }
    }

    public void renderGhost(float x, float y, float alpha) {
        for (int i = 0; i < getNumBlocksInCurrentRotation() && i < blocks.size(); i++) {
            Block b = blocks.get(i);
            // Background fill to prevent overlap artifacts
            GLUtils.drawFilledRectXYWH(x + b.xInPiece * cellW(), y + b.yInPiece * cellH(), (float) cellW(), (float) cellH(), 0, 0, 0, 1.0f);
            b.render(x + b.xInPiece * cellW(), y + b.yInPiece * cellH(), ghostAlpha * alpha, 1.0f, false, true);
        }
        if (getGameType().currentPieceRule_OutlineBlockAtZeroZero) renderOutlineBlockZeroZero(x, y, (ghostAlpha / 2) * alpha, true);
        if (getGameType().currentPieceOutlineFirstBlockRegardlessOfPosition) renderOutlineFirstBlock(x, y, (ghostAlpha / 2) * alpha, true);
    }

    public void renderOutlineFirstBlock(float x, float y, float alpha, boolean asGhost) {
        if (blocks.isEmpty()) return;
        Block b = blocks.get(0);
        float bx = x + b.xInPiece * cellW();
        float by = y + b.yInPiece * cellH();
        drawOutlineBox(bx, by, alpha);
    }

    public void renderOutlineBlockZeroZero(float x, float y, float alpha, boolean asGhost) {
        for (int i = 0; i < getNumBlocksInCurrentRotation() && i < blocks.size(); i++) {
            Block b = blocks.get(i);
            if (b.xInPiece == 0 && b.yInPiece == 0) {
                float bx = x + b.xInPiece * cellW();
                float by = y + b.yInPiece * cellH();
                drawOutlineBox(bx, by, alpha);
            }
        }
    }

    private void drawOutlineBox(float bx, float by, float a) {
        float w = (float) cellW();
        float h = (float) cellH();
        for (int p = 0; p < 3; p++) {
            GLUtils.drawFilledRectXYWH(bx, by - p, w, 1, 1, 1, 1, a);
            GLUtils.drawFilledRectXYWH(bx, by + h + p - 1, w, 1, 1, 1, 1, a);
            GLUtils.drawFilledRectXYWH(bx - p, by, 1, h, 1, 1, 1, a);
            GLUtils.drawFilledRectXYWH(bx + w + p - 1, by, 1, h, 1, 1, 1, a);
        }
    }

    public void rotateCCW() { currentRotation = (currentRotation == 0) ? pieceType.rotationSet.size() - 1 : currentRotation - 1; setRotation(currentRotation); }
    public void rotateCW() { currentRotation = (currentRotation == pieceType.rotationSet.size() - 1) ? 0 : currentRotation + 1; setRotation(currentRotation); }

    public void setRandomPieceColors(boolean grayscale) {
        // TODO: Port logic from C++ if needed (was empty/TODO in Piece.cpp)
    }

    public void setRotation(int rotation) {
        currentRotation = rotation;
        if (rotation >= pieceType.rotationSet.size()) rotation -= pieceType.rotationSet.size();
        Rotation r = pieceType.rotationSet.get(rotation);
        for (int i = 0; i < getNumBlocksInCurrentRotation() && i < blocks.size(); i++) {
            BlockOffset b = r.blockOffsets.get(i);
            blocks.get(i).setXYOffsetInPiece(b.x, b.y);
        }
    }

    public int getWidth() {
        int minX = 10, maxX = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) {
            int x = pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x;
            minX = Math.min(minX, x); maxX = Math.max(maxX, x);
        }
        return maxX - minX + 1;
    }

    public int getHeight() {
        int minY = 10, maxY = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) {
            int y = pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).y;
            minY = Math.min(minY, y); maxY = Math.max(maxY, y);
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

    public int getHighestOffsetX() {
        int maxX = -10;
        for (int i = 0; i < getNumBlocksInCurrentRotation(); i++) maxX = Math.max(maxX, pieceType.rotationSet.get(currentRotation).blockOffsets.get(i).x);
        return maxX;
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
        RotationSet rotations = new RotationSet("2 Block BottomLeft Always Filled");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
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
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, 1)); r.add(new BlockOffset(-1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get3BlockDRotationSet() {
        RotationSet rotations = new RotationSet("3 Block D");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, -1)); r.add(new BlockOffset(1, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(-1, 1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 1)); r.add(new BlockOffset(-1, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(-1, 1)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get4BlockORotationSet() {
        RotationSet rotations = new RotationSet("4 Block O");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(1, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, 0)); rotations.add(r); }
        { Rotation r = new Rotation(); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(0, -1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get4BlockSolidRotationSet() {
        RotationSet rotations = new RotationSet("4 Block Solid");
        { Rotation r = new Rotation(); r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, -1)); rotations.add(r); }
        return rotations;
    }

    public static RotationSet get9BlockSolidRotationSet() {
        RotationSet rotations = new RotationSet("9 Block Solid");
        {
            Rotation r = new Rotation();
            r.add(new BlockOffset(0, 0)); r.add(new BlockOffset(1, 0)); r.add(new BlockOffset(2, 0));
            r.add(new BlockOffset(0, -1)); r.add(new BlockOffset(1, -1)); r.add(new BlockOffset(2, -1));
            r.add(new BlockOffset(0, -2)); r.add(new BlockOffset(1, -2)); r.add(new BlockOffset(2, -2));
            rotations.add(r);
        }
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
