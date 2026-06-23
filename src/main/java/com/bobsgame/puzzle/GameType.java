package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameType implements Serializable {
    public static final Logger log = LoggerFactory.getLogger(GameType.class);

    public enum GameMode { DROP, STACK }
    public enum DropLockType { HARD_DROP_INSTANT_LOCK, SOFT_DROP_INSTANT_LOCK, NEITHER_INSTANT_LOCK }
    public enum GarbageSpawnRule { NONE, TICKS, LINES_CLEARED, BLOCKS_CLEARED, PIECES_MADE }
    public enum VSGarbageDropRule { FALL_FROM_CEILING_IN_EVEN_ROWS, RISE_FROM_FLOOR_IN_EVEN_ROWS }
    public enum ScoreType { LINES_CLEARED, BLOCKS_CLEARED, PIECES_MADE }
    public enum CursorType { ONE_BLOCK_PICK_UP, TWO_BLOCK_HORIZONTAL, TWO_BLOCK_VERTICAL, THREE_BLOCK_HORIZONTAL, THREE_BLOCK_VERTICAL, QUAD_BLOCK_ROTATE }
    public enum GarbageType { NONE, RANDOM, MATCH_BOTTOM_ROW, ZIGZAG_PATTERN }
    public enum RotationType { SRS, SEGA, NES, GB, DTET }
    public enum GameState { IDLE, READY, PLAYING, PAUSED, GAME_OVER }
    public enum BlockTypes { NORMAL, SPECIAL, GARBAGE, EMPTY }
    public enum SendGarbageToRule { SEND_GARBAGE_TO_ALL_PLAYERS, SEND_GARBAGE_TO_RANDOM_PLAYER, SEND_GARBAGE_TO_PLAYER_WITH_LEAST_BLOCKS, SEND_GARBAGE_TO_EACH_PLAYER_IN_ROTATION, SEND_GARBAGE_TO_ALL_PLAYERS_50_PERCENT_CHANCE }

    public String name = "";
    public String uuid = "";

    public boolean downloaded = false;
    public long creatorUserID = 0;
    public String creatorUserName = "";
    public long dateCreated = 0;
    public long lastModified = 0;
    public long howManyTimesUpdated = 0;
    public long upVotes = 0;
    public long downVotes = 0;
    public String yourVote = "none";

    public GameMode gameMode = GameMode.DROP;

    public int gridWidth = 10;
    public int gridHeight = 20;

    public int gridPixelsBetweenColumns = 0;
    public int gridPixelsBetweenRows = 0;

    public long maxLockDelayTicks = 17 * 30;
    public long spawnDelayTicksAmountPerPiece = 30 * 17;
    public long lineClearDelayTicksAmountPerLine = 100 * 17;
    public long lineClearDelayTicksAmountPerBlock = 10 * 17;
    public long gravityRule_ticksToMoveDownBlocksOverBlankSpaces = 200;
    public boolean moveDownAllLinesOverBlankSpacesAtOnce = false;
    public int removingBlocksDelayTicksBetweenEachBlock = 0;

    public float blockMovementInterpolationTicks = 100;
    public int blockAnimationTicksRandomUpToBetweenLoop = 0;

    public ArrayList<BlockType> blockTypes = new ArrayList<>();
    public ArrayList<PieceType> pieceTypes = new ArrayList<>();
    public ArrayList<DifficultyType> difficultyTypes = new ArrayList<>();

    public String normalMusic = "";
    public String fastMusic = "";
    public String winMusic = "";
    public String loseMusic = "";
    public String deadMusic = "";
    public String creditsMusic = "";

    public String blocksFlashingSound = "slam";
    public String singleLineFlashingSound = "single";
    public String doubleLineFlashingSound = "double";
    public String tripleLineFlashingSound = "triple";
    public String quadLineFlashingSound = "sosumi";
    public String hardDropSwishSound = "";
    public String hardDropClankSound = "slam2";
    public String switchHoldPieceSound = "hold";
    public String cantHoldPieceSound = "buzz";
    public String moveUpSound = "tick";
    public String moveDownSound = "tick";
    public String moveLeftSound = "tick";
    public String moveRightSound = "tick";
    public String pieceSetSound = "lock";
    public String touchBottomSound = "touchblock";
    public String wallKickSound = "wallkick";
    public String doubleWallKickSound = "doublewallkick";
    public String diagonalWallKickSound = "specialwallkick";
    public String floorKickSound = "floorkick";
    public String pieceFlip180Sound = "flip";
    public String rotateSound = "rotate";
    public String levelUpSound = "levelup";
    public String extraStage1Sound = "gtbling";
    public String extraStage2Sound = "gtbling";
    public String extraStage3Sound = "gtbling";
    public String extraStage4Sound = "gtbling";
    public String creditsSound = "gtbling";
    public String deadSound = "gtbling";
    public String winSound = "gtbling";
    public String loseSound = "gtbling";
    public String stackRiseSound = "tick";
    public String readySound = "ready";
    public String goSound = "go";
    public String gotBombSound = "gotBomb";
    public String gotWeightSound = "gotWeight";
    public String gotSubtractorSound = "gotSubtractor";
    public String gotAdderSound = "gotAdder";
    public String flashingClearSound = "flashingClear";
    public String scanlineClearSound = "scanlineClear";

    public boolean useRandomSoundModulation = false;
    public int readyTicksAmount = 2000;

    public boolean nextPieceEnabled = true;
    public int numberOfNextPiecesToShow = 3;
    public boolean holdPieceEnabled = true;
    public boolean resetHoldPieceRotation = true;

    public boolean chainRule_CheckEntireLine = false;
    public int chainRule_AmountPerChain = 0;
    public boolean chainRule_CheckRow = false;
    public boolean chainRule_CheckColumn = false;
    public boolean chainRule_CheckDiagonal = false;
    public boolean chainRule_CheckRecursiveConnections = false;
    public boolean chainRule_CheckTouchingBreakerBlocksChain = false;

    public boolean gravityRule_onlyMoveDownDisconnectedBlocks = false;

    public GarbageType playingFieldGarbageType = GarbageType.ZIGZAG_PATTERN;

    public GarbageSpawnRule playingFieldGarbageSpawnRule = GarbageSpawnRule.NONE;

    public boolean hardDropPunchThroughToLowestValidGridPosition = false;

    public boolean twoSpaceWallKickAllowed = true;
    public boolean diagonalWallKickAllowed = true;
    public boolean pieceClimbingAllowed = true;
    public boolean flip180Allowed = true;
    public boolean floorKickAllowed = true;

    public VSGarbageDropRule vsGarbageDropRule = VSGarbageDropRule.FALL_FROM_CEILING_IN_EVEN_ROWS;

    public ScoreType scoreType = ScoreType.BLOCKS_CLEARED;
    public int scoreTypeAmountPerLevelGained = 4;

    public String rules1 = "";
    public String rules2 = "";
    public String rules3 = "";

    public float bloomIntensity = 1.5f;
    public int bloomTimes = 4;

    public boolean stackDontPutSameColorNextToEachOther = false;
    public boolean stackDontPutSameBlockTypeNextToEachOther = false;
    public boolean stackDontPutSameColorDiagonalOrNextToEachOtherReturnNull = false;
    public boolean stackLeaveAtLeastOneGapPerRow = false;

    public CursorType stackCursorType = CursorType.ONE_BLOCK_PICK_UP;

    public boolean blockRule_drawDotToSquareOffBlockCorners = false;
    public boolean drawDotOnCenterOfRotation = false;

    public boolean gridRule_outlineOpenBlockEdges = false;
    public boolean fadeBlocksDarkerWhenLocking = true;
    public boolean blockRule_drawBlocksDarkerWhenLocked = false;
    public boolean blockRule_fillSolidSquareWhenSetInGrid = false;

    public boolean blockRule_drawBlocksConnectedByPieceIgnoringColor = false;
    public boolean blockRule_drawBlocksConnectedByColorIgnoringPiece = false;
    public boolean blockRule_drawBlocksConnectedByColorInPiece = false;

    public boolean whenGeneratingPieceDontMatchAllBlockColors = false;
    public boolean whenGeneratingPieceDontMatchTwoBlocksOfTheSameSpecialRandomTypeAndColor = false;
    public boolean whenGeneratingPieceDontMatchNormalBlockWithBlockOfDifferentTypeAndSameColor = false;

    public boolean currentPieceOutlineFirstBlockRegardlessOfPosition = false;
    public boolean currentPieceRule_OutlineBlockAtZeroZero = false;
    public boolean currentPieceRule_getNewPiecesRandomlyOutOfBagWithOneOfEachPieceUntilEmpty = false;

    public boolean currentPieceMoveUpHalfABlock = false;
    public boolean currentPieceRenderAsNormalPiece = true;
    public boolean currentPieceRenderHoldingBlock = true;
    public boolean currentPieceOutlineAllPieces = false;

    public static DifficultyType difficulty_BEGINNER = new DifficultyType("Beginner");
    public static DifficultyType difficulty_EASY = new DifficultyType("Easy");
    public static DifficultyType difficulty_NORMAL = new DifficultyType("Normal");
    public static DifficultyType difficulty_HARD = new DifficultyType("Hard");
    public static DifficultyType difficulty_INSANE = new DifficultyType("Insane");
    public static DifficultyType difficulty_IMPOSSIBLE = new DifficultyType("Impossible");

    static {
        difficulty_BEGINNER.initialLineDropSpeedTicks = 2000; difficulty_BEGINNER.minimumLineDropSpeedTicks = 1000; difficulty_BEGINNER.minStackRise = 600; difficulty_BEGINNER.maxStackRise = 1500; difficulty_BEGINNER.extraStage1Level = 5; difficulty_BEGINNER.extraStage2Level = 6; difficulty_BEGINNER.extraStage3Level = 7; difficulty_BEGINNER.extraStage4Level = 8; difficulty_BEGINNER.creditsLevel = 9; difficulty_BEGINNER.playingFieldGarbageSpawnRuleAmount = 10; difficulty_BEGINNER.maximumBlockTypeColors = 4;
        difficulty_EASY.initialLineDropSpeedTicks = 1500; difficulty_EASY.minimumLineDropSpeedTicks = 500; difficulty_EASY.minStackRise = 300; difficulty_EASY.maxStackRise = 800; difficulty_EASY.extraStage1Level = 10; difficulty_EASY.extraStage2Level = 11; difficulty_EASY.extraStage3Level = 12; difficulty_EASY.extraStage4Level = 13; difficulty_EASY.creditsLevel = 15; difficulty_EASY.playingFieldGarbageSpawnRuleAmount = 8; difficulty_EASY.maximumBlockTypeColors = 6;
        difficulty_NORMAL.initialLineDropSpeedTicks = 1000; difficulty_NORMAL.minimumLineDropSpeedTicks = 30; difficulty_NORMAL.minStackRise = 400; difficulty_NORMAL.maxStackRise = 64; difficulty_NORMAL.extraStage1Level = 10; difficulty_NORMAL.extraStage2Level = 15; difficulty_NORMAL.extraStage3Level = 20; difficulty_NORMAL.extraStage4Level = 25; difficulty_NORMAL.creditsLevel = 30; difficulty_NORMAL.playingFieldGarbageSpawnRuleAmount = 5; difficulty_NORMAL.maximumBlockTypeColors = 8;
        difficulty_HARD.initialLineDropSpeedTicks = 500; difficulty_HARD.minimumLineDropSpeedTicks = 20; difficulty_HARD.minStackRise = 15; difficulty_HARD.maxStackRise = 300; difficulty_HARD.extraStage1Level = 20; difficulty_HARD.extraStage2Level = 30; difficulty_HARD.extraStage3Level = 40; difficulty_HARD.extraStage4Level = 50; difficulty_HARD.creditsLevel = 60; difficulty_HARD.playingFieldGarbageSpawnRuleAmount = 4; difficulty_HARD.maximumBlockTypeColors = 8;
        difficulty_INSANE.initialLineDropSpeedTicks = 128; difficulty_INSANE.minimumLineDropSpeedTicks = 8; difficulty_INSANE.minStackRise = 15; difficulty_INSANE.maxStackRise = 200; difficulty_INSANE.extraStage1Level = 50; difficulty_INSANE.extraStage2Level = 60; difficulty_INSANE.extraStage3Level = 70; difficulty_INSANE.extraStage4Level = 80; difficulty_INSANE.creditsLevel = 99; difficulty_INSANE.playingFieldGarbageSpawnRuleAmount = 3; difficulty_INSANE.maximumBlockTypeColors = 8;
        difficulty_IMPOSSIBLE.initialLineDropSpeedTicks = 32; difficulty_IMPOSSIBLE.minimumLineDropSpeedTicks = 2; difficulty_IMPOSSIBLE.minStackRise = 2; difficulty_IMPOSSIBLE.maxStackRise = 128; difficulty_IMPOSSIBLE.extraStage1Level = 50; difficulty_IMPOSSIBLE.extraStage2Level = 60; difficulty_IMPOSSIBLE.extraStage3Level = 70; difficulty_IMPOSSIBLE.extraStage4Level = 80; difficulty_IMPOSSIBLE.creditsLevel = 99; difficulty_IMPOSSIBLE.playingFieldGarbageSpawnRuleAmount = 2; difficulty_IMPOSSIBLE.maximumBlockTypeColors = 8;
    }

    public GameType() {
        this.uuid = UUID.randomUUID().toString();
    }

    public ArrayList<BlockType> getNormalBlockTypes(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.useInNormalPieces && !d.blockTypesToDisallow_UUID.contains(b.uuid)) arr.add(b);
        return arr;
    }

    public ArrayList<BlockType> getGarbageBlockTypes(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.useAsGarbageBlock && !d.blockTypesToDisallow_UUID.contains(b.uuid)) arr.add(b);
        return arr;
    }

    public ArrayList<BlockType> getPlayingFieldBlockTypes(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.useAsPlayingFieldFillerBlock && !d.blockTypesToDisallow_UUID.contains(b.uuid)) arr.add(b);
        return arr;
    }

    public ArrayList<BlockType> getBlockTypesToIgnoreWhenCheckingChain(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.ignoreWhenCheckingChainConnections) arr.add(b);
        return arr;
    }

    public ArrayList<BlockType> getBlockTypesToIgnoreWhenMovingDown(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.ignoreWhenMovingDownBlocks) arr.add(b);
        return arr;
    }

    public ArrayList<BlockType> getBlockTypesChainMustContain(DifficultyType d) {
        ArrayList<BlockType> arr = new ArrayList<>();
        for (BlockType b : blockTypes) if (b.chainConnectionsMustContainAtLeastOneBlockWithThisTrue) arr.add(b);
        return arr;
    }

    public ArrayList<PieceType> getNormalPieceTypes(DifficultyType d) {
        ArrayList<PieceType> arr = new ArrayList<>();
        for (PieceType p : pieceTypes) if (p.useAsNormalPiece && !d.pieceTypesToDisallow_UUID.contains(p.uuid)) arr.add(p);
        return arr;
    }

    public ArrayList<PieceType> getGarbagePieceTypes(DifficultyType d) {
        ArrayList<PieceType> arr = new ArrayList<>();
        for (PieceType p : pieceTypes) if (p.useAsGarbagePiece && !d.pieceTypesToDisallow_UUID.contains(p.uuid)) arr.add(p);
        return arr;
    }

    public ArrayList<PieceType> getPlayingFieldPieceTypes(DifficultyType d) {
        ArrayList<PieceType> arr = new ArrayList<>();
        for (PieceType p : pieceTypes) if (p.useAsPlayingFieldFillerPiece && !d.pieceTypesToDisallow_UUID.contains(p.uuid)) arr.add(p);
        return arr;
    }

    public BlockType getBlockTypeByUUID(String uuid) {
        for (BlockType b : blockTypes) if (b.uuid.equals(uuid)) return b;
        return null;
    }

    public PieceType getPieceTypeByUUID(String uuid) {
        for (PieceType p : pieceTypes) if (p.uuid.equals(uuid)) return p;
        return null;
    }

    public BlockType getBlockTypeByName(String name) {
        for (BlockType b : blockTypes) if (b.name.equals(name)) return b;
        return null;
    }

    public PieceType getPieceTypeByName(String name) {
        for (PieceType p : pieceTypes) if (p.name.equals(name)) return p;
        return null;
    }

    public DifficultyType getDifficultyByName(String name) {
        for (DifficultyType d : difficultyTypes) if (d.name.equalsIgnoreCase(name)) return d;
        if (name.equalsIgnoreCase("Beginner")) return difficulty_BEGINNER;
        if (name.equalsIgnoreCase("Easy")) return difficulty_EASY;
        if (name.equalsIgnoreCase("Hard")) return difficulty_HARD;
        if (name.equalsIgnoreCase("Insane")) return difficulty_INSANE;
        if (name.equalsIgnoreCase("Impossible")) return difficulty_IMPOSSIBLE;
        return difficulty_NORMAL;
    }

    public void tetrid() {}
    public void tetsosumi() {}
    public String toBase64GZippedGSON() {
        return com.bobsgame.net.BobNet.toBase64GZippedGSON(this);
    }
}
