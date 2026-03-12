package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import com.bobsgame.client.GLUtils;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bobsgame.shared.Easing;

public class Grid {
    public static final Logger log = LoggerFactory.getLogger(Grid.class);

    public GameLogic game = null;
    public ArrayList<Block> blocks = new ArrayList<>();

    public float screenX = 0;
    public float screenY = 0;

    public float wigglePlayingFieldX = 0;
    public float wigglePlayingFieldY = 0;
    public float wigglePlayingFieldMaxX = 2;
    public int wigglePlayingFieldTicksSpeed = 40;
    private long wigglePlayingFieldTicks = 0;
    private boolean wigglePlayingFieldLeftRightToggle = false;

    private int shakePlayingFieldMaxX = 0;
    private int shakePlayingFieldMaxY = 0;
    private int shakePlayingFieldTicksPerShake = 0;
    private int shakePlayingFieldTicksDuration = 0;
    private long shakePlayingFieldStartTime = 0;
    private int shakePlayingFieldTicksPerShakeXCounter = 0;
    private int shakePlayingFieldTicksPerShakeYCounter = 0;
    private int shakePlayingFieldScreenTicksCounter = 0;
    private int shakePlayingFieldX = 0;
    private int shakePlayingFieldY = 0;
    private boolean shakePlayingFieldLeftRightToggle = false;
    private boolean shakePlayingFieldUpDownToggle = false;

    public float scrollPlayingFieldY = 0;
    public float scrollBlockIncrement = 60;

    private int scrollPlayingFieldBackgroundTicksSpeed = 30;
    private int backgroundScrollX = 0;
    private int backgroundScrollY = 0;
    private long scrollPlayingFieldBackgroundTicks = 0;

    private int lastGarbageHoleX = 0;
    private boolean garbageHoleDirectionToggle = false;

    public ArrayList<Piece> randomBag = new ArrayList<>();

    public Grid(GameLogic gameInstance) {
        this.game = gameInstance;
        int size = getWidth() * getHeight();
        for (int i = 0; i < size; i++) blocks.add(null);
    }

    public float getXInFBO() { return getXInFBONoShake() + wigglePlayingFieldX + shakePlayingFieldX; }
    public float getYInFBO() { return getYInFBONoShake() + wigglePlayingFieldY + shakePlayingFieldY; }
    public float getXInFBONoShake() { return (getGameLogic().playingFieldX1 - getGameLogic().playingFieldX0) / 2 - getWidth() * cellW() / 2; }
    public float getYInFBONoShake() { return 5 * cellH(); }
    public float getXOnScreenNoShake() { return screenX; }
    public float getYOnScreenNoShake() { return screenY; }
    public float bgX() { return getXInFBO() + backgroundScrollX; }
    public float bgY() { return getYInFBO() + backgroundScrollY; }
    public int getHeight() { return (getGameType() != null ? getGameType().gridHeight : 20) + GameLogic.aboveGridBuffer; }
    public int getWidth() { return getGameType() != null ? getGameType().gridWidth : 10; }

    public void update() {
        ArrayList<Piece> piecesInGrid = getArrayOfPiecesOnGrid();
        for (Piece p : piecesInGrid) p.update();
        updateShake();
    }

    public void reformat(int oldWidth, int oldHeight) {
        ArrayList<Block> blockList = new ArrayList<>();
        if (!blocks.isEmpty()) {
            for (int y = oldHeight - 1; y >= 0; y--) {
                for (int x = 0; x < oldWidth; x++) {
                    int index = y * oldWidth + x;
                    if (index < blocks.size() && blocks.get(index) != null) {
                        Block b = blocks.get(index);
                        blocks.set(index, null);
                        b.xInPiece = 0; b.yInPiece = 0;
                        b.connectedBlocksByColor.clear();
                        b.connectedBlocksByPiece.clear();
                        blockList.add(b);
                    }
                }
            }
        }
        blocks.clear();
        int newSize = getWidth() * getHeight();
        for (int i = 0; i < newSize; i++) blocks.add(null);
        int x = 0; int y = getHeight() - 1;
        while (!blockList.isEmpty() && y >= 0) {
            add(x, y, blockList.remove(0));
            x++;
            if (x >= getWidth()) { y--; x = 0; }
        }
    }

    public int getNumberOfFilledCells() {
        int amt = 0;
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) if (get(x, y) != null) amt++;
        return amt;
    }

    public void removeAllBlocksOfPieceFromGrid(Piece p, boolean fadeOut) {
        for (Block b : p.blocks) if (b.setInGrid) remove(b, fadeOut, true);
    }

    public void replaceAllBlocksWithNewGameBlocks() {
        ArrayList<Block> removedBlocks = new ArrayList<>();
        int maxHeight = GameLogic.aboveGridBuffer + ((getHeight() - GameLogic.aboveGridBuffer) / 3);
        for (int y = getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                Block a = get(x, y);
                if (a != null) {
                    removedBlocks.add(a);
                    remove(a, y < maxHeight, true);
                }
            }
        }
        ArrayList<BlockType> bt = getGameType().getPlayingFieldBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<PieceType> pt = getGameType().getPlayingFieldPieceTypes(getGameLogic().getCurrentDifficulty());
        for (Block a : removedBlocks) {
            int x = a.xGrid; int y = a.yGrid;
            if (y >= maxHeight) {
                Piece p = putOneBlockPieceInGridCheckingForFillRules(x, y, pt, bt);
                if (p != null && !p.blocks.isEmpty()) {
                    Block b = p.blocks.get(0);
                    if (b != null) { b.lastScreenX = a.lastScreenX; b.lastScreenY = a.lastScreenY; b.ticksSinceLastMovement = 0; }
                }
            }
        }
    }

    public Piece putOneBlockPieceInGridCheckingForFillRules(int x, int y, ArrayList<PieceType> pieceTypes, ArrayList<BlockType> blockTypes) {
        Piece p = null;
        if (getGameType().stackDontPutSameColorNextToEachOther) p = dontPutSameColorNextToEachOtherOrReturnNull(p, x, y, pieceTypes, blockTypes);
        if (getGameType().stackDontPutSameBlockTypeNextToEachOther) p = dontPutSameBlockTypeNextToEachOtherOrReturnNull(p, x, y, pieceTypes, blockTypes);
        if (getGameType().stackDontPutSameColorDiagonalOrNextToEachOtherReturnNull) p = dontPutSameColorDiagonalOrNextToEachOtherReturnNull(p, x, y, pieceTypes, blockTypes);
        if (p == null) {
            p = getRandomPiece(pieceTypes, blockTypes);
            while (p.blocks.size() > 1) p.blocks.remove(p.blocks.size() - 1).breakConnectionsInPiece();
        }
        if (p != null) {
            setPiece(p, x, y);
            if (getGameType().stackLeaveAtLeastOneGapPerRow) {
                boolean isFull = true;
                for (int xx = 0; xx < getWidth(); xx++) if (get(xx, y) == null) { isFull = false; break; }
                if (isFull) remove(getGameLogic().getRandomIntLessThan(getWidth(), "putOneBlockPieceInGridCheckingForFillRules"), y, false, true);
            }
        }
        return p;
    }

    public Piece dontPutSameColorDiagonalOrNextToEachOtherReturnNull(Piece p, int x, int y, ArrayList<PieceType> pieceTypes, ArrayList<BlockType> blockTypes) {
        ArrayList<BobColor> acceptableColors = new ArrayList<>();
        int maxC = getGameLogic().getCurrentDifficulty().maximumBlockTypeColors;
        for (BlockType bt : blockTypes) {
            int amt = Math.min(bt.colors.size(), maxC);
            for (int i = 0; i < amt; i++) {
                BobColor c = bt.colors.get(i);
                if (!acceptableColors.contains(c)) acceptableColors.add(c);
            }
        }
        if (x > 0 && y > 0 && get(x-1, y-1) != null) acceptableColors.remove(get(x-1, y-1).getColor());
        if (x > 0 && y < getHeight()-1 && get(x-1, y+1) != null) acceptableColors.remove(get(x-1, y+1).getColor());
        if (x > 0 && get(x-1, y) != null) acceptableColors.remove(get(x-1, y).getColor());
        if (y < getHeight()-1 && get(x, y+1) != null) acceptableColors.remove(get(x, y+1).getColor());
        if (y > 0 && get(x, y-1) != null) acceptableColors.remove(get(x, y-1).getColor());
        if (!acceptableColors.isEmpty()) {
            BobColor color = acceptableColors.get(getGameLogic().getRandomIntLessThan(acceptableColors.size(), "dontPutSameColorDiagonalOrNextToEachOtherReturnNull"));
            if (p == null) {
                p = getRandomPiece(pieceTypes, blockTypes);
                while (p.blocks.size() > 1) p.blocks.remove(p.blocks.size() - 1).breakConnectionsInPiece();
            }
            for (Block b : p.blocks) b.setColor(color);
            return p;
        }
        return null;
    }

    public Piece dontPutSameColorNextToEachOtherOrReturnNull(Piece p, int x, int y, ArrayList<PieceType> pieceTypes, ArrayList<BlockType> blockTypes) {
        ArrayList<BobColor> acceptableColors = new ArrayList<>();
        int maxC = getGameLogic().getCurrentDifficulty().maximumBlockTypeColors;
        for (BlockType bt : blockTypes) {
            int amt = Math.min(bt.colors.size(), maxC);
            for (int i = 0; i < amt; i++) {
                BobColor c = bt.colors.get(i);
                if (!acceptableColors.contains(c)) acceptableColors.add(c);
            }
        }
        if (x > 0 && get(x-1, y) != null) acceptableColors.remove(get(x-1, y).getColor());
        if (y < getHeight()-1 && get(x, y+1) != null) acceptableColors.remove(get(x, y+1).getColor());
        if (y > 0 && get(x, y-1) != null) acceptableColors.remove(get(x, y-1).getColor());
        if (!acceptableColors.isEmpty()) {
            BobColor color = acceptableColors.get(getGameLogic().getRandomIntLessThan(acceptableColors.size(), "dontPutSameColorNextToEachOtherOrReturnNull"));
            if (p == null) {
                p = getRandomPiece(pieceTypes, blockTypes);
                while (p.blocks.size() > 1) p.blocks.remove(p.blocks.size() - 1).breakConnectionsInPiece();
                for (Block b : p.blocks) b.setColor(color);
            } else {
                for (Block b : p.blocks) if (!acceptableColors.contains(b.getColor())) b.setColor(color);
            }
            return p;
        }
        return null;
    }

    public Piece dontPutSameBlockTypeNextToEachOtherOrReturnNull(Piece p, int x, int y, ArrayList<PieceType> pieceTypes, ArrayList<BlockType> blockTypes) {
        ArrayList<BlockType> acceptableBlockTypes = new ArrayList<>(blockTypes);
        if (x > 0 && get(x-1, y) != null) acceptableBlockTypes.remove(get(x-1, y).blockType);
        if (y < getHeight()-1 && get(x, y+1) != null) acceptableBlockTypes.remove(get(x, y+1).blockType);
        if (y > 0 && get(x, y-1) != null) acceptableBlockTypes.remove(get(x, y-1).blockType);
        if (!acceptableBlockTypes.isEmpty()) {
            if (p != null) { for (Block b : p.blocks) if (!acceptableBlockTypes.contains(b.blockType)) { p = null; break; } }
            if (p == null) {
                PieceType pt = getRandomPieceType(pieceTypes);
                BlockType bt = acceptableBlockTypes.get(getGameLogic().getRandomIntLessThan(acceptableBlockTypes.size(), "dontPutSameBlockTypeNextToEachOtherOrReturnNull"));
                p = new Piece(game, this, pt, bt); p.init();
                while (p.blocks.size() > 1) p.blocks.remove(p.blocks.size() - 1).breakConnectionsInPiece();
            }
            return p;
        }
        return null;
    }

    public void removeAndDestroyAllBlocksInGrid() {
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) if (get(x, y) != null) remove(x, y, true, true);
    }

    public void randomlyFillGridWithPlayingFieldPieces(int numberOfBlocks, int topY) {
        topY += GameLogic.aboveGridBuffer;
        int fieldSize = getWidth() * Math.max((getHeight() - topY), 0);
        int num = getNumberOfFilledCells();
        if (num > 0 && num < numberOfBlocks) numberOfBlocks = num;
        if (numberOfBlocks >= fieldSize) numberOfBlocks = fieldSize - 1;
        if (numberOfBlocks < 0) numberOfBlocks = 0;
        ArrayList<Block> blockList = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) {
            Block b = remove(x, y, false, true);
            if (b != null && !blockList.contains(b)) blockList.add(b);
        }
        ArrayList<BlockType> bt = getGameType().getPlayingFieldBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<PieceType> pt = getGameType().getPlayingFieldPieceTypes(getGameLogic().getCurrentDifficulty());
        for (int i = 0; i < numberOfBlocks; i++) {
            int r = getGameLogic().getRandomIntLessThan(fieldSize, "randomlyFillGridWithPlayingFieldPieces");
            int x = r % getWidth(); int y = (r / getWidth()) + topY;
            int attempt = 0;
            while (get(x, y) != null && attempt < fieldSize) {
                r = getGameLogic().getRandomIntLessThan(fieldSize, "randomlyFillGridWithPlayingFieldPieces");
                x = r % getWidth(); y = (r / getWidth()) + topY;
                attempt++;
            }
            if (get(x, y) == null) { Piece p = putOneBlockPieceInGridCheckingForFillRules(x, y, pt, bt); if (p != null) i += p.blocks.size() - 1; }
        }
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) {
            Block b = get(x, y);
            if (b != null && !blockList.isEmpty()) { Block a = blockList.remove(0); b.lastScreenX = a.lastScreenX; b.lastScreenY = a.lastScreenY; b.ticksSinceLastMovement = 0; }
        }
    }

    public void buildRandomStackRetainingExistingBlocks(int numberOfBlocks, int topY) {
        topY += GameLogic.aboveGridBuffer;
        scrollPlayingFieldY = 0;
        int fieldSize = getWidth() * Math.max((getHeight() - topY), 0);
        int num = getNumberOfFilledCells();
        if (num > 0 && num < numberOfBlocks) numberOfBlocks = num;
        if (numberOfBlocks >= fieldSize) numberOfBlocks = fieldSize - 1;
        if (numberOfBlocks < 0) numberOfBlocks = 0;
        ArrayList<Block> blockList = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) {
            Block b = remove(x, y, false, true);
            if (b != null && !blockList.contains(b)) blockList.add(b);
        }
        ArrayList<BlockType> bt = getGameType().getPlayingFieldBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<PieceType> pt = getGameType().getPlayingFieldPieceTypes(getGameLogic().getCurrentDifficulty());
        int blocksPlaced = 0;
        for (int y = getHeight() - 1; y >= topY; y--) for (int x = 0; x < getWidth(); x++) {
            if (get(x, y) != null) blocksPlaced++;
            else { Piece p = putOneBlockPieceInGridCheckingForFillRules(x, y, pt, bt); if (p != null) blocksPlaced += p.blocks.size(); }
        }
        while (blocksPlaced > numberOfBlocks) {
            int x = getGameLogic().getRandomIntLessThan(getWidth(), "buildRandomStackRetainingExistingBlocks");
            for (int y = 0; y < getHeight() && blocksPlaced > numberOfBlocks; y++) if (get(x, y) != null) { remove(x, y, false, true); blocksPlaced--; break; }
        }
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) {
            Block b = get(x, y);
            if (b != null && !blockList.isEmpty()) { Block a = blockList.remove(0); b.lastScreenX = a.lastScreenX; b.lastScreenY = a.lastScreenY; b.ticksSinceLastMovement = 0; }
        }
    }

    public boolean scrollUpStack(Piece cursorPiece, int amt) {
        scrollPlayingFieldY -= amt;
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) { Block b = get(x, y); if (b != null) b.lastScreenY = b.getScreenY(); }
        if (scrollPlayingFieldY < 0 - scrollBlockIncrement) {
            for (int x = 0; x < getWidth(); x++) if (get(x, GameLogic.aboveGridBuffer) != null) { scrollPlayingFieldY = 0 - scrollBlockIncrement; return false; }
            cursorPiece.yGrid -= 1;
            if (cursorPiece.yGrid < 1 + GameLogic.aboveGridBuffer) cursorPiece.yGrid += 1;
            scrollPlayingFieldY += scrollBlockIncrement;
            for (int y = 0; y < getHeight() - 1; y++) for (int x = 0; x < getWidth(); x++) { Block b = remove(x, y + 1, false, false); if (b != null) add(x, y, b); }
            ArrayList<BlockType> bt = getGameType().getNormalBlockTypes(getGameLogic().getCurrentDifficulty());
            ArrayList<PieceType> pt = getGameType().getNormalPieceTypes(getGameLogic().getCurrentDifficulty());
            for (int x = 0; x < getWidth(); x++) putOneBlockPieceInGridCheckingForFillRules(x, getHeight() - 1, pt, bt);
            getGameLogic().piecesMadeThisGame++;
        }
        return true;
    }

    public void makeGarbageRowFromCeiling() {
        ArrayList<BlockType> bt = getGameType().getGarbageBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<PieceType> pt = getGameType().getGarbagePieceTypes(getGameLogic().getCurrentDifficulty());
        for (int x = 0; x < getWidth(); x++) {
            Piece p = putOneBlockPieceInGridCheckingForFillRules(x, 0, pt, bt);
            if (p != null) for (Block b : p.blocks) { b.lastScreenX = getXInFBO() + b.xGrid * cellW(); b.lastScreenY = getYInFBO() + b.yInPiece * cellH(); b.ticksSinceLastMovement = 0; }
        }
    }

    public void moveAllRowsUpOne() {
        for (int x = 0; x < getWidth(); x++) { Block b = get(x, 0); if (b != null) remove(b, true, true); }
        for (int y = 0; y < getHeight() - 1; y++) for (int x = 0; x < getWidth(); x++) { Block b = remove(x, y + 1, false, false); if (b != null) add(x, y, b); }
    }

    public void makeGarbageRowFromFloor() {
        moveAllRowsUpOne();
        int y = getHeight() - 1;
        if (getGameType().playingFieldGarbageType == GameType.GarbageType.MATCH_BOTTOM_ROW) { for (int x = 0; x < getWidth(); x++) if (get(x, y - 1) != null) putGarbageBlockFromFloor(x, y); }
        else if (getGameType().playingFieldGarbageType == GameType.GarbageType.RANDOM) { for (int x = 0; x < getWidth(); x++) if (getGameLogic().getRandomIntLessThan(2, "makeGarbageRowFromFloor") == 0) putGarbageBlockFromFloor(x, y); }
        else if (getGameType().playingFieldGarbageType == GameType.GarbageType.ZIGZAG_PATTERN) {
            for (int x = 0; x < getWidth(); x++) if (x != lastGarbageHoleX) putGarbageBlockFromFloor(x, y);
            if (garbageHoleDirectionToggle) { lastGarbageHoleX++; if (lastGarbageHoleX >= getWidth()) { lastGarbageHoleX = getWidth() - 1; garbageHoleDirectionToggle = false; } }
            else { lastGarbageHoleX--; if (lastGarbageHoleX < 0) { lastGarbageHoleX = 0; garbageHoleDirectionToggle = true; } }
        }
    }

    public void putGarbageBlockFromFloor(int x, int y) {
        ArrayList<BlockType> bt = getGameType().getGarbageBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<PieceType> pt = getGameType().getGarbagePieceTypes(getGameLogic().getCurrentDifficulty());
        Piece p = putOneBlockPieceInGridCheckingForFillRules(x, y, pt, bt);
        if (p != null) for (Block b : p.blocks) { b.lastScreenX = getXInFBO() + b.xGrid * cellW(); b.lastScreenY = getYInFBO() + b.yInPiece * cellH() + getHeight() * cellH(); b.ticksSinceLastMovement = 0; }
    }

    public void cursorSwapBetweenTwoBlocksHorizontal(Piece cursor) {
        Block a = get(cursor.xGrid, cursor.yGrid); Block b = get(cursor.xGrid + 1, cursor.yGrid);
        if (a != null && a.interpolateSwappingWithX == 0 && !a.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithX == 0) a.interpolateSwappingWithX = 1;
        if (b != null && b.interpolateSwappingWithX == 0 && !b.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithX == 1) b.interpolateSwappingWithX = -1;
    }

    public void cursorSwapBetweenTwoBlocksVertical(Piece cursor) {
        Block a = get(cursor.xGrid, cursor.yGrid); Block b = get(cursor.xGrid, cursor.yGrid + 1);
        if (a != null && a.interpolateSwappingWithY == 0 && !a.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithY == 0) a.interpolateSwappingWithY = 1;
        if (b != null && b.interpolateSwappingWithY == 0 && !b.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithY == 1) b.interpolateSwappingWithY = -1;
    }

    public void cursorSwapBetweenThreeBlocksHorizontal(Piece cursor, GameLogic.MovementType rotation) {
        Block a = get(cursor.xGrid - 1, cursor.yGrid); Block b = get(cursor.xGrid, cursor.yGrid); Block c = get(cursor.xGrid + 1, cursor.yGrid);
        if (rotation == GameLogic.MovementType.ROTATE_CLOCKWISE) {
            if (a != null && a.interpolateSwappingWithX == 0 && !a.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithX == 0) a.interpolateSwappingWithX = 1;
            if (b != null && b.interpolateSwappingWithX == 0 && !b.flashingToBeRemoved) if (c == null || c.interpolateSwappingWithX == -2) b.interpolateSwappingWithX = 1;
            if (c != null && c.interpolateSwappingWithX == 0 && !c.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithX == 1) c.interpolateSwappingWithX = -2;
        } else if (rotation == GameLogic.MovementType.ROTATE_COUNTERCLOCKWISE) {
            if (a != null && a.interpolateSwappingWithX == 0 && !a.flashingToBeRemoved) if (c == null || c.interpolateSwappingWithX == -1) a.interpolateSwappingWithX = 2;
            if (b != null && b.interpolateSwappingWithX == 0 && !b.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithX == 2) b.interpolateSwappingWithX = -1;
            if (c != null && c.interpolateSwappingWithX == 0 && !c.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithX == -1) c.interpolateSwappingWithX = -1;
        }
    }

    public void cursorSwapBetweenThreeBlocksVertical(Piece cursor, GameLogic.MovementType rotation) {
        Block a = get(cursor.xGrid, cursor.yGrid - 1); Block b = get(cursor.xGrid, cursor.yGrid); Block c = get(cursor.xGrid, cursor.yGrid + 1);
        if (rotation == GameLogic.MovementType.ROTATE_CLOCKWISE) {
            if (a != null && a.interpolateSwappingWithY == 0 && !a.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithY == 0) a.interpolateSwappingWithY = 1;
            if (b != null && b.interpolateSwappingWithY == 0 && !b.flashingToBeRemoved) if (c == null || c.interpolateSwappingWithY == -2) b.interpolateSwappingWithY = 1;
            if (c != null && c.interpolateSwappingWithY == 0 && !c.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithY == 1) c.interpolateSwappingWithY = -2;
        } else if (rotation == GameLogic.MovementType.ROTATE_COUNTERCLOCKWISE) {
            if (a != null && a.interpolateSwappingWithY == 0 && !a.flashingToBeRemoved) if (c == null || c.interpolateSwappingWithY == -1) a.interpolateSwappingWithY = 2;
            if (b != null && b.interpolateSwappingWithY == 0 && !b.flashingToBeRemoved) if (a == null || a.interpolateSwappingWithY == 2) b.interpolateSwappingWithY = -1;
            if (c != null && c.interpolateSwappingWithY == 0 && !c.flashingToBeRemoved) if (b == null || b.interpolateSwappingWithY == -1) c.interpolateSwappingWithY = -1;
        }
    }

    public void cursorSwapHoldingBlockWithGrid(Piece cursor) {
        Block gridBlock = get(cursor.xGrid, cursor.yGrid); if (gridBlock != null && gridBlock.flashingToBeRemoved) return;
        Block heldBlock = cursor.holdingBlock; cursor.holdingBlock = gridBlock;
        if (gridBlock != null) remove(cursor.xGrid, cursor.yGrid, false, false);
        if (heldBlock != null) add(cursor.xGrid, cursor.yGrid, heldBlock);
    }

    public void cursorRotateBlocks(Piece cursor, GameLogic.MovementType rotation) {
        Block a = get(cursor.xGrid, cursor.yGrid); Block b = get(cursor.xGrid + 1, cursor.yGrid); Block c = get(cursor.xGrid, cursor.yGrid + 1); Block d = get(cursor.xGrid + 1, cursor.yGrid + 1);
        if ((a != null && a.flashingToBeRemoved) || (b != null && b.flashingToBeRemoved) || (c != null && c.flashingToBeRemoved) || (d != null && d.flashingToBeRemoved)) return;
        if (rotation == GameLogic.MovementType.ROTATE_CLOCKWISE) {
            a = remove(cursor.xGrid, cursor.yGrid, false, false); b = remove(cursor.xGrid + 1, cursor.yGrid, false, false);
            c = remove(cursor.xGrid, cursor.yGrid + 1, false, false); d = remove(cursor.xGrid + 1, cursor.yGrid + 1, false, false);
            if (a != null) add(cursor.xGrid + 1, cursor.yGrid, a); if (b != null) add(cursor.xGrid + 1, cursor.yGrid + 1, b);
            if (c != null) add(cursor.xGrid, cursor.yGrid, c); if (d != null) add(cursor.xGrid, cursor.yGrid + 1, d);
        } else {
            a = remove(cursor.xGrid, cursor.yGrid, false, false); b = remove(cursor.xGrid + 1, cursor.yGrid, false, false);
            c = remove(cursor.xGrid, cursor.yGrid + 1, false, false); d = remove(cursor.xGrid + 1, cursor.yGrid + 1, false, false);
            if (a != null) add(cursor.xGrid, cursor.yGrid + 1, a); if (b != null) add(cursor.xGrid, cursor.yGrid, b);
            if (c != null) add(cursor.xGrid + 1, cursor.yGrid + 1, c); if (d != null) add(cursor.xGrid + 1, cursor.yGrid, d);
        }
    }

    public boolean continueSwappingBlocks() {
        boolean swappingAny = false;
        for (int y = 0; y < getHeight(); y++) for (int x = 0; x < getWidth(); x++) {
            Block a = get(x, y); if (a == null) continue;
            if (a.interpolateSwappingWithX != 0) {
                Block b = get(x + a.interpolateSwappingWithX, y); swappingAny = true;
                if (a.swapTicks < 17 * 6) { a.swapTicks += getGameLogic().ticks(); if (b != null) b.swapTicks = a.swapTicks; }
                else { a.swapTicks = 0; if (b != null) b.swapTicks = 0; remove(x, y, false, false); remove(x + a.interpolateSwappingWithX, y, false, false); if (b != null) add(x, y, b); add(x + a.interpolateSwappingWithX, y, a); a.interpolateSwappingWithX = 0; if (b != null) b.interpolateSwappingWithX = 0; }
            }
            if (a.interpolateSwappingWithY != 0) {
                Block b = get(x, y + a.interpolateSwappingWithY); swappingAny = true;
                if (a.swapTicks < 17 * 6) { a.swapTicks += getGameLogic().ticks(); if (b != null) b.swapTicks = a.swapTicks; }
                else { a.swapTicks = 0; if (b != null) b.swapTicks = 0; remove(x, y, false, false); remove(x, y + a.interpolateSwappingWithY, false, false); if (b != null) add(x, y, b); add(x, y + a.interpolateSwappingWithY, a); a.interpolateSwappingWithY = 0; if (b != null) b.interpolateSwappingWithY = 0; }
            }
        }
        return swappingAny;
    }

    public void scrollBackground() {
        scrollPlayingFieldBackgroundTicks += getGameLogic().ticks();
        if (scrollPlayingFieldBackgroundTicks > scrollPlayingFieldBackgroundTicksSpeed) {
            scrollPlayingFieldBackgroundTicks = 0;
            if (backgroundScrollX <= 0) backgroundScrollX = cellW() - 1; else backgroundScrollX--;
            if (backgroundScrollY <= 0) backgroundScrollY = cellH() - 1; else backgroundScrollY--;
        }
    }

    public void shakeSmall() { setShakePlayingField(120, 2, 2, 40); }
    public void shakeMedium() { setShakePlayingField(300, 4, 2, 60); }
    public void shakeHard() { setShakePlayingField(600, 10, 10, 60); }

    public void setShakePlayingField(int ticksDuration, int maxX, int maxY, int ticksPerShake) {
        if (shakePlayingFieldScreenTicksCounter == 0) shakePlayingFieldStartTime = System.currentTimeMillis();
        shakePlayingFieldScreenTicksCounter += ticksDuration;
        shakePlayingFieldTicksDuration = shakePlayingFieldScreenTicksCounter;
        shakePlayingFieldMaxX = maxX; shakePlayingFieldMaxY = maxY; shakePlayingFieldTicksPerShake = ticksPerShake;
    }

    public void updateShake() {
        if (shakePlayingFieldScreenTicksCounter > 0) {
            shakePlayingFieldScreenTicksCounter -= (int) getGameLogic().ticks();
            if (shakePlayingFieldScreenTicksCounter < 0) shakePlayingFieldScreenTicksCounter = 0;
            int ticksPassed = (int) (System.currentTimeMillis() - shakePlayingFieldStartTime);
            double xOver = Easing.easeInOutCircular(shakePlayingFieldTicksDuration / 2 + ticksPassed, 0, shakePlayingFieldMaxX, shakePlayingFieldTicksDuration * 2);
            double yOver = Easing.easeInOutCircular(shakePlayingFieldTicksDuration / 2 + ticksPassed, 0, shakePlayingFieldMaxY, shakePlayingFieldTicksDuration * 2);
            shakePlayingFieldTicksPerShakeXCounter += (int) getGameLogic().ticks();
            if (shakePlayingFieldTicksPerShakeXCounter > shakePlayingFieldTicksPerShake) { shakePlayingFieldTicksPerShakeXCounter = 0; shakePlayingFieldLeftRightToggle = !shakePlayingFieldLeftRightToggle; }
            shakePlayingFieldTicksPerShakeYCounter += (int) getGameLogic().ticks();
            if (shakePlayingFieldTicksPerShakeYCounter > shakePlayingFieldTicksPerShake * 2) { shakePlayingFieldTicksPerShakeYCounter = 0; shakePlayingFieldUpDownToggle = !shakePlayingFieldUpDownToggle; }
            double xThis = Easing.easeInOutCircular(shakePlayingFieldTicksPerShakeXCounter, 0, xOver, shakePlayingFieldTicksPerShake);
            double yThis = Easing.easeInOutCircular(shakePlayingFieldTicksPerShakeYCounter, 0, yOver, shakePlayingFieldTicksPerShake * 2);
            shakePlayingFieldX = (int) (shakePlayingFieldLeftRightToggle ? xThis : -xThis);
            shakePlayingFieldY = (int) (shakePlayingFieldUpDownToggle ? yThis : -yThis);
        } else { shakePlayingFieldX = 0; shakePlayingFieldY = 0; }
    }

    public void add(int x, int y, Block b) {
        if (b == null) return;
        b.xGrid = x; b.yGrid = y; b.grid = this;
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) return;
        blocks.set(y * getWidth() + x, b);
    }

    public Block get(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) return null;
        return blocks.get(y * getWidth() + x);
    }

    public boolean contains(int x, int y) { return get(x, y) != null; }

    public Block remove(int x, int y, boolean fadeOut, boolean breakConnections) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) return null;
        Block b = blocks.get(y * getWidth() + x);
        if (b == null) return null;
        blocks.set(y * getWidth() + x, null);
        if (fadeOut) { b.fadingOut = true; if (!getGameLogic().fadingOutBlocks.contains(b)) getGameLogic().fadingOutBlocks.add(b); }
        if (breakConnections) b.breakConnectionsInPiece();
        return b;
    }

    public void remove(Block b, boolean fadeOut, boolean breakConnections) {
        if (b.xGrid < 0 || b.yGrid < 0) return;
        remove(b.xGrid, b.yGrid, fadeOut, breakConnections);
    }

    public void moveToAndRemoveAndFadeOut(Block b, int x, int y) {
        b.lastScreenX = b.getScreenX(); b.lastScreenY = b.getScreenY(); b.ticksSinceLastMovement = 0;
        add(x, y, b); remove(b, true, true);
    }

    public ArrayList<Piece> getArrayOfPiecesOnGrid() {
        ArrayList<Piece> res = new ArrayList<>();
        for (Block b : blocks) if (b != null && b.piece != null && !res.contains(b.piece)) res.add(b.piece);
        return res;
    }

    public boolean doBlocksMatchColor(Block a, Block b, ArrayList<BlockType> ignore) {
        if (a == null || b == null) return false;
        if (a.interpolateSwappingWithX != 0 || b.interpolateSwappingWithX != 0 || a.flashingToBeRemoved || b.flashingToBeRemoved) return false;
        if (ignore != null && (ignore.contains(a.blockType) || ignore.contains(b.blockType))) return false;
        if (a.getColor() != null && b.getColor() != null && (a.getColor() == b.getColor() || a.getColor().name.equals(b.getColor().name))) return true;
        if (a.specialColor() != null && b.specialColor() != null && (a.specialColor() == b.specialColor() || a.specialColor().name.equals(b.specialColor().name))) return true;
        if (a.blockType.matchAnyColor || b.blockType.matchAnyColor) return true;
        return false;
    }

    public ArrayList<Block> getConnectedBlocksUpDownLeftRight(Block b) {
        ArrayList<Block> connectedBlocks = new ArrayList<>();
        int xOffset = 1;
        if (b.xGrid + xOffset < getWidth()) { Block n = get(b.xGrid + xOffset, b.yGrid); if (n != null) connectedBlocks.add(n); }
        if (b.xGrid - xOffset >= 0) { Block n = get(b.xGrid - xOffset, b.yGrid); if (n != null) connectedBlocks.add(n); }
        int yOffset = 1;
        if (b.yGrid + yOffset < getHeight()) { Block n = get(b.xGrid, b.yGrid + yOffset); if (n != null) connectedBlocks.add(n); }
        if (b.yGrid - yOffset >= 0) { Block n = get(b.xGrid, b.yGrid - yOffset); if (n != null) connectedBlocks.add(n); }
        return connectedBlocks;
    }

    public void setColorConnections(ArrayList<BlockType> ignoreTypes) {
        for (Block b : blocks) if (b != null) b.connectedBlocksByColor.clear();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                ArrayList<Block> connectedBlocksByColorList = new ArrayList<>();
                Block b = get(x, y);
                if (b != null && (ignoreTypes.isEmpty() || !ignoreTypes.contains(b.blockType))) {
                    if (!b.connectedBlocksByColor.isEmpty()) continue;
                    recursivelyGetAllMatchingBlocksConnectedToBlockToArrayIfNotInItAlready(b, connectedBlocksByColorList, ignoreTypes);
                    for (Block c : connectedBlocksByColorList) if (b != c && !b.connectedBlocksByColor.contains(c)) b.connectedBlocksByColor.add(c);
                }
            }
        }
    }

    private void recursivelyGetAllMatchingBlocksConnectedToBlockToArrayIfNotInItAlready(Block b, ArrayList<Block> arr, ArrayList<BlockType> ignore) {
        if (arr.contains(b)) return; arr.add(b);
        for (Block n : getConnectedBlocksUpDownLeftRight(b)) if (doBlocksMatchColor(b, n, ignore)) recursivelyGetAllMatchingBlocksConnectedToBlockToArrayIfNotInItAlready(n, arr, ignore);
    }

    public void addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(Block b, ArrayList<Block> connectedBlocks, int leastInARow, int startX, int endX, int startY, int endY, ArrayList<BlockType> ignoreTypes, ArrayList<BlockType> mustContainAtLeastOneTypes) {
        ArrayList<Block> row = new ArrayList<>(); row.add(b);
        for (int xOffset = 1; b.xGrid + xOffset < endX; xOffset++) { Block n = get(b.xGrid + xOffset, b.yGrid); if (doBlocksMatchColor(b, n, ignoreTypes)) row.add(n); else break; }
        for (int xOffset = 1; b.xGrid - xOffset >= startX; xOffset++) { Block n = get(b.xGrid - xOffset, b.yGrid); if (doBlocksMatchColor(b, n, ignoreTypes)) row.add(n); else break; }
        if (row.size() >= leastInARow) {
            if (!mustContainAtLeastOneTypes.isEmpty()) {
                boolean ok = false; for (Block r : row) if (mustContainAtLeastOneTypes.contains(r.blockType)) { ok = true; break; }
                if (!ok) row.clear();
            }
            for (Block c : row) if (!connectedBlocks.contains(c)) connectedBlocks.add(c);
        }
    }

    public void addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(Block b, ArrayList<Block> connectedBlocks, int leastInARow, int startX, int endX, int startY, int endY, ArrayList<BlockType> ignoreTypes, ArrayList<BlockType> mustContainAtLeastOneTypes) {
        ArrayList<Block> column = new ArrayList<>(); column.add(b);
        for (int yOffset = 1; b.yGrid + yOffset < endY; yOffset++) { Block n = get(b.xGrid, b.yGrid + yOffset); if (doBlocksMatchColor(b, n, ignoreTypes)) column.add(n); else break; }
        for (int yOffset = 1; b.yGrid - yOffset >= startY; yOffset++) { Block n = get(b.xGrid, b.yGrid - yOffset); if (doBlocksMatchColor(b, n, ignoreTypes)) column.add(n); else break; }
        if (column.size() >= leastInARow) {
            if (!mustContainAtLeastOneTypes.isEmpty()) {
                boolean ok = false; for (Block c : column) if (mustContainAtLeastOneTypes.contains(c.blockType)) { ok = true; break; }
                if (!ok) column.clear();
            }
            for (Block c : column) if (!connectedBlocks.contains(c)) connectedBlocks.add(c);
        }
    }

    public ArrayList<Block> checkLines(ArrayList<BlockType> ignore, ArrayList<BlockType> mustContain) {
        ArrayList<Block> result = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            ArrayList<Block> row = new ArrayList<>();
            boolean full = true;
            for (int x = 0; x < getWidth(); x++) {
                Block b = get(x, y);
                if (b == null || (ignore != null && ignore.contains(b.blockType))) { full = false; break; }
                row.add(b);
            }
            if (full) {
                if (mustContain != null && !mustContain.isEmpty()) {
                    boolean ok = false; for (Block b : row) if (mustContain.contains(b.blockType)) { ok = true; break; }
                    if (!ok) continue;
                }
                for (Block b : row) if (!result.contains(b)) result.add(b);
            }
        }
        return result;
    }

    public void addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfDiagonalAtLeastAmount(Block b, ArrayList<Block> connectedBlocks, int leastInARow, int startX, int endX, int startY, int endY, ArrayList<BlockType> ignoreTypes, ArrayList<BlockType> mustContainAtLeastOneTypes) {
        // Diagonal \
        ArrayList<Block> diag1 = new ArrayList<>(); diag1.add(b);
        for (int i = 1; b.xGrid + i < endX && b.yGrid + i < endY; i++) { Block n = get(b.xGrid + i, b.yGrid + i); if (doBlocksMatchColor(b, n, ignoreTypes)) diag1.add(n); else break; }
        for (int i = 1; b.xGrid - i >= startX && b.yGrid - i >= startY; i++) { Block n = get(b.xGrid - i, b.yGrid - i); if (doBlocksMatchColor(b, n, ignoreTypes)) diag1.add(n); else break; }
        if (diag1.size() >= leastInARow) {
            if (!mustContainAtLeastOneTypes.isEmpty()) {
                boolean ok = false; for (Block d : diag1) if (mustContainAtLeastOneTypes.contains(d.blockType)) { ok = true; break; }
                if (!ok) diag1.clear();
            }
            for (Block c : diag1) if (!connectedBlocks.contains(c)) connectedBlocks.add(c);
        }
        // Diagonal /
        ArrayList<Block> diag2 = new ArrayList<>(); diag2.add(b);
        for (int i = 1; b.xGrid + i < endX && b.yGrid - i >= startY; i++) { Block n = get(b.xGrid + i, b.yGrid - i); if (doBlocksMatchColor(b, n, ignoreTypes)) diag2.add(n); else break; }
        for (int i = 1; b.xGrid - i >= startX && b.yGrid + i < endY; i++) { Block n = get(b.xGrid - i, b.yGrid + i); if (doBlocksMatchColor(b, n, ignoreTypes)) diag2.add(n); else break; }
        if (diag2.size() >= leastInARow) {
            if (!mustContainAtLeastOneTypes.isEmpty()) {
                boolean ok = false; for (Block d : diag2) if (mustContainAtLeastOneTypes.contains(d.blockType)) { ok = true; break; }
                if (!ok) diag2.clear();
            }
            for (Block c : diag2) if (!connectedBlocks.contains(c)) connectedBlocks.add(c);
        }
    }

    public ArrayList<Block> checkBreakerBlocks(int toRow, ArrayList<BlockType> ignore, ArrayList<BlockType> breakers) {
        ArrayList<Block> result = new ArrayList<>();
        for (int y = 0; y < toRow; y++) {
            for (int x = 0; x < getWidth(); x++) {
                Block b = get(x, y);
                if (b != null && breakers.contains(b.blockType)) {
                    ArrayList<Block> connected = new ArrayList<>();
                    addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(b, connected, 2, 0, getWidth(), 0, getHeight(), ignore, breakers);
                    addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(b, connected, 2, 0, getWidth(), 0, getHeight(), ignore, breakers);
                    if (connected.size() > 0) {
                        int size = connected.size();
                        for (int i = 0; i < size; i++) {
                            addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(connected.get(i), connected, 2, 0, getWidth(), 0, getHeight(), ignore, new ArrayList<>());
                            addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(connected.get(i), connected, 2, 0, getWidth(), 0, getHeight(), ignore, new ArrayList<>());
                            if (connected.size() > size) { size = connected.size(); i = -1; }
                        }
                        if (connected.size() >= 2) {
                            for (Block c : connected) if (!result.contains(c)) result.add(c);
                            for (Block s : getConnectedBlocksUpDownLeftRight(b)) if (ignore.contains(s.blockType)) if (!result.contains(s)) result.add(s);
                        }
                    }
                }
            }
        }
        return result;
    }

    public void checkRecursiveConnectedRowOrColumn(ArrayList<Block> connectedBlocks, int leastAmountConnected, int startX, int endX, int startY, int endY, ArrayList<BlockType> ignoreTypes, ArrayList<BlockType> mustContainAtLeastOneTypes) {
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                ArrayList<Block> connectedToThisBlock = new ArrayList<>();
                Block b = get(x, y);
                if (b != null && !ignoreTypes.contains(b.blockType)) {
                    addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(b, connectedToThisBlock, 2, startX, endX, startY, endY, ignoreTypes, mustContainAtLeastOneTypes);
                    addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(b, connectedToThisBlock, 2, startX, endX, startY, endY, ignoreTypes, mustContainAtLeastOneTypes);
                    if (!connectedToThisBlock.isEmpty()) {
                        int size = connectedToThisBlock.size();
                        for (int i = 0; i < size; i++) {
                            addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(connectedToThisBlock.get(i), connectedToThisBlock, 2, startX, endX, startY, endY, ignoreTypes, mustContainAtLeastOneTypes);
                            addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(connectedToThisBlock.get(i), connectedToThisBlock, 2, startX, endX, startY, endY, ignoreTypes, mustContainAtLeastOneTypes);
                            if (connectedToThisBlock.size() > size) { size = connectedToThisBlock.size(); i = -1; }
                        }
                        if (connectedToThisBlock.size() >= leastAmountConnected) {
                            for (Block c : connectedToThisBlock) if (!connectedBlocks.contains(c)) connectedBlocks.add(c);
                        }
                    }
                }
            }
        }
    }

    public void setPiece(Piece p, int x, int y) {
        if (p.pieceType != null && p.pieceType.fadeOutOnceSetInsteadOfAddedToGrid) {
            for (Block b : p.blocks) { b.fadingOut = true; if (!getGameLogic().fadingOutBlocks.contains(b)) getGameLogic().fadingOutBlocks.add(b); }
            return;
        }
        for (Block b : p.blocks) { add(x + b.xInPiece, y + b.yInPiece, b); b.setInGrid = true; b.locking = true; }
        p.setInGrid = true;
    }

    public void setPiece(Piece p) { setPiece(p, p.xGrid, p.yGrid); }

    public boolean doesPieceFit(Piece p, int x, int y) {
        for (Block b : p.blocks) {
            int tx = x + b.xInPiece; int ty = y + b.yInPiece;
            if (tx < 0 || tx >= getWidth() || ty >= getHeight()) return false;
            if (ty >= 0 && get(tx, ty) != null) return false;
        }
        return true;
    }

    public Piece getRandomPiece(ArrayList<PieceType> pt, ArrayList<BlockType> bt) {
        Piece p = new Piece(game, this, getRandomPieceType(pt), bt); p.init(); return p;
    }

    public PieceType getRandomPieceType(ArrayList<PieceType> pt) {
        return pt.get(getGameLogic().getRandomIntLessThan(pt.size(), "getRandomPieceType"));
    }

    public BlockType getRandomBlockType(ArrayList<BlockType> bt) {
        return bt.get(getGameLogic().getRandomIntLessThan(bt.size(), "getRandomBlockType"));
    }

    public BlockType getRandomBlockTypeDisregardingSpecialFrequency(ArrayList<BlockType> bt) { return getRandomBlockType(bt); }
    public BlockType getRandomBlockTypeFromArrayExcludingSpecialBlockTypes(ArrayList<BlockType> bt) {
        ArrayList<BlockType> res = new ArrayList<>();
        for (BlockType b : bt) if (!b.isSpecialType()) res.add(b);
        return res.isEmpty() ? bt.get(0) : res.get(getGameLogic().getRandomIntLessThan(res.size(), "getRandomBlockTypeExcludingSpecial"));
    }

    public boolean areAnyBlocksPopping() {
        for (Block b : blocks) if (b != null && b.popping) return true;
        return false;
    }

    public void render() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Block b = get(x, y);
                if (b != null) {
                    b.render(getXInFBO() + x * cellW(), getYInFBO() + (scrollPlayingFieldY / scrollBlockIncrement) * cellH() + y * cellH(), 1.0f, 1.0f, true, false);
                }
            }
        }
    }

    public void renderBackground() {
        float alpha = 0.85f;
        int h = getHeight();
        if (getGameType().gameMode == GameType.GameMode.STACK) h--;

        for (int x = -1; x < getWidth(); x++) {
            for (int y = -1; y < h; y++) {
                BobColor color = (y % 2 == 0) ? (x % 2 == 0 ? BobColor.darkGray : BobColor.gray) : (x % 2 == 0 ? BobColor.gray : BobColor.darkGray);
                float fbgX = bgX() + (x * cellW());
                float fbgY = bgY() + (y * cellH());
                GLUtils.drawFilledRectXYWH(fbgX, fbgY, (float) cellW(), (float) cellH(), color.rf(), color.gf(), color.bf(), alpha);
            }
        }

        // Danger zone
        float yZone = getYInFBO();
        float hZone = (float) cellH() * GameLogic.aboveGridBuffer;
        GLUtils.drawFilledRectXYWH(getXInFBO(), yZone, (float) cellW() * getWidth(), hZone, 0.2f, 0.2f, 0.2f, alpha);
        GLUtils.drawFilledRectXYWH(getXInFBO(), yZone + hZone - 1, (float) cellW() * getWidth(), 1, 0.7f, 0.7f, 0.7f, alpha);
    }

    public void renderBorder() {
        float x0 = getXInFBO();
        float y0 = getYInFBO();
        float w = getWidth() * cellW();
        float h = getHeight() * cellH();
        if (getGameType().gameMode == GameType.GameMode.STACK) h -= cellH();
        
        GLUtils.drawBox(x0 - 1, x0 + w + 1, y0 - 1, y0 + h + 1, 255, 255, 255);
    }

    public void renderBlockOutlines() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Block b = get(x, y);
                if (b != null) {
                    b.renderOutlines(getXInFBO() + x * cellW(), getYInFBO() + (scrollPlayingFieldY / scrollBlockIncrement) * cellH() + y * cellH(), 1.0f);
                }
            }
        }
    }

    public void renderTransparentOverLastRow() {
        float x = getXInFBO();
        float y = getYInFBO() + (getHeight() - 2) * cellH() + cellH() / 2f;
        float w = (float) getWidth() * cellW();
        float h = (float) ((getYInFBO() + getHeight() * cellH()) - y);

        int div = 16;
        for (int i = 0; i < div; i++) {
            float alpha = (float) i / (div - 1);
            GLUtils.drawFilledRectXYWH(x, y + (h / div) * i, w, h / div, 0, 0, 0, alpha);
        }
        renderBorder();
    }

    public void renderGhostPiece(Piece currentPiece) {
        int ghostY = currentPiece.yGrid;
        for (int y = ghostY; y < getHeight(); y++) {
            if (doesPieceFit(currentPiece, currentPiece.xGrid, y)) ghostY = y;
            else break;
        }
        if (ghostY != currentPiece.yGrid) {
            float x = getXInFBO() + currentPiece.xGrid * cellW();
            float y = getYInFBO() + ghostY * cellH();
            float alpha = (float) (ghostY - currentPiece.yGrid) / (getHeight() * 0.6f);
            if (alpha > 1) alpha = 1;
            currentPiece.renderGhost(x, y, alpha);
        }
    }

    public int deadX = 0;
    public int deadY = 0;

    public boolean isWithinBounds(Piece piece, int x, int y) {
        for (int i = 0; i < piece.getNumBlocksInCurrentRotation() && i < piece.blocks.size(); i++) {
            Block b = piece.blocks.get(i);
            if (x + b.xInPiece >= getWidth() || x + b.xInPiece < 0 || y + b.yInPiece >= getHeight()) {
                return false;
            }
        }
        return true;
    }

    public boolean isHittingLeft(Piece piece, int x, int y) {
        if (x < 0) return true;
        for (int i = 0; i < piece.getNumBlocksInCurrentRotation() && i < piece.blocks.size(); i++) {
            Block b = piece.blocks.get(i);
            if (x + b.xInPiece < 0) return true;
        }
        for (int i = 0; i < piece.getNumBlocksInCurrentRotation() && i < piece.blocks.size(); i++) {
            Block b = piece.blocks.get(i);
            Block gridBlock = get(x + b.xInPiece, y + b.yInPiece);
            if (x + b.xInPiece < x && gridBlock != null) return true;
        }
        return false;
    }

    public boolean isHittingRight(Piece piece, int x, int y) {
        if (x >= getWidth()) return true;
        for (int i = 0; i < piece.getNumBlocksInCurrentRotation() && i < piece.blocks.size(); i++) {
            Block b = piece.blocks.get(i);
            if (x + b.xInPiece >= getWidth()) return true;
        }
        for (int i = 0; i < piece.getNumBlocksInCurrentRotation() && i < piece.blocks.size(); i++) {
            Block b = piece.blocks.get(i);
            Block gridBlock = get(x + b.xInPiece, y + b.yInPiece);
            if (x + b.xInPiece > x && gridBlock != null) return true;
        }
        return false;
    }

    public void doDeathSequence() {
        if (deadX < getWidth()) {
            Piece p = getRandomPiece(getGameType().getNormalPieceTypes(getGameLogic().getCurrentDifficulty()), getGameType().getNormalBlockTypes(getGameLogic().getCurrentDifficulty()));
            for (Block b : p.blocks) {
                b.lastScreenX = getXInFBO() + (deadX + b.xInPiece) * cellW();
                b.lastScreenY = getYInFBO() + (deadY + b.yInPiece) * cellH() + (scrollPlayingFieldY / scrollBlockIncrement) * cellH();
            }
            Block d = get(deadX, deadY);
            if (d != null) removeAllBlocksOfPieceFromGrid(d.piece, true);

            if (doesPieceFit(p, deadX, deadY) && deadY + p.getLowestOffsetY() > 2) {
                setPiece(p, deadX, deadY);
                deadX += p.getWidth();
                deadY -= 1;
            } else {
                deadX += getGameLogic().getRandomIntLessThan(3, "doDeathSequence");
                deadY -= getGameLogic().getRandomIntLessThan(3, "doDeathSequence");
            }
            if (deadY < 0) deadY = getHeight() - 1;
            if (deadX >= getWidth()) deadX = 0;
        }
    }

    public BlockType getRandomSpecialBlockTypeFromArrayExcludingNormalBlocksOrNull(ArrayList<BlockType> arr) {
        ArrayList<BlockType> bag = new ArrayList<>();
        for (BlockType b : arr) {
            if (b.frequencySpecialBlockTypeOnceEveryNPieces != 0) {
                if (getGameLogic().createdPiecesCounterForFrequencyPieces >= b.frequencySpecialBlockTypeOnceEveryNPieces - 1) {
                    bag.add(b);
                }
            }
        }
        if (!bag.isEmpty()) {
            getGameLogic().createdPiecesCounterForFrequencyPieces = 0;
            return bag.get(getGameLogic().getRandomIntLessThan(bag.size(), "getRandomSpecialBlockType"));
        }
        for (BlockType b : arr) {
            if (b.randomSpecialBlockChanceOneOutOf > 0) {
                if (getGameLogic().getRandomIntLessThan(b.randomSpecialBlockChanceOneOutOf, "getRandomSpecialBlockType") == 0) {
                    bag.add(b);
                }
            }
        }
        if (!bag.isEmpty()) return bag.get(getGameLogic().getRandomIntLessThan(bag.size(), "getRandomSpecialBlockType"));
        return null;
    }

    public PieceType getRandomSpecialPieceTypeFromArrayExcludingNormalPiecesOrNull(ArrayList<PieceType> pieceTypes) {
        ArrayList<PieceType> bag = new ArrayList<>();
        for (PieceType p : pieceTypes) {
            if (p.frequencySpecialPieceTypeOnceEveryNPieces != 0) {
                if (getGameLogic().createdPiecesCounterForFrequencyPieces >= p.frequencySpecialPieceTypeOnceEveryNPieces) {
                    bag.add(p);
                }
            }
        }
        if (!bag.isEmpty()) {
            getGameLogic().createdPiecesCounterForFrequencyPieces = 0;
            return bag.get(getGameLogic().getRandomIntLessThan(bag.size(), "getRandomSpecialPieceType"));
        }
        for (PieceType p : pieceTypes) {
            if (p.randomSpecialPieceChanceOneOutOf > 0) {
                if (getGameLogic().getRandomIntLessThan(p.randomSpecialPieceChanceOneOutOf, "getRandomSpecialPieceType") == 0) {
                    bag.add(p);
                }
            }
        }
        if (!bag.isEmpty()) return bag.get(getGameLogic().getRandomIntLessThan(bag.size(), "getRandomSpecialPieceType"));
        return null;
    }

    public ArrayList<Piece> getBagOfOneOfEachNonRandomNormalPieces() {
        ArrayList<PieceType> pt = getGameType().getNormalPieceTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<BlockType> bt = getGameType().getNormalBlockTypes(getGameLogic().getCurrentDifficulty());
        ArrayList<Piece> tempBag = new ArrayList<>();
        for (PieceType type : pt) {
            if (type.randomSpecialPieceChanceOneOutOf == 0 && type.frequencySpecialPieceTypeOnceEveryNPieces == 0) {
                Piece p = new Piece(getGameLogic(), this, type, bt.get(0));
                p.init();
                tempBag.add(p);
            }
        }
        return tempBag;
    }

    public Piece getPieceFromNormalPieceRandomBag() {
        if (randomBag.isEmpty()) {
            ArrayList<Piece> tempBag = getBagOfOneOfEachNonRandomNormalPieces();
            while (!tempBag.isEmpty()) {
                int i = getGameLogic().getRandomIntLessThan(tempBag.size(), "getPieceFromBag");
                if (randomBag.isEmpty()) {
                    boolean anyAllowed = false;
                    for (Piece p : tempBag) if (!p.pieceType.disallowAsFirstPiece) anyAllowed = true;
                    if (anyAllowed) while (tempBag.get(i).pieceType.disallowAsFirstPiece) i = getGameLogic().getRandomIntLessThan(tempBag.size(), "getPieceFromBag");
                }
                randomBag.add(tempBag.remove(i));
            }
        }
        return randomBag.remove(0);
    }

    public int cellW() { return getGameLogic().cellW(); }
    public int cellH() { return getGameLogic().cellH(); }
    public GameType getGameType() { return getGameLogic().currentGameType; }
    public GameLogic getGameLogic() { return game; }
}
