package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class PieceType implements Serializable {
    public static PieceType emptyPieceType = new PieceType();
    public static PieceType oneBlockCursorPieceType = new PieceType("cursorPiece", "", null, 1, Piece.get1BlockCursorRotationSet());
    public static PieceType twoBlockHorizontalCursorPieceType = new PieceType("cursorPiece", "", null, 2, Piece.get2BlockHorizontalCursorRotationSet());
    public static PieceType twoBlockVerticalCursorPieceType = new PieceType("cursorPiece", "", null, 2, Piece.get2BlockVerticalCursorRotationSet());
    public static PieceType threeBlockHorizontalCursorPieceType = new PieceType("cursorPiece", "", null, 2, Piece.get3BlockHorizontalCursorRotationSet());
    public static PieceType threeBlockVerticalCursorPieceType = new PieceType("cursorPiece", "", null, 2, Piece.get3BlockVerticalCursorRotationSet());
    public static PieceType fourBlockCursorPieceType = new PieceType("cursorPiece", "", null, 4, Piece.get4BlockCursorRotationSet());

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

    public boolean useAsNormalPiece = true;
    public boolean useAsGarbagePiece = false;
    public boolean useAsPlayingFieldFillerPiece = false;
    public boolean disallowAsFirstPiece = false;

    public String spriteName = "";

    public boolean bombPiece = false;
    public boolean weightPiece = false;
    public boolean pieceRemovalShooterPiece = false;
    public boolean pieceShooterPiece = false;

    public ArrayList<String> overrideBlockTypes_UUID = new ArrayList<>();

    public PieceType() {
        this.uuid = UUID.randomUUID().toString();
        Piece.Rotation r = new Piece.Rotation();
        r.blockOffsets.add(new Piece.BlockOffset(0, 0));
        this.rotationSet.add(r);
    }

    public PieceType(String name, String description, BobColor color, int numBlocks, Piece.RotationSet rotationSet) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.color = color;
        this.rotationSet = rotationSet;
    }

    public PieceType(String name, String description, BobColor color, int numBlocks, Piece.RotationSet rotationSet, int randomSpecialPieceChanceOneOutOf, int frequencySpecialPieceTypeOnceEveryNPieces) {
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.color = color;
        this.rotationSet = rotationSet;
        this.randomSpecialPieceChanceOneOutOf = randomSpecialPieceChanceOneOutOf;
        this.frequencySpecialPieceTypeOnceEveryNPieces = frequencySpecialPieceTypeOnceEveryNPieces;
    }
}
