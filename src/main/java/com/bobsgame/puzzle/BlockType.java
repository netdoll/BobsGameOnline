package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class BlockType implements Serializable {
    public String name = "";
    public String uuid = "";
    public String description = "";

    public String sprite = "";
    public String spriteName = "";
    public String specialSpriteName = "";

    public BobColor specialColor = null;
    public ArrayList<BobColor> colors = new ArrayList<>();

    public int randomSpecialBlockChanceOneOutOf = 0;
    public int frequencySpecialBlockTypeOnceEveryNPieces = 0;
    public boolean flashingSpecialType = false;

    public ArrayList<String> makePieceTypeWhenCleared_UUID = new ArrayList<>();
    public boolean clearEveryOtherLineOnGridWhenCleared = false;

    public boolean useInNormalPieces = false;
    public boolean useAsGarbageBlock = false;
    public boolean useAsGarbage = false;
    public boolean useAsPlayingFieldFillerBlock = false;
    public boolean useAsPlayingFieldFiller = false;

    public boolean removeAllBlocksOfColorOnFieldBlockIsSetOn = false;
    public boolean changeAllBlocksOfColorOnFieldBlockIsSetOnToDiamondColor = false;

    public boolean pacmanType = false;
    public boolean pacJarType = false;
    public int ticksToChangeDirection = 1000;

    public boolean counterType = false;
    public int turnBackToNormalBlockAfterNPiecesLock = -1;

    public ArrayList<String> ifConnectedUpDownLeftRightToExplodingBlockChangeIntoThisType_UUID = new ArrayList<>();

    public boolean ignoreWhenCheckingChainConnections = false;
    public boolean ignoreWhenMovingDownBlocks = false;
    public boolean chainConnectionsMustContainAtLeastOneBlockWithThisTrue = false;
    public boolean addToChainIfConnectedUpDownLeftRightToExplodingChainBlocks = false;
    public boolean matchAnyColor = false;

    public static class TurnFromBlockTypeToType implements Serializable {
        public String fromType_UUID = "";
        public String toType_UUID = "";
    }

    public ArrayList<TurnFromBlockTypeToType> whenSetTurnAllTouchingBlocksOfFromTypesIntoToTypeAndFadeOut = new ArrayList<>();

    public BlockType() {
        this.uuid = UUID.randomUUID().toString();
    }

    public boolean isNormalType() {
        return useInNormalPieces;
    }

    public boolean isSpecialType() {
        if (randomSpecialBlockChanceOneOutOf != 0) return true;
        if (frequencySpecialBlockTypeOnceEveryNPieces != 0) return true;
        if (flashingSpecialType) return true;
        if (makePieceTypeWhenCleared_UUID != null && !makePieceTypeWhenCleared_UUID.isEmpty()) return true;
        if (clearEveryOtherLineOnGridWhenCleared) return true;
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}
