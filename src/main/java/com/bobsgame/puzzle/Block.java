package com.bobsgame.puzzle;

import com.bobsgame.client.engine.entity.Sprite;
import com.bobsgame.shared.BobColor;
import com.bobsgame.client.GLUtils;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Block {
    public static final Logger log = LoggerFactory.getLogger(Block.class);

    public GameLogic game = null;
    public Grid grid = null;

    public Piece piece = null;
    public ArrayList<Block> connectedBlocksByPiece = new ArrayList<>();
    public ArrayList<Block> connectedBlocksByColor = new ArrayList<>();

    public int xInPiece = 0;
    public int yInPiece = 0;

    public int xGrid = -1;
    public int yGrid = -1;

    public BlockType blockType = null;

    private BobColor color = null;
    public BobColor overrideColor = null;

    public float effectAlphaFrom = 0.5f;
    public float effectAlphaTo = 0.8f;
    public long effectFadeTicksPerPhase = 1000;
    private float effectAlpha = 0.5f;
    private long effectFadeTicks = 0;
    private boolean effectFadeInOutToggle = false;

    public float colorFlashFrom = 0.0f;
    public float colorFlashTo = 1.0f;
    public long colorFlashTicksPerPhase = 100;
    private float colorFlash = 0.0f;
    private long colorFlashTicks = 0;
    private boolean colorFlashInOutToggle = false;

    public boolean overrideAnySpecialBehavior = false;

    public int interpolateSwappingWithX = 0;
    public int interpolateSwappingWithY = 0;
    public long swapTicks = 0;
    public boolean flashingToBeRemoved = false;
    public boolean flashingToBeRemovedLightDarkToggle = false;

    public boolean setInGrid = false;
    public boolean locking = false;
    public int lockingAnimationFrame = 0;
    public long lockAnimationFrameTicks = 0;

    public boolean fadingOut = false;
    public float disappearingAlpha = 1.0f;

    public float lastScreenX = -1;
    public float lastScreenY = -1;

    public long ticksSinceLastMovement = 0;

    public boolean slamming = false;
    private long ticksSinceSlam = 0;
    public float slamX = 0;
    public float slamY = 0;

    public AnimationState animationState = AnimationState.NORMAL;
    public int animationFrame = 0;
    public long animationFrameTicks = 0;
    public int animationFrameSpeed = 100;

    public int counterCount = -2;

    public boolean didFlashingColoredDiamond = false;
    public boolean ateBlocks = false;
    public int direction = -1;
    public long directionChangeTicksCounter = 0;

    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;

    private int customInterpolationTicks = -1;

    public boolean popping = false;
    public boolean panic = false;

    public boolean connectedUp = false;
    public boolean connectedDown = false;
    public boolean connectedLeft = false;
    public boolean connectedRight = false;

    public boolean connectedUpRight = false;
    public boolean connectedDownRight = false;
    public boolean connectedUpLeft = false;
    public boolean connectedDownLeft = false;

    public Block() {}

    public enum AnimationState {
        NORMAL,
        DROPPING,
        TOUCHING_BOTTOM,
        SET_AT_BOTTOM,
        FLASHING,
        REMOVING,
        PRESSURE
    }

    public Block(GameLogic gameInstance, Grid grid, Piece piece, BlockType blockType) {
        this.game = gameInstance;
        this.grid = grid;
        this.piece = piece;
        this.blockType = blockType;
        this.color = new BobColor();
    }

    public void update() {
        effectFadeTicks += game.ticks();
        ticksSinceLastMovement += game.ticks();

        if (slamming) {
            ticksSinceSlam += game.ticks();
            if (ticksSinceSlam >= 100) {
                slamming = false;
            }
        }

        if (locking) {
            lockAnimationFrameTicks += game.ticks();
            if (lockAnimationFrameTicks > 20) {
                lockAnimationFrameTicks = 0;
                if (lockingAnimationFrame < 8) {
                    lockingAnimationFrame++;
                } else {
                    lockingAnimationFrame = 0;
                    locking = false;
                }
            }
        }

        if (fadingOut) {
            if (disappearingAlpha > 0.0f) {
                disappearingAlpha -= (float)game.ticks() * 0.005f;
            }
            if (disappearingAlpha < 0.0f) disappearingAlpha = 0.0f;

            if (disappearingAlpha == 0.0f) {
                fadingOut = false;
                if(game.fadingOutBlocks.contains(this)) game.fadingOutBlocks.remove(this);
            }
        }
    }

    public void setXYOffsetInPiece(int x, int y) {
        this.xInPiece = x;
        this.yInPiece = y;
    }

    public void breakConnectionsInPiece() {
        connectedBlocksByPiece.clear();
        connectedBlocksByColor.clear();
    }

    public float getScreenX() {
        if (grid == null) return 0;
        return grid.getXInFBO() + xGrid * grid.cellW();
    }

    public float getScreenY() {
        if (grid == null) return 0;
        return grid.getYInFBO() + yGrid * grid.cellH() + (grid.scrollPlayingFieldY / grid.scrollBlockIncrement) * grid.cellH();
    }

    public BobColor getColor() {
        if (overrideColor != null) return overrideColor;
        return color;
    }

    public void setColor(BobColor color) {
        this.color = color;
    }

    public BobColor specialColor() {
        if (blockType != null && blockType.isSpecialType()) return color;
        return null;
    }

    public void setRandomBlockTypeColor() {
        if (blockType != null && !blockType.colors.isEmpty()) {
            int index = game.getRandomIntLessThan(blockType.colors.size(), "Block.setRandomColor");
            this.color = blockType.colors.get(index);
        }
    }

    public void render(float screenX, float screenY, float a, float scale, boolean interpolate, boolean ghost) {
        BobColor renderColor = getColor();
        if (renderColor == null) renderColor = BobColor.gray;
        if (overrideAnySpecialBehavior == false && blockType != null && blockType.specialColor != null) renderColor = blockType.specialColor;

        float w = grid.cellW() * scale;
        float h = grid.cellH() * scale;

        if (interpolateSwappingWithX != 0) {
            float ratio = (float) swapTicks / (17 * 6);
            screenX += (interpolateSwappingWithX * grid.cellW() * ratio);
        }
        if (interpolateSwappingWithY != 0) {
            float ratio = (float) swapTicks / (17 * 6);
            screenY += (interpolateSwappingWithY * grid.cellH() * ratio);
        }

        BobColor textureColor = new BobColor(renderColor);
        if (game.currentGameType.fadeBlocksDarkerWhenLocking && locking) {
            for (int i = 0; i < lockingAnimationFrame; i++) textureColor.darker(0.1f);
            if (lockingAnimationFrame > 5) textureColor = new BobColor(BobColor.white);
        } else if (game.currentGameType.blockRule_drawBlocksDarkerWhenLocked && setInGrid && !flashingToBeRemoved) {
            textureColor.darker(0.5f);
        }

        if (flashingToBeRemoved) {
            if (flashingToBeRemovedLightDarkToggle) textureColor.lighter(); else textureColor.darker();
        }

        if (slamming && ticksSinceSlam < 100) {
            float xDiff = screenX - slamX;
            float yDiff = screenY - slamY;
            screenX = slamX; screenY = slamY;
            w += xDiff; h += yDiff;
        }

        if (blockType != null && blockType.spriteName != null && !blockType.spriteName.isEmpty()) {
            Sprite s = game.manager.getSpriteManager().getSpriteByNameOrRequestFromServerIfNotExist(blockType.spriteName);
            if (s != null && s.texture != null) {
                s.drawFrame(animationFrame, screenX, screenX + w, screenY, screenY + h, textureColor.rf(), textureColor.gf(), textureColor.bf(), a, GLUtils.FILTER_NEAREST);
            } else {
                GLUtils.drawFilledRectXYWH(screenX, screenY, w, h, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
            }
        } else {
            GLUtils.drawFilledRectXYWH(screenX, screenY, w, h, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
        }
        
        if (game.currentGameType.blockRule_drawDotToSquareOffBlockCorners) {
            float s = w * 0.04f;
            if (connectedDown && connectedRight && !connectedDownRight) GLUtils.drawFilledRectXYWH(screenX + w - s, screenY + h - s, s, s, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
            if (connectedDown && connectedLeft && !connectedDownLeft) GLUtils.drawFilledRectXYWH(screenX, screenY + h - s, s, s, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
            if (connectedUp && connectedLeft && !connectedUpLeft) GLUtils.drawFilledRectXYWH(screenX, screenY, s, s, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
            if (connectedUp && connectedRight && !connectedUpRight) GLUtils.drawFilledRectXYWH(screenX + w - s, screenY, s, s, textureColor.rf(), textureColor.gf(), textureColor.bf(), a);
        }

        if (game.currentGameType.drawDotOnCenterOfRotation && xInPiece == 0 && yInPiece == 0) {
            BobColor dotColor = new BobColor(renderColor); dotColor.lighter(); dotColor.lighter();
            GLUtils.drawFilledRectXYWH(screenX + 3 * scale, screenY + 3 * scale, w - 6 * scale, h - 6 * scale, dotColor.rf(), dotColor.gf(), dotColor.bf(), a);
        }

        GLUtils.drawBox(screenX, screenX + w, screenY, screenY + h, 0, 0, 0);
    }

    public void renderDisappearing() {
        render(getScreenX(), getScreenY(), disappearingAlpha, 1.0f + (2.0f - (disappearingAlpha * 2.0f)), true, false);
    }

    public void renderOutlines(float screenX, float screenY, float s) {
        float w = grid.cellW() * s;
        float h = grid.cellH() * s;

        if (setInGrid && !fadingOut) {
            float gridAlpha = 1.0f;
            int gridOutlineWidth = 2;

            for (int j = 0; j < gridOutlineWidth; j++) {
                float i = (float) j;
                if (xGrid - 1 < 0 || grid.get(xGrid - 1, yGrid) == null) GLUtils.drawFilledRectXYWH(screenX - i, screenY, 1, h, 1, 1, 1, gridAlpha);
                if (xGrid + 1 >= grid.getWidth() || grid.get(xGrid + 1, yGrid) == null) GLUtils.drawFilledRectXYWH(screenX + w + i, screenY, 1, h, 1, 1, 1, gridAlpha);
                if (yGrid - 1 < 0 || grid.get(xGrid, yGrid - 1) == null) GLUtils.drawFilledRectXYWH(screenX, screenY - i, w, 1, 1, 1, 1, gridAlpha);
                if (yGrid + 1 >= grid.getHeight() || grid.get(xGrid, yGrid + 1) == null) GLUtils.drawFilledRectXYWH(screenX, screenY + h + i, w, 1, 1, 1, 1, gridAlpha);
            }
        }
    }
}
