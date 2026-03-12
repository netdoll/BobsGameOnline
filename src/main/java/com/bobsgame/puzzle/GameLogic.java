package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import com.bobsgame.client.GLUtils;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bobsgame.client.engine.text.Caption;
import com.bobsgame.client.engine.text.CaptionManager;
import com.bobsgame.client.BobsGame;
import org.apache.commons.io.FileUtils;
import com.bobsgame.shared.Easing;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.EnginePart;

public class GameLogic extends EnginePart {
    public static final Logger log = LoggerFactory.getLogger(GameLogic.class);

    public enum MovementType { NONE, LEFT, RIGHT, DOWN, UP, ROTATE_CLOCKWISE, ROTATE_COUNTERCLOCKWISE, HARD_DROP }

    public GameSequence currentGameSequence = null;
    public PuzzlePlayer player = null;

    public ArrayList<GameType> gameTypeRandomBag = new ArrayList<>();
    public GameType currentGameType = null;

    public Grid grid = null;

    public int blockWidth = 1;
    public int blockHeight = 1;

    public static int aboveGridBuffer = 5;

    public boolean dontResetNextPieces = false;

    public long lockInputCountdownTicks = 0;

    public boolean canPressRotateCW = false;
    public boolean canPressRotateCCW = false;
    public boolean canPressRight = false;
    public boolean canPressLeft = false;
    public boolean canPressDown = false;
    public boolean canPressUp = false;
    public boolean canPressHoldRaise = false;
    public boolean canPressSlam = false;

    public long ticksHoldingRotateCW = 0;
    public long ticksHoldingRotateCCW = 0;
    public long ticksHoldingRight = 0;
    public long ticksHoldingLeft = 0;
    public long ticksHoldingDown = 0;
    public long ticksHoldingUp = 0;
    public long ticksHoldingHoldRaise = 0;
    public long ticksHoldingSlam = 0;

    public boolean repeatStartedRotateCW = false;
    public boolean repeatStartedRotateCCW = false;
    public boolean repeatStartedHoldRaise = false;
    public boolean repeatStartedUp = false;
    public boolean repeatStartedDown = false;
    public boolean repeatStartedLeft = false;
    public boolean repeatStartedRight = false;
    public boolean repeatStartedSlam = false;

    public int timesToFlashBlocks = 20;
    public long flashBlockSpeedTicks = 30;

    public long flashScreenSpeedTicks = 50;
    public int flashScreenTimesPerLevel = 4;

    public boolean won = false;
    public boolean lost = false;
    public boolean died = false;
    public boolean dead = false;
    public boolean startedDeathSequence = false;
    public boolean startedWinSequence = false;
    public boolean startedLoseSequence = false;

    public boolean complete = false;
    public boolean creditScreenInitialized = false;
    public boolean firstInit = true;
    public boolean didInit = false;

    public boolean madeBeginnerStackAnnouncement = false;

    public boolean extraStage1 = false;
    public boolean extraStage2 = false;
    public boolean extraStage3 = false;
    public boolean extraStage4 = false;

    public boolean gravityThisFrame = false;

    public boolean pieceSetAtBottom = false;
    public boolean switchedHoldPieceAlready = false;
    public boolean playingFastMusic = false;
    public boolean firstDeath = false;

    public float gameSpeed = 0.0f;
    public long currentLineDropSpeedTicks = 0;
    public long currentStackRiseSpeedTicks = 0;
    public long lockDelayTicksCounter = 0;
    public long lineDropTicksCounter = 0;
    public long spawnDelayTicksCounter = 0;
    public long lineClearDelayTicksCounter = 0;
    public long moveDownLineTicksCounter = 0;

    public long currentTotalYLockDelay = 0;
    public long adjustedMaxLockDelayTicks = 0;
    public long adjustedSpawnDelayTicksAmount = 0;
    public long currentFloorMovements = 0;

    public String playingMusic = "";

    public long stackRiseTicksCounter = 0;
    public long stopStackRiseTicksCounter = 0;
    public long manualStackRiseTicksCounter = 0;
    public int manualStackRiseSoundToggle = 0;

    public int timesToFlashScreenQueue = 0;
    public long flashScreenTicksCounter = 0;
    public boolean flashScreenOnOffToggle = false;

    public long flashBlocksTicksCounter = 0;
    public int timesToFlashBlocksQueue = 0;
    public long removeBlocksTicksCounter = 0;
    public ArrayList<Block> currentChainBlocks = new ArrayList<>();
    public ArrayList<Block> fadingOutBlocks = new ArrayList<>();

    public Piece currentPiece = null;
    public Piece lastPiece = null;

    public Piece holdPiece = null;
    public ArrayList<Piece> nextPieces = new ArrayList<>();
    public ArrayList<Piece> nextPieceSpecialBuffer = new ArrayList<>();

    public int lastKnownLevel = 0;
    public int currentLevel = 0;

    public int piecesMadeThisGame = 0;
    public int lastPiecesMadeThisGame = 0;
    public int blocksClearedThisGame = 0;
    public int linesClearedThisGame = 0;

    public int piecesMadeThisLevel = 0;
    public int blocksClearedThisLevel = 0;
    public int linesClearedThisLevel = 0;

    public int blocksMadeTotal = 0;
    public int piecesMadeTotal = 0;
    public int piecesPlacedTotal = 0;
    public int blocksClearedTotal = 0;
    public int linesClearedTotal = 0;

    public long timeStarted = 0;
    public long timeEnded = 0;
    public long totalTicksPassed = 0;
    public int createdPiecesCounterForFrequencyPieces = 0;

    public boolean waitingForStart = true;
    public boolean waitingForReady = true;
    public boolean playedReadySound = false;
    public long readyTicksCounter = 0;

    public long playingFieldGarbageValueCounter = 0;

    public boolean forceGravityThisFrame = false;
    public String uuid = UUID.randomUUID().toString();
    public boolean mute = false;

    public static final int MIDDLE = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    public boolean waitingForPlayer = false;
    public boolean sendNetworkFrames = false;
    public boolean controlledByNetwork = false;
    public boolean win = false;
    public boolean lose = false;
    public boolean credits = false;

    public int currentChain = 0;
    public int currentCombo = 0;
    public int comboChainTotal = 0;

    public int totalCombosMade = 0;
    public int biggestComboChain = 0;

    public int lastSentGarbageToPlayerIndex = 0;
    public int queuedVSGarbageAmountToSend = 0;
    public int queuedVSGarbageAmountFromOtherPlayer = 0;
    public int garbageWaitForPiecesSetCount = 0;

    public boolean checkForChainAgainIfNoBlocksPopping = false;

    public float captionY = -1;
    public float captionX = 0;
    public float playingFieldX0 = 0;
    public float playingFieldX1 = 0;
    public float playingFieldY0 = 0;
    public float playingFieldY1 = 0;

    public float captionColorCycleHueValue = 0;
    public boolean captionColorCycleHueInOutToggle = false;

    public float captionScale = 1.0f;
    public int captionYSize = 14;

    public BobColor captionTextColor = null;
    public BobColor captionBGColor = null;
    public int captionFontSize = 0;

    public float announcementCaptionScale = 0.3f;
    public BobColor announcementCaptionTextColor = null;
    public BobColor announcementCaptionBGColor = null;
    public int announcementCaptionFontSize = 0;
    public int resultCaptionFontSize = 0;
    public int mediumCaptionFontSize = 0;

    public Caption levelCaption = null;
    public String levelCaptionText = "levelCaptionText";

    public Caption gameTypeCaption = null;
    public Caption rulesCaption1 = null;
    public Caption rulesCaption2 = null;
    public Caption rulesCaption3 = null;
    public Caption difficultyCaption = null;
    public Caption stopCounterCaption = null;
    public String stopCounterCaptionText = "Go!";

    public Caption xCaption = null;
    public Caption yCaption = null;
    public Caption lineDropTicksCaption = null;
    public Caption lockDelayCaption = null;
    public Caption spawnDelayCaption = null;
    public Caption lineClearDelayCaption = null;
    public Caption gravityCaption = null;
    public Caption rotationCaption = null;
    public Caption holdCaption = null;
    public Caption nextCaption = null;

    public Caption totalLinesClearedCaption = null;
    public Caption totalBlocksClearedCaption = null;
    public Caption totalPiecesMadeCaption = null;

    public Caption linesClearedThisGameCaption = null;
    public Caption blocksClearedThisGameCaption = null;
    public Caption piecesMadeThisGameCaption = null;

    public Caption blocksInGridCaption = null;
    public Caption currentChainCaption = null;
    public Caption currentComboCaption = null;
    public Caption comboChainTotalCaption = null;
    public Caption seedCaption = null;
    public Caption bgCaption = null;

    public Caption piecesToLevelUpThisLevelCaption = null;
    public Caption piecesLeftToLevelUpCaption = null;

    public ArrayList<Caption> infoCaptions = new ArrayList<>();
    public ArrayList<Caption> announcementCaptions = new ArrayList<>();

    public Caption totalTicksPassedCaption = null;
    public int timeCaptionStandardizedWidth = 0;

    public Caption pressStartCaption = null;
    public Caption waitingForPlayerCaption = null;
    public Caption creditsCaption = null;
    public Caption deadCaption = null;
    public Caption winCaption = null;
    public Caption loseCaption = null;
    public Caption garbageWaitCaption = null;

    public boolean triedToGetHighScore = false;
    public Caption scoreBarTypeCaption = null;
    public Caption myScoreBarCaption = null;
    public Caption myHighScoreBarCaption = null;
    public Caption leaderboardBarCaption = null;

    public boolean isNetworkPlayer = false;
    public FrameState frameState = new FrameState();
    public ArrayList<FrameState> framesArray = new ArrayList<>();
    public long randomSeed = -1;
    public Random randomGenerator = new Random();

    public int lastSentPacketID = 0;
    public ArrayList<String> outboundPacketQueueVector = new ArrayList<>();
    public HashMap<String, String> outboundPacketQueueHashMap = new HashMap<>();

    public long lastIncomingFramePacketID = 0;
    public long storePacketsTicksCounter = 0;

    public ArrayList<ArrayList<FrameState>> allNetworkPacketsSentUpUntilNow = new ArrayList<>();
    public boolean waitingForNetworkFrames = true;

    public long lastIncomingTrafficTime = 0;
    public boolean theyForfeit = false;

    public boolean pauseMiniMenuShowing = false;

    public ArrayList<String> _gotPacketsLog = new ArrayList<>();
    public boolean packetProcessThreadStarted = false;

    private boolean _stopThread = false;
    private final ReentrantLock _stopThread_Lock = new ReentrantLock();

    public boolean getStopThread_S() {
        _stopThread_Lock.lock();
        try { return _stopThread; } finally { _stopThread_Lock.unlock(); }
    }
    public void setStopThread_S(boolean b) {
        _stopThread_Lock.lock();
        try { _stopThread = b; } finally { _stopThread_Lock.unlock(); }
    }

    public NetworkPacket networkPacket = new NetworkPacket();

    public static class NetworkPacket {
        public ArrayList<FrameState> frameStates = new ArrayList<>();
    }

    public GameLogic(Engine g, long seed) {
        super(g);
        this.randomSeed = seed;
        this.randomGenerator = new Random(seed);
        this.grid = new Grid(this);
    }

    public long ticks() { return frameState != null ? frameState.ticksPassed : 0; }

    public DifficultyType getCurrentDifficulty() {
        if (currentGameSequence != null && !currentGameSequence.gameTypes.isEmpty()) {
            return currentGameSequence.gameTypes.get(0).getDifficultyByName(currentGameSequence.currentDifficultyName);
        }
        return GameType.difficulty_NORMAL;
    }

    public void update(int gameIndex, int numGames) {
        float screenWidth = 800; float screenHeight = 600;
        int colWidth = (int)screenWidth / numGames;
        blockHeight = (int)screenHeight / (gridH() + 7); blockWidth = blockHeight;
        blockWidth -= currentGameType.gridPixelsBetweenColumns; blockHeight -= currentGameType.gridPixelsBetweenRows;
        grid.screenX = (float)(gameIndex * colWidth) + (colWidth / 2 - (gridW() * cellW() / 2));
        grid.screenY = 5 * cellH();
        playingFieldX0 = gameIndex * colWidth; playingFieldX1 = (gameIndex + 1) * colWidth;
        playingFieldY0 = 0; playingFieldY1 = screenHeight;
        captionX = grid.getXOnScreenNoShake() + (grid.getWidth() + 1) * cellW() + 4;
        updateCaptionFadeValues();
        if (!isNetworkPlayer) {
            frameState = new FrameState(); frameState.ticksPassed = 16;
            setControlsState();
            if (!didInit) initGame();
            processFrame();
            framesArray.add(frameState);
        }
    }

    public void updateNormalGame(int side) { update(side, 3); }
    public void updateNetworkGame() { update(LEFT, 2); }

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

    private void initGame() {
        if (firstInit) {
            firstInit = false;
            timeStarted = System.currentTimeMillis();
            gameSpeed = getRoom().gameSpeedStart;
            adjustedMaxLockDelayTicks = currentGameType.maxLockDelayTicks;
        }
        didInit = true;
        resetNextPieces();
        grid.reformat(gridW(), gridH());
        grid.scrollPlayingFieldY = 0;
        manuallyApplyGravityWithoutChainChecking();
        grid.replaceAllBlocksWithNewGameBlocks();
        manuallyApplyGravityWithoutChainChecking();
        lockDelayTicksCounter = adjustedMaxLockDelayTicks;
        currentLineDropSpeedTicks = getCurrentDifficulty().initialLineDropSpeedTicks;
        currentStackRiseSpeedTicks = getCurrentDifficulty().maxStackRise;
        stopStackRiseTicksCounter = 1000;
        if (currentGameType.gameMode == GameType.GameMode.DROP) {
            if (getCurrentDifficulty().randomlyFillGrid) grid.randomlyFillGridWithPlayingFieldPieces(getCurrentDifficulty().randomlyFillGridAmount, getCurrentDifficulty().randomlyFillGridStartY);
            newRandomPiece();
        } else if (currentGameType.gameMode == GameType.GameMode.STACK) {
            if (getCurrentDifficulty().randomlyFillGrid) grid.buildRandomStackRetainingExistingBlocks(getCurrentDifficulty().randomlyFillGridAmount, getCurrentDifficulty().randomlyFillGridStartY);
            currentPiece = grid.getRandomPiece(currentGameType.getNormalPieceTypes(getCurrentDifficulty()), currentGameType.getNormalBlockTypes(getCurrentDifficulty()));
            currentPiece.xGrid = grid.getWidth() / 2; currentPiece.yGrid = 7 + aboveGridBuffer;
        }
    }

    private void processFrame() {
        updateCaptions();
        if (won || lost || complete || died) { if (timeEnded == 0) timeEnded = System.currentTimeMillis(); }
        if (won) return; if (lost) return; if (complete) return; if (died) return;
        totalTicksPassed += ticks();
        updateSpecialPiecesAndBlocks();
        processQueuedGarbageSentFromOtherPlayer();
        grid.update();
        grid.scrollBackground();
        doExtraStageEffects();
        lockInputCountdownTicks = Math.max(0, lockInputCountdownTicks - ticks());
        lockDelayTicksCounter = Math.max(0, lockDelayTicksCounter - ticks());
        lineDropTicksCounter = Math.max(0, lineDropTicksCounter - ticks());
        lineClearDelayTicksCounter = Math.max(0, lineClearDelayTicksCounter - ticks());
        spawnDelayTicksCounter = Math.max(0, spawnDelayTicksCounter - ticks());
        if (currentGameType.gameMode == GameType.GameMode.STACK) doStackRiseGame();
        else if (currentGameType.gameMode == GameType.GameMode.DROP) doFallingBlockGame();
        moveDownLineTicksCounter += ticks();
        if ((pieceSetAtBottom && !detectedChain()) || forceGravityThisFrame) {
            if (checkForChainAgainIfNoBlocksPopping) { if (grid.areAnyBlocksPopping()) return; else checkForChainAgainIfNoBlocksPopping = false; }
            boolean movedDownBlocks = moveDownBlocksOverBlankSpaces();
            if (movedDownBlocks) gravityThisFrame = true;
            else {
                forceGravityThisFrame = false; gravityThisFrame = false;
                checkForChain(); handleNewChain(); checkForFastMusic();
                if (!detectedChain() && !checkForChainAgainIfNoBlocksPopping) {
                    currentCombo = 0; currentChain = 0; comboChainTotal = 0;
                    if (currentGameType.gameMode == GameType.GameMode.DROP && pieceSetAtBottom) newRandomPiece();
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
        if (timesToFlashScreenQueue > 0) { flashScreen(); stop = true; }
        if (grid.continueSwappingBlocks()) { stop = true; }
        if (!stop) {
            stackRiseTicksCounter += ticks();
            if (stackRiseTicksCounter > currentStackRiseSpeedTicks) {
                stackRiseTicksCounter = 0;
                if (!grid.scrollUpStack(currentPiece, 1)) died = true;
            }
        }
        updateKeyInput();
    }

    private void doFallingBlockGame() {
        if (timesToFlashBlocksQueue > 0) { flashChainBlocks(); return; }
        if (detectedChain()) { removeFlashedChainBlocks(); return; }
        if (timesToFlashScreenQueue > 0) flashScreen();
        if (!pieceSetAtBottom) {
            currentTotalYLockDelay += lockDelayTicksCounter;
            if (getRoom().totalYLockDelayLimit > -1 && currentTotalYLockDelay >= getRoom().totalYLockDelayLimit) {
                currentPiece.yGrid++; if (!grid.doesPieceFit(currentPiece, currentPiece.xGrid, currentPiece.yGrid)) { currentPiece.yGrid--; setPiece(); } else currentPiece.yGrid--;
            }
            if (lineDropTicksCounter == 0 && spawnDelayTicksCounter == 0 && lineClearDelayTicksCounter == 0) {
                if (movePiece(MovementType.DOWN)) lineDropTicksCounter = currentLineDropSpeedTicks;
            }
            updateKeyInput();
        }
    }

    public boolean movePiece(MovementType move) {
        if (currentPiece == null) return false;
        int oldX = currentPiece.xGrid; int oldY = currentPiece.yGrid; int oldRot = currentPiece.currentRotation;
        if (move == MovementType.ROTATE_CLOCKWISE) currentPiece.rotateCW();
        else if (move == MovementType.ROTATE_COUNTERCLOCKWISE) currentPiece.rotateCCW();
        else if (move == MovementType.LEFT) currentPiece.xGrid--;
        else if (move == MovementType.RIGHT) currentPiece.xGrid++;
        else if (move == MovementType.DOWN || move == MovementType.HARD_DROP) currentPiece.yGrid++;
        else if (move == MovementType.UP) currentPiece.yGrid--;
        if (grid.doesPieceFit(currentPiece, currentPiece.xGrid, currentPiece.yGrid)) { lockDelayTicksCounter = adjustedMaxLockDelayTicks; return true; }
        else {
            currentPiece.xGrid = oldX; currentPiece.yGrid = oldY; currentPiece.setRotation(oldRot);
            if (move == MovementType.DOWN || move == MovementType.HARD_DROP) if (lockDelayTicksCounter == 0) setPiece();
            return false;
        }
    }

    public void updateKeyInput() {
        if (lockInputCountdownTicks > 0 || player == null) return;
        if (!frameState.ROTATECW_HELD) { canPressRotateCW = true; ticksHoldingRotateCW = 0; }
        if (!frameState.ROTATECCW_HELD) { canPressRotateCCW = true; ticksHoldingRotateCCW = 0; }
        if (!frameState.RIGHT_HELD) { canPressRight = true; ticksHoldingRight = 0; }
        if (!frameState.LEFT_HELD) { canPressLeft = true; ticksHoldingLeft = 0; }
        if (!frameState.DOWN_HELD) { canPressDown = true; ticksHoldingDown = 0; }
        if (!frameState.UP_HELD) { canPressUp = true; ticksHoldingUp = 0; }
        if (!frameState.HOLDRAISE_HELD) { canPressHoldRaise = true; ticksHoldingHoldRaise = 0; }
        if (!frameState.SLAM_HELD) { canPressSlam = true; ticksHoldingSlam = 0; }
        if (frameState.RIGHT_HELD) { ticksHoldingRight += ticks(); if (ticksHoldingRight > 150) canPressRight = true; }
        if (frameState.LEFT_HELD) { ticksHoldingLeft += ticks(); if (ticksHoldingLeft > 150) canPressLeft = true; }
        if (frameState.DOWN_HELD) { ticksHoldingDown += ticks(); if (ticksHoldingDown > 30) canPressDown = true; }
        if (frameState.ROTATECW_HELD && canPressRotateCW) { movePiece(MovementType.ROTATE_CLOCKWISE); canPressRotateCW = false; }
        if (frameState.ROTATECCW_HELD && canPressRotateCCW) { movePiece(MovementType.ROTATE_COUNTERCLOCKWISE); canPressRotateCCW = false; }
        if (frameState.RIGHT_HELD && canPressRight) { movePiece(MovementType.RIGHT); canPressRight = false; }
        if (frameState.LEFT_HELD && canPressLeft) { movePiece(MovementType.LEFT); canPressLeft = false; }
        if (frameState.DOWN_HELD && canPressDown) {
            if (currentGameType.gameMode == GameType.GameMode.DROP) { if (!pieceSetAtBottom) { if (movePiece(MovementType.DOWN)) lockDelayTicksCounter = 0; } }
            else { currentPiece.yGrid++; if (currentPiece.yGrid > grid.getHeight() - (1 + currentPiece.getHeight())) currentPiece.yGrid--; }
            canPressDown = false;
        }
        if (frameState.UP_HELD && canPressUp) { if (currentGameType.gameMode == GameType.GameMode.STACK) { currentPiece.yGrid--; if (currentPiece.yGrid < 1 + aboveGridBuffer) currentPiece.yGrid++; } canPressUp = false; }
        if (frameState.SLAM_HELD && canPressSlam) {
            if (currentGameType.gameMode == GameType.GameMode.DROP && !pieceSetAtBottom) { currentPiece.setBlocksSlamming(); while (movePiece(MovementType.HARD_DROP)); grid.setShakePlayingField(120, 2, 2, 40); }
            canPressSlam = false;
        }
        if (frameState.HOLDRAISE_HELD && canPressHoldRaise) {
            if (currentGameType.gameMode == GameType.GameMode.DROP && currentGameType.holdPieceEnabled) {
                if (holdPiece == null) { holdPiece = currentPiece; newRandomPiece(); }
                else if (!switchedHoldPieceAlready) { Piece tmp = holdPiece; holdPiece = currentPiece; currentPiece = tmp; switchedHoldPieceAlready = true; setCurrentPieceAtTop(); }
            }
            canPressHoldRaise = false;
        }
    }

    public void manuallyApplyGravityWithoutChainChecking() {
        if (grid == null) return;
        boolean moved = true;
        while (moved) {
            moved = false;
            for (int y = grid.getHeight() - 2; y >= 0; y--) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null && grid.get(x, y+1) == null) { grid.remove(x, y, false, false); grid.add(x, y + 1, b); moved = true; }
                }
            }
        }
    }

    private boolean moveDownBlocksOverBlankSpaces() {
        if (grid == null) return false;
        boolean movedAny = false;
        moveDownLineTicksCounter += ticks();
        if (moveDownLineTicksCounter >= currentGameType.gravityRule_ticksToMoveDownBlocksOverBlankSpaces || currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) {
            moveDownLineTicksCounter = 0;
            boolean movedThisPass = true;
            while (movedThisPass) {
                movedThisPass = false;
                for (int y = grid.getHeight() - 2; y >= 0; y--) {
                    for (int x = 0; x < grid.getWidth(); x++) {
                        Block b = grid.get(x, y);
                        if (b != null && grid.get(x, y + 1) == null) { grid.remove(x, y, false, false); grid.add(x, y + 1, b); movedThisPass = true; movedAny = true; }
                    }
                }
                if (!currentGameType.moveDownAllLinesOverBlankSpacesAtOnce) break;
            }
        }
        return movedAny;
    }

    public void checkForChain() {
        if (grid == null) return;
        currentChainBlocks.clear();

        ArrayList<BlockType> ignore = currentGameType.getBlockTypesToIgnoreWhenCheckingChain(getCurrentDifficulty());
        ArrayList<BlockType> mustContain = currentGameType.getBlockTypesChainMustContain(getCurrentDifficulty());

        if (currentGameType.chainRule_CheckEntireLine) {
            ArrayList<Block> lineBlocks = grid.checkLines(ignore, mustContain);
            for (Block b : lineBlocks) if (!currentChainBlocks.contains(b)) currentChainBlocks.add(b);
        }

        if (currentGameType.chainRule_CheckRow) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInRowAtLeastAmount(b, currentChainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, grid.getHeight(), ignore, mustContain);
                }
            }
        }

        if (currentGameType.chainRule_CheckColumn) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfInColumnAtLeastAmount(b, currentChainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, grid.getHeight(), ignore, mustContain);
                }
            }
        }

        if (currentGameType.chainRule_CheckDiagonal) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Block b = grid.get(x, y);
                    if (b != null) grid.addBlocksConnectedToBlockToArrayIfNotInItAlreadyIfDiagonalAtLeastAmount(b, currentChainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, grid.getHeight(), ignore, mustContain);
                }
            }
        }

        if (currentGameType.chainRule_CheckRecursiveConnections) {
            grid.checkRecursiveConnectedRowOrColumn(currentChainBlocks, currentGameType.chainRule_AmountPerChain, 0, grid.getWidth(), 0, grid.getHeight(), ignore, mustContain);
        }

        if (currentGameType.chainRule_CheckTouchingBreakerBlocksChain) {
            ArrayList<BlockType> breakers = new ArrayList<>();
            for (BlockType bt : currentGameType.blockTypes) if (bt.isSpecialType()) breakers.add(bt); 
            ArrayList<Block> breakerBlocks = grid.checkBreakerBlocks(grid.getHeight(), ignore, breakers);
            for (Block b : breakerBlocks) if (!currentChainBlocks.contains(b)) currentChainBlocks.add(b);
        }
    }

    public void handleNewChain() {
        if (!currentChainBlocks.isEmpty()) {
            for (Block b : currentChainBlocks) { grid.remove(b, true, true); blocksClearedThisGame++; }
            currentChain++; currentCombo++; comboChainTotal++; grid.shakeSmall();
        }
    }

    private boolean detectedChain() { return !currentChainBlocks.isEmpty(); }
    public void checkForFastMusic() {}
    public void updateScore() {}
    public void processQueuedGarbageSentFromOtherPlayer() {}
    public void queueVSGarbageToSend(int amount) {}
    public void updateSpecialPiecesAndBlocks() { if (currentPiece != null) currentPiece.update(); if (holdPiece != null) holdPiece.update(); }
    public void doExtraStageEffects() {}
    public void updateCaptions() {}
    public void updateCaptionFadeValues() {}
    public void flashChainBlocks() {}
    public void flashScreen() {}
    public void removeFlashedChainBlocks() {}

    public void render() {
        renderBackground();
        renderBlocks();
        renderForeground();
    }

    public void renderBackground() {
        grid.renderBackground();
        grid.renderBorder();
    }

    public void renderBlocks() {
        // renderQueuedGarbage(); // TODO
        for (int i = 0; i < fadingOutBlocks.size(); i++) {
            fadingOutBlocks.get(i).renderDisappearing();
        }
        grid.render();
        renderHoldPiece();
        renderNextPiece();
        renderCurrentPiece();
    }

    public void renderHoldPiece() {
        if (currentGameType.gameMode == GameType.GameMode.STACK || currentGameType.holdPieceEnabled == false) return;

        float holdBoxX = grid.getXOnScreenNoShake() - 3 * cellW();
        float holdBoxY = grid.getYOnScreenNoShake();
        float holdBoxW = (float) (2 * cellW());
        float holdBoxH = (float) (2 * cellH());

        GLUtils.drawFilledRectXYWH(holdBoxX, holdBoxY, holdBoxW, holdBoxH, 1, 1, 1, 1.0f);
        GLUtils.drawFilledRectXYWH(holdBoxX + 1, holdBoxY + 1, holdBoxW - 2, holdBoxH - 2, 0, 0, 0, 1.0f);

        if (holdPiece != null) {
            float scale = 0.5f;
            float w = (float) cellW();
            float h = (float) cellH();
            float holdX = holdBoxX + 1 * w * scale;
            float holdY = holdBoxY + 1 * h * scale;

            if (holdPiece.getWidth() == 3) holdX -= 0.5f * w * scale;
            if (holdPiece.getWidth() == 4) holdX -= 1 * w * scale;

            for (int i = 0; i < holdPiece.getNumBlocksInCurrentRotation() && i < holdPiece.blocks.size(); i++) {
                Block b = holdPiece.blocks.get(i);
                float blockX = (b.xInPiece - holdPiece.getLowestOffsetX()) * w * scale;
                float blockY = (b.yInPiece - holdPiece.getLowestOffsetY()) * h * scale;
                b.render(holdX + blockX, holdY + blockY, 1.0f, 0.5f, true, false);
            }
        }
    }

    public void renderNextPiece() {
        if (currentGameType.gameMode == GameType.GameMode.STACK || currentGameType.nextPieceEnabled == false) return;
        if (nextPieces.isEmpty()) return;

        float lastPieceX = grid.getXOnScreenNoShake() + grid.getWidth() * cellW() + cellW();
        float nextY = grid.getYOnScreenNoShake();

        for (int i = 0; i < nextPieces.size(); i++) {
            Piece nextPiece = nextPieces.get(i);
            float scale = (i == 0) ? 1.0f : 0.75f;
            float w = cellW() * scale;
            float h = cellH() * scale;

            for (int b = 0; b < nextPiece.getNumBlocksInCurrentRotation() && b < nextPiece.blocks.size(); b++) {
                Block blk = nextPiece.blocks.get(b);
                float bx = lastPieceX + (blk.xInPiece - nextPiece.getLowestOffsetX()) * w;
                float by = nextY + (blk.yInPiece - nextPiece.getLowestOffsetY()) * h;
                blk.render(bx, by, 1.0f, scale, true, false);
            }
            lastPieceX += (nextPiece.getWidth() + 1) * w;
            if (lastPieceX > 1280) break; // Screen edge
        }
    }

    public void renderCurrentPiece() {
        if (currentPiece != null) {
            currentPiece.renderAsCurrentPiece();
        }
    }

    public void renderForeground() {
        grid.renderBlockOutlines();
        if (currentGameType.gameMode == GameType.GameMode.STACK) {
            grid.renderTransparentOverLastRow();
        }
    }

    public int queuedGarbageAmountToSend = 0;

    public GameType Settings() { return currentGameType; }
    public GameType GameType() { return currentGameType; }

    public void resetNextPieces() { currentPiece = null; holdPiece = null; nextPieces.clear(); nextPieceSpecialBuffer.clear(); }
    public void newRandomPiece() { pieceSetAtBottom = false; currentPiece = grid.getRandomPiece(currentGameType.getNormalPieceTypes(getCurrentDifficulty()), currentGameType.getNormalBlockTypes(getCurrentDifficulty())); currentPiece.init(); setCurrentPieceAtTop(); switchedHoldPieceAlready = false; }
    private void setCurrentPieceAtTop() { currentPiece.xGrid = grid.getWidth() / 2; currentPiece.yGrid = -2 + aboveGridBuffer; if (!grid.doesPieceFit(currentPiece, currentPiece.xGrid, currentPiece.yGrid)) died = true; }
    private void setPiece() { grid.setPiece(currentPiece, currentPiece.xGrid, currentPiece.yGrid); pieceSetAtBottom = true; lastPiece = currentPiece; currentPiece = null; }
    public Room getRoom() { return new Room(); }
    public static ArrayList<GameType> getBuiltInGameTypes() { return new ArrayList<>(); }
    public void setGameSequence(GameSequence seq) { this.currentGameSequence = seq; }
    public void deleteAllCaptions() {}
    public int cellW() { return blockWidth + (currentGameType != null ? currentGameType.gridPixelsBetweenColumns : 0); }
    public int cellH() { return blockHeight + (currentGameType != null ? currentGameType.gridPixelsBetweenRows : 0); }
    public int gridW() { return currentGameType != null ? currentGameType.gridWidth : 10; }
    public int gridH() { return (currentGameType != null ? currentGameType.gridHeight : 20) + aboveGridBuffer; }

    public void gotVSGarbageFromOtherPlayer(int amount) {}

    public int getRandomIntLessThan(int i, String s) {
        if (i <= 0) return 0;
        return randomGenerator.nextInt(i);
    }
}
