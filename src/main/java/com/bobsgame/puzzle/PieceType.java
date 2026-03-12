package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class PieceType implements Serializable {
    public String name = "";
    public String uuid = "";
    public String description = "";

    public BobColor color = null;
    public Piece.RotationSet rotationSet = new Piece.RotationSet("");

    public int frequencySpecialPieceTypeOnceEveryNPieces = 0;
    public int randomSpecialPieceChanceOneOutOf = 0;
    public boolean flashingSpecialType = false;
    public boolean clearEveryRowPieceIsOnIfAnySingleRowCleared = false;
    public int turnBackToNormalPieceAfterNPiecesLock = -1;
    public boolean fadeOutOnceSetInsteadOfAddedToGrid = false;

    public boolean useAsNormalPiece = false;
    public boolean useAsGarbagePiece = false;
    public boolean useAsPlayingFieldFillerPiece = false;
    public boolean disallowAsFirstPiece = false;

    public String spriteName = "";

    public boolean bombPiece = false;
    public boolean weightPiece = false;
    public boolean pieceRemovalShooterPiece = false;
    public boolean pieceShooterPiece = false;

    public ArrayList<String> overrideBlockTypes_UUID = new ArrayList<>();

    public static PieceType emptyPieceType = new PieceType();
    public static PieceType oneBlockCursorPieceType = new PieceType("1 Block Cursor", "", null, 1, Piece.get1BlockCursorRotationSet(), 0, 0);
    public static PieceType twoBlockHorizontalCursorPieceType = new PieceType("2 Block Horizontal Cursor", "", null, 2, Piece.get2BlockHorizontalCursorRotationSet(), 0, 0);
    public static PieceType twoBlockVerticalCursorPieceType = new PieceType("2 Block Vertical Cursor", "", null, 2, Piece.get2BlockVerticalCursorRotationSet(), 0, 0);
    public static PieceType threeBlockHorizontalCursorPieceType = new PieceType("3 Block Horizontal Cursor", "", null, 3, Piece.get3BlockHorizontalCursorRotationSet(), 0, 0);
    public static PieceType threeBlockVerticalCursorPieceType = new PieceType("3 Block Vertical Cursor", "", null, 3, Piece.get3BlockVerticalCursorRotationSet(), 0, 0);
    public static PieceType fourBlockCursorPieceType = new PieceType("4 Block Cursor", "", null, 4, Piece.get4BlockCursorRotationSet(), 0, 0);
    public static PieceType threeBlockVerticalSwapPieceType = new PieceType("3 Block Vertical Swap", "", null, 3, Piece.get3BlockVerticalRotationSet(), 0, 0);
    public static PieceType threeBlockHorizontalSwapPieceType = new PieceType("3 Block Horizontal Swap", "", null, 3, Piece.get3BlockHorizontalRotationSet(), 0, 0);
    public static PieceType threeBlockTPieceType = new PieceType("3 Block T", "", null, 3, Piece.get3BlockTRotationSet(), 0, 0);
    public static PieceType threeBlockLPieceType = new PieceType("3 Block L", "", null, 3, Piece.get3BlockLRotationSet(), 0, 0);
    public static PieceType threeBlockJPieceType = new PieceType("3 Block J", "", null, 3, Piece.get3BlockJRotationSet(), 0, 0);
    public static PieceType threeBlockIPieceType = new PieceType("3 Block I", "", null, 3, Piece.get3BlockIRotationSet(), 0, 0);
    public static PieceType threeBlockCPieceType = new PieceType("3 Block C", "", null, 3, Piece.get3BlockCRotationSet(), 0, 0);
    public static PieceType threeBlockDPieceType = new PieceType("3 Block D", "", null, 3, Piece.get3BlockDRotationSet(), 0, 0);
    public static PieceType fourBlockOPieceType = new PieceType("4 Block O", "", null, 4, Piece.get4BlockORotationSet(), 0, 0);
    public static PieceType fourBlockSolidPieceType = new PieceType("4 Block Solid", "", null, 4, Piece.get4BlockSolidRotationSet(), 0, 0);
    public static PieceType nineBlockSolidPieceType = new PieceType("9 Block Solid", "", null, 9, Piece.get9BlockSolidRotationSet(), 0, 0);
    public static PieceType fourBlockIPieceType = new PieceType("4 Block I (SRS)", "", null, 4, Piece.get4BlockIRotationSet(Piece.RotationType.SRS), 0, 0);
    public static PieceType fourBlockJPieceType = new PieceType("4 Block J (SRS)", "", null, 4, Piece.get4BlockJRotationSet(Piece.RotationType.SRS), 0, 0);
    public static PieceType fourBlockLPieceType = new PieceType("4 Block L (SRS)", "", null, 4, Piece.get4BlockLRotationSet(Piece.RotationType.SRS), 0, 0);
    public static PieceType fourBlockSPieceType = new PieceType("4 Block S (SRS)", "", null, 4, Piece.get4BlockSRotationSet(Piece.RotationType.SRS), 0, 0);
    public static PieceType fourBlockTPieceType = new PieceType("4 Block T (SRS)", "", null, 4, Piece.get4BlockTRotationSet(Piece.RotationType.SRS), 0, 0);
    public static PieceType fourBlockZPieceType = new PieceType("4 Block Z (SRS)", "", null, 4, Piece.get4BlockZRotationSet(Piece.RotationType.SRS), 0, 0);

    public PieceType() {
        this.name = "empty";
        this.uuid = UUID.randomUUID().toString();
        Piece.Rotation r = new Piece.Rotation();
        r.add(new Piece.BlockOffset(0, 0));
        this.rotationSet.add(r);
    }

    public PieceType(String name, String spriteName, BobColor color, int numBlocks, Piece.RotationSet rotationSet, int randomSpecialPieceChanceOneOutOf, int frequencySpecialPieceTypeOnceEveryNPieces) {
        this.name = name;
        this.uuid = UUID.randomUUID().toString();
        this.spriteName = spriteName;
        this.color = color;
        this.rotationSet = rotationSet;
        this.randomSpecialPieceChanceOneOutOf = randomSpecialPieceChanceOneOutOf;
        this.frequencySpecialPieceTypeOnceEveryNPieces = frequencySpecialPieceTypeOnceEveryNPieces;
    }
}
