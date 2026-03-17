package com.bobsgame.puzzle;

import com.bobsgame.client.BobsGame;
import com.bobsgame.shared.BobColor;
import com.bobsgame.puzzle.GameType.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLogic {
    public static final Logger log = LoggerFactory.getLogger(GameLogic.class);

    public String uuid;
    public PuzzlePlayer player;
    public GameSequence currentGameSequence;
    public GameType currentGameType;
    public Grid grid;

    public GameState state = GameState.IDLE;

    public int blockWidth = 1;
    public int blockHeight = 1;
    public static final int aboveGridBuffer = 5;

    public long lockInputCountdownTicks = 0;
    
    public long lockDelayTicksCounter = 0;
    public long lineDropTicksCounter = 0;
    public long spawnDelayTicksCounter = 0;
    public long lineClearDelayTicksCounter = 0;
    public long moveDownLineTicksCounter = 0;
    public long removeBlocksTicksCounter = 0;

    public boolean won = false;
    public boolean lost = false;
    public boolean died = false;
    public boolean dead = false;
    public boolean complete = false;
    public boolean didInit = false;
    public boolean firstInit = true;

    public boolean pieceSetAtBottom = false;
    public boolean switchedHoldPieceAlready = false;
    public boolean playingFastMusic = false;

    public float gameSpeed = 0.0f;
    public int currentLineDropSpeedTicks = 0;
    public int currentStackRiseSpeedTicks = 0;

    public long stackRiseTicksCounter = 0;
    public long stopStackRiseTicksCounter = 0;
    public long manualStackRiseTicksCounter = 0;

    public int timesToFlashBlocksQueue = 0;
    public long flashBlocksTicksCounter = 0;
    public int timesToFlashBlocks = 20;
    public int flashBlockSpeedTicks = 30;

    public ArrayList<Block> currentChainBlocks = new ArrayList<>();
    public ArrayList<Block> fadingOutBlocks = new ArrayList<>();

    public Piece currentPiece = null;
    public Piece lastPiece = null;
    public Piece holdPiece = null;
    public ArrayList<Piece> nextPieces = new ArrayList<>();

    public int currentLevel = 0;
    public int lastKnownLevel = 0;

    public int piecesMadeThisGame = 0;
    public int lastPiecesMadeThisGame = 0;
    public int blocksClearedThisGame = 0;
    public int linesClearedThisGame = 0;

    public int piecesMadeThisLevel = 0;
    public int blocksClearedThisLevel = 0;
    public int linesClearedThisLevel = 0;

    public int piecesPlacedTotal = 0;
    public int blocksClearedTotal = 0;
    public int linesClearedTotal = 0;

    public long score = 0;

    public long totalTicksPassed = 0;
    public long timeStarted = 0;
    public long timeEnded = 0;

    public int currentChain = 0;
    public int currentCombo = 0;
    public int comboChainTotal = 0;
    public int biggestComboChain = 0;
    public int totalCombosMade = 0;

    public int queuedVSGarbageAmountToSend = 0;
    public int queuedVSGarbageAmountFromOtherPlayer = 0;
    public int garbageWaitForPiecesSetCount = 0;
    public long playingFieldGarbageValueCounter = 0;

    public boolean checkForChainAgainIfNoBlocksPopping = false;

    public FrameState frameState = new FrameState();
    public boolean isNetworkPlayer = false;

    public int lastSentGarbageToPlayerIndex = 0;

    public GameManager manager;
    private Random random;
    public long randomSeed;

    private ArrayList<GameLogicListener> listeners = new ArrayList<>();
    public void addListener(GameLogicListener l) { listeners.add(l); }

    public GameLogic(GameManager manager, long seed) {
        this.uuid = UUID.randomUUID().toString();
        this.manager = manager;
        this.grid = new Grid(this);
        this.currentGameType = new GameType();
        this.randomSeed = seed;
        this.random = new Random(seed);
    }

    public long ticks() { return frameState != null ? frameState.ticksPassed : 0; }

    public DifficultyType getCurrentDifficulty() {
        if (currentGameSequence != null && !currentGameSequence.gameTypes.isEmpty()) {
            return currentGameSequence.gameTypes.get(0).getDifficultyByName(currentGameSequence.currentDifficultyName);
        }
        return GameType.difficulty_NORMAL;
    }

    public void update(int gameIndex, int numGames) {
        if (!isNetworkPlayer) {
            frameState = new FrameState();
            frameState.ticksPassed = 16; // Simulate 60fps
            setControlsState();
            if (!didInit) initGame();
            processFrame();
        }

        if (!getRoom().multiplayer_DisableVSGarbage) {
            ArrayList<GameLogic> otherPlayers = new ArrayList<>();
            if (manager != null && manager.getGames() != null) {
                for (GameLogic g2 : manager.getGames()) {
                    if (g2 != this) otherPlayers.add(g2);
                }
            }
            Collections.sort(otherPlayers, Comparator.comparing(a -> a.uuid));

            if (isNetworkGame()) {
                if (getRoom().multiplayer_SendGarbageTo != SendGarbageToRule.SEND_GARBAGE_TO_ALL_PLAYERS) {
                    getRoom().multiplayer_SendGarbageTo = SendGarbageToRule.SEND_GARBAGE_TO_ALL_PLAYERS;
                }
            } else {
                ArrayList<GameLogic> alivePlayers = new ArrayList<>();
                for (GameLogic g2 : otherPlayers) {
                    if (!g2.won && !g2.died && !g2.lost && !g2.complete) alivePlayers.add(g2);
                }

                if (!alivePlayers.isEmpty()) {
                    if (getRoom().multiplayer_SendGarbageTo == SendGarbageToRule.SEND_GARBAGE_TO_EACH_PLAYER_IN_ROTATION) {
                        if (queuedVSGarbageAmountToSend > 0) {
                            lastSentGarbageToPlayerIndex++;
                            if (lastSentGarbageToPlayerIndex >= alivePlayers.size()) lastSentGarbageToPlayerIndex = 0;
                            GameLogic g2 = alivePlayers.get(lastSentGarbageToPlayerIndex);
                            g2.gotVSGarbageFromOtherPlayer(queuedVSGarbageAmountToSend);
                            g2.frameState.receivedGarbageAmount += queuedVSGarbageAmountToSend;
                            queuedVSGarbageAmountToSend = 0;
                        }
                    }
                    if (getRoom().multiplayer_SendGarbageTo == SendGarbageToRule.SEND_GARBAGE_TO_PLAYER_WITH_LEAST_BLOCKS) {
                        if (queuedVSGarbageAmountToSend > 0) {
                            GameLogic leastBlocksPlayer = alivePlayers.get(0);
                            int leastBlocks = alivePlayers.get(0).grid.getNumberOfFilledCells();
                            for (GameLogic g2 : alivePlayers) {
                                if (g2.grid.getNumberOfFilledCells() < leastBlocks) {
                                    leastBlocks = g2.grid.getNumberOfFilledCells();
                                    leastBlocksPlayer = g2;
                                }
                            }
                            leastBlocksPlayer.gotVSGarbageFromOtherPlayer(queuedVSGarbageAmountToSend);
                            leastBlocksPlayer.frameState.receivedGarbageAmount += queuedVSGarbageAmountToSend;
                            queuedVSGarbageAmountToSend = 0;
                        }
                    }
                    if (getRoom().multiplayer_SendGarbageTo == SendGarbageToRule.SEND_GARBAGE_TO_RANDOM_PLAYER) {
                        if (queuedVSGarbageAmountToSend > 0) {
                            GameLogic g2 = alivePlayers.get(random.nextInt(alivePlayers.size()));
                            g2.gotVSGarbageFromOtherPlayer(queuedVSGarbageAmountToSend);
                            g2.frameState.receivedGarbageAmount += queuedVSGarbageAmountToSend;
                            queuedVSGarbageAmountToSend = 0;
                        }
                    }
                }
            }

            if (getRoom().multiplayer_SendGarbageTo == SendGarbageToRule.SEND_GARBAGE_TO_ALL_PLAYERS) {
                if (!isNetworkGame()) {
                    if (queuedVSGarbageAmountToSend > 0) {
                        for (GameLogic g2 : otherPlayers) {
                            g2.gotVSGarbageFromOtherPlayer(queuedVSGarbageAmountToSend);
                            g2.frameState.receivedGarbageAmount += queuedVSGarbageAmountToSend;
                        }
                        queuedVSGarbageAmountToSend = 0;
                    }
                } else {
                    for (GameLogic g2 : otherPlayers) {
                        if (g2.queuedVSGarbageAmountToSend > 0) {
                            gotVSGarbageFromOtherPlayer(g2.queuedVSGarbageAmountToSend);
                            frameState.receivedGarbageAmount += g2.queuedVSGarbageAmountToSend;
                            g2.queuedVSGarbageAmountToSend = 0;
                        }
                    }
                }
            }

            if (getRoom().multiplayer_SendGarbageTo == SendGarbageToRule.SEND_GARBAGE_TO_ALL_PLAYERS_50_PERCENT_CHANCE) {
                if (!isNetworkGame()) {
                    if (queuedVSGarbageAmountToSend > 0) {
                        for (GameLogic g2 : otherPlayers) {
                            if (random.nextInt(2) == 0) {
                                g2.gotVSGarbageFromOtherPlayer(queuedVSGarbageAmountToSend);
                                g2.frameState.receivedGarbageAmount += queuedVSGarbageAmountToSend;
                            }
                        }
                        queuedVSGarbageAmountToSend = 0;
                    }
                } else {
                    for (GameLogic g2 : otherPlayers) {
                        if (g2.queuedVSGarbageAmountToSend > 0) {
                            if (random.nextInt(2) == 0) {
                                gotVSGarbageFromOtherPlayer(g2.queuedVSGarbageAmountToSend);
                                frameState.receivedGarbageAmount += g2.queuedVSGarbageAmountToSend;
                            }
                            g2.queuedVSGarbageAmountToSend = 0;
                        }
                    }
                }
            }
        }
    }

    private void setControlsState() {
        if (player == null) return;
        frameState.ROTATECW_HELD = player.ROTATECW_HELD;
        frameState.HOLDRAISE_HELD = player.HOLDRAISE_HELD;
        frameState.ROTATECCW_HELD = player.ROTATECCW_HELD;
        frameState.UP_HELD = player.UP_HELD;
        frameState.LEFT_HELD = player.LEFT_HELD;
        frameState.DOWN_HELD = player.DOWN_HELD;
        frameState.RIGHT_HELD = player.RIGHT_HELD;
        frameState.SLAM_HELD = player.SLAM_HELD;
        frameState.slamLock = player.slamLock;
        frameState.singleDownLock = player.singleDownLock;
        frameState.doubleDownLock = player.doubleDownLock;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void initGame() {
        if (firstInit) {
            firstInit = false;
            timeStarted = System.currentTimeMillis();
            gameSpeed = getRoom().gameSpeedStart;
        }
        didInit = true;
        resetNextPieces();
        grid.reformat(gridW(), gridH());
        manuallyApplyGravityWithoutChainChecking();
        grid.replaceAllBlocksWithNewGameBlocks();
        manuallyApplyGravityWithoutChainChecking();
        lockDelayTicksCounter = currentGameType.maxLockDelayTicks;
        currentLineDropSpeedTicks = getCurrentDifficulty().initialLineDropSpeedTicks;
        currentStackRiseSpeedTicks = getCurrentDifficulty().maxStackRise;
        stopStackRiseTicksCounter = 1000;
        if (currentGameType.gameMode == GameMode.DROP) {
            if (getCurrentDifficulty().randomlyFillGrid) grid.randomlyFillGridWithPlayingFieldPieces(getCurrentDifficulty().randomlyFillGridAmount, getCurrentDifficulty().randomlyFillGridStartY);
            newRandomPiece();
        } else if (currentGameType.gameMode == GameMode.STACK) {
            if (getCurrentDifficulty().randomlyFillGrid) grid.buildRandomStackRetainingExistingBlocks(getCurrentDifficulty().randomlyFillGridAmount, getCurrentDifficulty().randomlyFillGridStartY);
            currentPiece = grid.getRandomPiece();
            currentPiece.xGrid = grid.getWidth() / 2; currentPiece.yGrid = 7 + aboveGridBuffer;
        }
        setState(GameState.READY);
    }

    public void start() { setState(GameState.PLAYING); }
    public void pause() { setState(GameState.PAUSED); }
    public void resume() { setState(GameState.PLAYING); }

    private void processFrame() {
        if (won || lost || complete || died) {
            if (timeEnded == 0) {
                timeEnded = System.currentTimeMillis();
            }
        }
        if (won || lost || complete || died) return;
        if (state == GameState.PAUSED) return;

        totalTicksPassed += ticks();
        updateSpecialPiecesAndBlocks();
        processQueuedGarbageSentFromOtherPlayer();
        processGarbageRules();
        grid.update();
        lockDelayTicksCounter = Math.max(0, lockDelayTicksCounter - ticks());
        lineDropTicksCounter = Math.max(0, lineDropTicksCounter - ticks());
        lineClearDelayTicksCounter = Math.max(0, lineClearDelayTicksCounter - ticks());
        spawnDelayTicksCounter = Math.max(0, spawnDelayTicksCounter - ticks());
        if (currentGameType.gameMode == GameMode.STACK) doStackRiseGame();
        else if (currentGameType.gameMode == GameMode.DROP) doFallingBlockGame();
        moveDownLineTicksCounter += ticks();
        if (pieceSetAtBottom && !detectedChain()) {
            if (checkForChainAgainIfNoBlocksPopping) { if (grid.areAnyBlocksPopping()) return; else checkForChainAgainIfNoBlocksPopping = false; }
            boolean movedDownBlocks = moveDownBlocksOverBlankSpaces();
            if (!movedDownBlocks) {
                checkForChain(); handleNewChain(); checkForFastMusic();
                if (!detectedChain() && !checkForChainAgainIfNoBlocksPopping) {
                    currentCombo = 0; currentChain = 0; comboChainTotal = 0;
                    if (currentGameType.gameMode == GameMode.DROP && pieceSetAtBottom) newRandomPiece();
                    updateScore();
                }
            }
        }
    }

    private void doStackRiseGame() {
        pieceSetAtBottom = true; manualStackRiseTicksCounter += ticks();
        boolean stop = false;
        if (stopStackRiseTicksCounter > 0) { stopStackRiseTicksCounter = Math.max(0, stopStackRiseTicksCounter - ticks()); stop = true; }
        if (timesToFlashBlocksQueue > 0) { flashChainBlocks(); stop = true; }
        else if (detectedChain()) { removeFlashedChainBlocks(); stop = true; }
        if (grid.continueSwappingBlocks()) stop = true;
        if (!stop) {
            stackRiseTicksCounter += ticks();
            if (stackRiseTicksCounter > currentStackRiseSpeedTicks) {
                stackRiseTicksCounter = 0;
                if (!grid.scrollUpStack(currentPiece, 1)) {
                    died = true;
                    setState(GameState.GAME_OVER);
                }
            }
        }
        updateKeyInput();
    }

    private void doFallingBlockGame() {
        if (timesToFlashBlocksQueue > 0) { flashChainBlocks(); return; }
        if (detectedChain()) { removeFlashedChainBlocks(); return; }
        if (!pieceSetAtBottom) {
            if (lineDropTicksCounter == 0 && spawnDelayTicksCounter == 0 && lineClearDelayTicksCounter == 0) {
                if (movePiece(MovementType.DOWN)) lineDropTicksCounter = currentLineDropSpeedTicks;
            }
            updateKeyInput();
        }
    }

    public boolean movePiece(MovementType move) {
        if (currentPiece == null) return false;

        if (move == MovementType.ROTATE_COUNTERCLOCKWISE || move == MovementType.ROTATE_CLOCKWISE) {
            if (currentPiece.pieceType.pieceShooterPiece) {
                switchedHoldPieceAlready = true;
                Piece p = new Piece(this, grid, PieceType.emptyPieceType, BlockTypes.NORMAL);
                p.init();
                Block b = p.blocks.get(0);
                b.lastScreenX = grid.screenX + currentPiece.xGrid * cellW();
                b.lastScreenY = grid.screenY + currentPiece.yGrid * cellH();
                b.ticksSinceLastMovement = 0;
                int x = currentPiece.xGrid; int y = currentPiece.yGrid;
                while (y < gridH() - 1 && grid.get(x, y + 1) == null) y++;
                p.xGrid = x; p.yGrid = y;
                if (y != currentPiece.yGrid) { grid.setPiece(p); }
                return false;
            }
            if (currentPiece.pieceType.pieceRemovalShooterPiece) {
                switchedHoldPieceAlready = true;
                int x = currentPiece.xGrid; int y = currentPiece.yGrid;
                while (y < gridH() - 1 && grid.get(x, y) == null) y++;
                Block b = grid.get(x, y);
                if (b != null) {
                    grid.removeBlock(b, true, true);
                    b.lastScreenX = grid.screenX + x * cellW();
                    b.lastScreenY = grid.screenY + y * cellH();
                    b.xGrid = x; b.yGrid = currentPiece.yGrid;
                }
                return false;
            }
        }

        int oldX = currentPiece.xGrid; int oldY = currentPiece.yGrid; int oldRot = currentPiece.currentRotation;
        if (move == MovementType.ROTATE_COUNTERCLOCKWISE) currentPiece.rotateCCW();
        else if (move == MovementType.ROTATE_CLOCKWISE) currentPiece.rotateCW();
        else if (move == MovementType.LEFT) currentPiece.xGrid--;
        else if (move == MovementType.RIGHT) currentPiece.xGrid++;
        else if (move == MovementType.DOWN) currentPiece.yGrid++;

        if (grid.doesPieceFit(currentPiece)) {
            pieceMoved();
            return true;
        } else {
            if (move == MovementType.ROTATE_COUNTERCLOCKWISE || move == MovementType.ROTATE_CLOCKWISE) {
                if (currentGameType.pieceClimbingAllowed) {
                    if (frameState.LEFT_HELD) {
                        int tempY = currentPiece.yGrid; currentPiece.xGrid--;
                        for (int i = 0; i <= currentPiece.getHeight(); i++) {
                            currentPiece.yGrid--;
                            if (grid.doesPieceFit(currentPiece)) { pieceMoved(); return true; }
                        }
                        currentPiece.yGrid = tempY; currentPiece.xGrid++;
                    }
                    if (frameState.RIGHT_HELD) {
                        int tempY = currentPiece.yGrid; currentPiece.xGrid++;
                        for (int i = 0; i <= currentPiece.getHeight(); i++) {
                            currentPiece.yGrid--;
                            if (grid.doesPieceFit(currentPiece)) { pieceMoved(); return true; }
                        }
                        currentPiece.yGrid = tempY; currentPiece.xGrid--;
                    }
                }
            }
            currentPiece.xGrid = oldX; currentPiece.yGrid = oldY; currentPiece.setRotation(oldRot);
            if (move == MovementType.DOWN) { if (lockDelayTicksCounter == 0) setPiece(); }
            return false;
        }
    }

    public void updateKeyInput() {
        if (lockInputCountdownTicks > 0 || player == null) return;
        if (player.rotateCWPressed()) movePiece(MovementType.ROTATE_CLOCKWISE);
        if (player.rotateCCWPressed()) movePiece(MovementType.ROTATE_COUNTERCLOCKWISE);
        if (player.leftPressed()) movePiece(MovementType.LEFT);
        if (player.rightPressed()) movePiece(MovementType.RIGHT);
        if (player.downPressed()) movePiece(MovementType.DOWN);
    }

    public void manuallyApplyGravityWithoutChainChecking() {
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int y = grid.getHeight() - 2; y >= 0; y--) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null && grid.get(x, y + 1) == null) { grid.remove(x, y, false, false); grid.add(x, y + 1, b); moved = true; }
                }
            }
        }
    }

    private boolean moveDownBlocksOverBlankSpaces() {
        ArrayList<BlockType> ignore = currentGameType.getBlockTypesToIgnoreWhenMovingDown(getCurrentDifficulty());
        boolean movedAny = false;
        if (moveDownLineTicksCounter >= currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces || currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) {
            moveDownLineTicksCounter = 0;
            boolean movedThisPass = true;
            while (movedThisPass) {
                movedThisPass = false;
                if (currentGameType.chainRule_CheckEntireLine) movedThisPass = grid.moveDownLinesAboveBlankLinesOneLine();
                else if (currentGameType.gravityRule_onlyMoveDownDisconnectedBlocks) movedThisPass = grid.moveDownDisconnectedBlocksAboveBlankSpacesOneLine(ignore);
                else movedThisPass = grid.moveDownAnyBlocksAboveBlankSpacesOneLine(ignore);
                if (movedThisPass) movedAny = true;
                if (!currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) break;
            }
        }
        return movedAny;
    }

    public void checkForChain() {
        currentChainBlocks.clear();
        ArrayList<BlockType> ignore = currentGameType.getBlockTypesToIgnoreWhenCheckingChain(getCurrentDifficulty());
        ArrayList<BlockType> mustContain = currentGameType.getBlockTypesChainMustContain(getCurrentDifficulty());
        grid.setColorConnections(ignore);
        int toRow = (currentGameType.gameMode == GameMode.STACK) ? grid.getHeight() - 1 : grid.getHeight();

        if (currentGameType.chainRule_CheckEntireLine) {
            addToChainBlocks(grid.checkLines(ignore, mustContain));
        }
        if (currentGameType.chainRule_AmountPerChain > 0) {
            ArrayList<Block> chainBlocks = new ArrayList<>();
            for (int y = 0; y < toRow; y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null && !ignore.contains(b.blockType)) {
                        if (currentGameType.chainRule_CheckRow) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(b, chainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, toRow, ignore, mustContain);
                        if (currentGameType.chainRule_CheckColumn) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(b, chainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, toRow, ignore, mustContain);
                        if (currentGameType.chainRule_CheckDiagonal) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfDiagonalAtLeastAmount(b, chainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, toRow, ignore, mustContain);
                    }
                }
            }
            if (currentGameType.chainRule_CheckRecursiveConnections) grid.checkRecursiveConnectedRowOrColumn(chainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, toRow, ignore, mustContain);
            addToChainBlocks(chainBlocks);
        }
        if (currentGameType.chainRule_CheckTouchingBreakerBlocksChain) {
            ArrayList<BlockType> breakers = new ArrayList<>();
            for (BlockType bt : currentGameType.blockTypes) if (bt.isSpecialType()) breakers.add(bt);
            addToChainBlocks(grid.checkBreakerBlocks(toRow, ignore, breakers));
        }
    }

    private void addToChainBlocks(ArrayList<Block> arr) { for (Block b : arr) if (!currentChainBlocks.contains(b)) currentChainBlocks.add(b); }

    public void handleNewChain() {
        if (detectedChain()) {
            int chainMinimum = currentGameType.chainRule_CheckEntireLine ? currentGameType.gridWidth : currentGameType.chainRule_AmountPerChain;
            if (currentCombo == 0) {
                currentCombo = 1;
                currentChain = currentChainBlocks.size();
                int bonus = (currentChain - chainMinimum);
                if (currentGameType.chainRule_CheckEntireLine) {
                    bonus = currentChain / currentGameType.gridWidth;
                    if (bonus == 1) bonus = 0;
                }
                if (bonus > 0) queueVSGarbageToSend(bonus);
            } else {
                currentCombo++;
                currentChain = currentChainBlocks.size();
                comboChainTotal += currentChain;
                totalCombosMade++;
                if (comboChainTotal > biggestComboChain) biggestComboChain = comboChainTotal;
                int bonus = (currentChain - chainMinimum);
                if (bonus <= 0) bonus = 1;
                queueVSGarbageToSend(currentCombo);
            }

            ArrayList<Block> addToChain = new ArrayList<>();
            for (Block a : currentChainBlocks) {
                for (Block b : grid.getConnectedBlocksUpDownLeftRight(a)) {
                    if (b.blockType.addToChainIfConnectedUpDownLeftRightToExplodingChainBlocks) {
                        if (!currentChainBlocks.contains(b) && !addToChain.contains(b)) addToChain.add(b);
                    }
                }
            }
            currentChainBlocks.addAll(addToChain);
            for (Block b : currentChainBlocks) b.flashingToBeRemoved = true;

            if (currentGameType.gameMode == GameMode.STACK && currentChainBlocks.size() > 3) {
                stopStackRiseTicksCounter += 1000 * currentChainBlocks.size();
            }
            timesToFlashBlocksQueue = timesToFlashBlocks;
        }
    }

    public void queueVSGarbageToSend(int amount) {
        amount *= getRoom().multiplayer_GarbageMultiplier;
        if (getRoom().multiplayer_GarbageScaleByDifficulty) {
            String diff = getCurrentDifficulty().name;
            if (diff.equals("Beginner")) amount = (int)(amount * 2.0);
            if (diff.equals("Easy")) amount = (int)(amount * 1.5);
            if (diff.equals("Normal")) amount = (int)(amount * 1.0);
            if (diff.equals("Hard")) amount = (int)(amount * 0.75);
            if (diff.equals("Insane")) amount = (int)(amount * 0.5);
        }

        if (queuedVSGarbageAmountFromOtherPlayer > 0) {
            if (amount >= queuedVSGarbageAmountFromOtherPlayer) {
                amount -= queuedVSGarbageAmountFromOtherPlayer;
                queuedVSGarbageAmountFromOtherPlayer = 0;
            } else {
                queuedVSGarbageAmountFromOtherPlayer -= amount;
                amount = 0;
            }
        }

        if (isMultiplayer() && !getRoom().multiplayer_DisableVSGarbage) {
            if (amount > 0) {
                queuedVSGarbageAmountToSend += amount;
                for (GameLogicListener l : listeners) l.onGarbageSent(amount);
            }
        }
    }

    public void gotVSGarbageFromOtherPlayer(int amount) {
        garbageWaitForPiecesSetCount = Math.min(4, garbageWaitForPiecesSetCount + 3);
        if (getRoom().multiplayer_GarbageScaleByDifficulty) {
            String diff = getCurrentDifficulty().name;
            if (diff.equals("Beginner")) amount = (int)(amount * 0.5);
            if (diff.equals("Easy")) amount = (int)(amount * 0.75);
            if (diff.equals("Normal")) amount = (int)(amount * 1.0);
            if (diff.equals("Hard")) amount = (int)(amount * 1.5);
            if (diff.equals("Insane")) amount = (int)(amount * 2.0);
        }
        queuedVSGarbageAmountFromOtherPlayer += amount;
        if (getRoom().multiplayer_GarbageLimit > 0 && queuedVSGarbageAmountFromOtherPlayer > getRoom().multiplayer_GarbageLimit) {
            queuedVSGarbageAmountFromOtherPlayer = getRoom().multiplayer_GarbageLimit;
        }
    }

    public void processQueuedGarbageSentFromOtherPlayer() {
        if (queuedVSGarbageAmountFromOtherPlayer > 0) {
            if (garbageWaitForPiecesSetCount == 0) {
                int garbageMultiplier = 2;
                while (queuedVSGarbageAmountFromOtherPlayer / (grid.getWidth() / garbageMultiplier) > 0) {
                    queuedVSGarbageAmountFromOtherPlayer -= grid.getWidth();
                    if (queuedVSGarbageAmountFromOtherPlayer < 0) queuedVSGarbageAmountFromOtherPlayer = 0;

                    if (currentGameType.vsGarbageDropRule == VSGarbageDropRule.FALL_FROM_CEILING_IN_EVEN_ROWS) {
                        makeGarbageRowFromCeiling();
                        moveDownBlocksOverBlankSpaces();
                    } else if (currentGameType.vsGarbageDropRule == VSGarbageDropRule.RISE_FROM_FLOOR_IN_EVEN_ROWS) {
                        makeGarbageRowFromFloor();
                    }
                }
            }
        }
    }

    public void processGarbageRules() {
        boolean makeGarbage = false;
        if (currentGameType.playingFieldGarbageSpawnRule == GarbageSpawnRule.TICKS) {
            playingFieldGarbageValueCounter += ticks();
            if (playingFieldGarbageValueCounter > getCurrentDifficulty().playingFieldGarbageSpawnRuleAmount) {
                playingFieldGarbageValueCounter = 0;
                makeGarbage = true;
            }
        } else {
            GarbageSpawnRule rule = currentGameType.playingFieldGarbageSpawnRule;
            int amount = getCurrentDifficulty().playingFieldGarbageSpawnRuleAmount;
            if (rule == GarbageSpawnRule.PIECES_MADE) {
                if (piecesMadeThisGame >= playingFieldGarbageValueCounter + amount) {
                    playingFieldGarbageValueCounter = piecesMadeThisGame;
                    makeGarbage = true;
                }
            } else if (rule == GarbageSpawnRule.BLOCKS_CLEARED) {
                if (blocksClearedThisGame >= playingFieldGarbageValueCounter + amount) {
                    playingFieldGarbageValueCounter = blocksClearedThisGame;
                    makeGarbage = true;
                }
            } else if (rule == GarbageSpawnRule.LINES_CLEARED) {
                if (linesClearedThisGame >= playingFieldGarbageValueCounter + amount) {
                    playingFieldGarbageValueCounter = linesClearedThisGame;
                    makeGarbage = true;
                }
            }
        }
        if (makeGarbage) makeGarbageRowFromFloor();
    }

    public void makeGarbageRowFromFloor() {
        grid.makeGarbageRowFromFloor();
        manuallyApplyGravityWithoutChainChecking();
    }

    public void makeGarbageRowFromCeiling() {
        grid.makeGarbageRowFromCeiling();
        manuallyApplyGravityWithoutChainChecking();
    }

    public boolean isNetworkGame() { return manager != null && manager.isNetworkGame(); }
    public boolean isMultiplayer() { return manager != null && manager.getGames().size() > 1; }

    private boolean detectedChain() { return !currentChainBlocks.isEmpty(); }

    public void updateScore() {
        if (piecesMadeThisGame > lastPiecesMadeThisGame) {
            lastPiecesMadeThisGame = piecesMadeThisGame;
            gameSpeed += getRoom().gameSpeedChangeRate;
            if (gameSpeed > getRoom().gameSpeedMaximum) gameSpeed = getRoom().gameSpeedMaximum;
            int dropSpeedDiff = getCurrentDifficulty().initialLineDropSpeedTicks - getCurrentDifficulty().minimumLineDropSpeedTicks;
            currentLineDropSpeedTicks = getCurrentDifficulty().initialLineDropSpeedTicks - (int)(dropSpeedDiff * gameSpeed);
        }
        int amount = (int)(currentGameType.scoreTypeAmountPerLevelGained * getRoom().levelUpMultiplier * getRoom().levelUpCompoundMultiplier);
        if (currentGameType.scoreType == ScoreType.LINES_CLEARED && linesClearedThisLevel >= amount) { currentLevel++; linesClearedThisLevel -= amount; }
        else if (currentGameType.scoreType == ScoreType.BLOCKS_CLEARED && blocksClearedThisLevel >= amount) { currentLevel++; blocksClearedThisLevel -= amount; }
        else if (currentGameType.scoreType == ScoreType.PIECES_MADE && piecesMadeThisLevel >= amount) { currentLevel++; piecesMadeThisLevel -= amount; }
    }

    public void flashChainBlocks() {
        flashBlocksTicksCounter += ticks();
        if (flashBlocksTicksCounter > flashBlockSpeedTicks) {
            flashBlocksTicksCounter = 0;
            for (Block b : currentChainBlocks) b.flashingToBeRemovedLightDarkToggle = !b.flashingToBeRemovedLightDarkToggle;
            timesToFlashBlocksQueue--;
        }
    }

    public void removeFlashedChainBlocks() {
        removeBlocksTicksCounter += ticks();
        int delay = currentGameType.removingBlocksDelayTicksBetweenEachBlock;
        while (!currentChainBlocks.isEmpty() && (delay == 0 || removeBlocksTicksCounter > delay)) {
            removeBlocksTicksCounter = 0;
            Block a = currentChainBlocks.remove(0);
            grid.removeBlock(a, true, true);
            blocksClearedThisGame++; blocksClearedThisLevel++;
        }
    }

    private void setPiece() {
        grid.setPiece(currentPiece);
        pieceSetAtBottom = true; piecesPlacedTotal++; lastPiece = currentPiece; 
        currentPiece = null;
    }

    public void newRandomPiece() {
        pieceSetAtBottom = false; currentPiece = grid.getRandomPiece();
        currentPiece.init(); setCurrentPieceAtTop();
    }

    private void setCurrentPieceAtTop() {
        currentPiece.xGrid = grid.getWidth() / 2 - (currentPiece.getWidth() / 2 + currentPiece.getLowestOffsetX());
        currentPiece.yGrid = -2 + aboveGridBuffer;
        if (!grid.doesPieceFit(currentPiece)) {
            died = true;
            setState(GameState.GAME_OVER);
        }
        spawnDelayTicksCounter = 0; // adjustedSpawnDelayTicksAmount
    }

    public void pieceMoved() { lockDelayTicksCounter = currentGameType.maxLockDelayTicks; }
    public Room getRoom() { return manager != null && manager.getCurrentRoom() != null ? manager.getCurrentRoom() : new Room(); }
    public int cellW() { return blockWidth + currentGameType.gridPixelsBetweenColumns; }
    public int cellH() { return blockHeight + currentGameType.gridPixelsBetweenRows; }
    public int gridW() { return currentGameType.gridWidth; }
    public int gridH() { return currentGameType.gridHeight + aboveGridBuffer; }
    private void updateSpecialPiecesAndBlocks() { if (currentPiece != null) currentPiece.update(); if (holdPiece != null) holdPiece.update(); }
    private void resetNextPieces() { currentPiece = null; holdPiece = null; nextPieces.clear(); }
    private void checkForFastMusic() { playingFastMusic = grid.isAnythingAboveThreeQuarters(); }

    public void render() {
        // Render logic handled by BobsGame in Java/libGDX
    }

    public int getRandomIntLessThan(int n, String source) {
        return random.nextInt(n);
    }

    public static List<GameType> getBuiltInGameTypes() {
        return new ArrayList<>();
    }

    public void setGameSequence(GameSequence seq) {
        this.currentGameSequence = seq;
    }

    public void deleteAllCaptions() {
        // TODO
    }

    public void updateNormalGame(int side) {
        // TODO
    }

    public static final int MIDDLE = 0;

    public void renderBackground() {}
    public void renderBlocks() {}
    public void renderForeground() {}
}
