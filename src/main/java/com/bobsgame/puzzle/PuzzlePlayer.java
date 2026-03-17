package com.bobsgame.puzzle;

import com.bobsgame.shared.BobColor;
import java.io.Serializable;

public class PuzzlePlayer implements Serializable {
    public GameLogic gameLogic;

    public boolean confirmed = false;
    
    public boolean selectGameSequenceOrSingleGameTypeMiniMenuShowing = true;
    public boolean selectGameSequenceMiniMenuShowing = false;
    public boolean gameSequenceOptionsMiniMenuShowing = false;
    public boolean selectSingleGameTypeMiniMenuShowing = false;
    public boolean setGameSequence = false;
    public boolean setDifficulty = false;

    public boolean upRepeatedStarted = false;
    public boolean upRepeating = false;
    public long upLastTime = 0;

    public boolean downRepeatedStarted = false;
    public boolean downRepeating = false;
    public long downLastTime = 0;

    public boolean allowAnalogControls = true;
    public boolean slamWithY = true;
    public boolean slamWithR = false;
    public boolean slamWithUp = true;

    public boolean slamLock = true;
    public boolean singleDownLock = false;
    public boolean doubleDownLock = true;

    public float hue = 1.0f;

    // Config
    public boolean gridRule_showWarningForFieldThreeQuartersFilled = true;
    public BobColor gridBorderColor = new BobColor(255, 255, 255);
    public BobColor gridCheckeredBackgroundColor1 = BobColor.black;
    public BobColor gridCheckeredBackgroundColor2 = new BobColor(8, 8, 8);
    public BobColor screenBackgroundColor = BobColor.black;

    // Input state
    public boolean UP_HELD = false;
    public boolean DOWN_HELD = false;
    public boolean LEFT_HELD = false;
    public boolean RIGHT_HELD = false;
    public boolean ROTATECW_HELD = false;
    public boolean ROTATECCW_HELD = false;
    public boolean HOLDRAISE_HELD = false;
    public boolean SLAM_HELD = false;
    public boolean PAUSE_HELD = false;
    public boolean CONFIRM_HELD = false;
    public boolean CANCEL_HELD = false;

    public boolean LAST_UP_HELD = false;
    public boolean LAST_DOWN_HELD = false;
    public boolean LAST_LEFT_HELD = false;
    public boolean LAST_RIGHT_HELD = false;
    public boolean LAST_ROTATECW_HELD = false;
    public boolean LAST_ROTATECCW_HELD = false;
    public boolean LAST_HOLDRAISE_HELD = false;
    public boolean LAST_SLAM_HELD = false;
    public boolean LAST_PAUSE_HELD = false;
    public boolean LAST_CONFIRM_HELD = false;
    public boolean LAST_CANCEL_HELD = false;

    private boolean UP_PRESSED = false;
    private boolean DOWN_PRESSED = false;
    private boolean LEFT_PRESSED = false;
    private boolean RIGHT_PRESSED = false;
    private boolean ROTATECW_PRESSED = false;
    private boolean ROTATECCW_PRESSED = false;
    private boolean HOLDRAISE_PRESSED = false;
    private boolean SLAM_PRESSED = false;
    private boolean PAUSE_PRESSED = false;
    private boolean CONFIRM_PRESSED = false;
    private boolean CANCEL_PRESSED = false;

    public PuzzlePlayer(GameLogic logic) {
        this.gameLogic = logic;
        if (logic != null) logic.player = this;
    }

    public boolean upPressed() { if (UP_PRESSED) { UP_PRESSED = false; return true; } return false; }
    public boolean downPressed() { if (DOWN_PRESSED) { DOWN_PRESSED = false; return true; } return false; }
    public boolean leftPressed() { if (LEFT_PRESSED) { LEFT_PRESSED = false; return true; } return false; }
    public boolean rightPressed() { if (RIGHT_PRESSED) { RIGHT_PRESSED = false; return true; } return false; }
    public boolean rotateCWPressed() { if (ROTATECW_PRESSED) { ROTATECW_PRESSED = false; return true; } return false; }
    public boolean rotateCCWPressed() { if (ROTATECCW_PRESSED) { ROTATECCW_PRESSED = false; return true; } return false; }
    public boolean holdRaisePressed() { if (HOLDRAISE_PRESSED) { HOLDRAISE_PRESSED = false; return true; } return false; }
    public boolean slamPressed() { if (SLAM_PRESSED) { SLAM_PRESSED = false; return true; } return false; }
    public boolean pausePressed() { if (PAUSE_PRESSED) { PAUSE_PRESSED = false; return true; } return false; }
    public boolean confirmPressed() { if (CONFIRM_PRESSED) { CONFIRM_PRESSED = false; return true; } return false; }
    public boolean cancelPressed() { if (CANCEL_PRESSED) { CANCEL_PRESSED = false; return true; } return false; }

    public void resetPressedButtons() {
        UP_PRESSED = false; DOWN_PRESSED = false; LEFT_PRESSED = false; RIGHT_PRESSED = false;
        ROTATECW_PRESSED = false; ROTATECCW_PRESSED = false; HOLDRAISE_PRESSED = false; SLAM_PRESSED = false;
        PAUSE_PRESSED = false; CONFIRM_PRESSED = false; CANCEL_PRESSED = false;
    }

    public void setButtonStates() {
        LAST_UP_HELD = UP_HELD; LAST_DOWN_HELD = DOWN_HELD; LAST_LEFT_HELD = LEFT_HELD; LAST_RIGHT_HELD = RIGHT_HELD;
        LAST_ROTATECW_HELD = ROTATECW_HELD; LAST_ROTATECCW_HELD = ROTATECCW_HELD; LAST_HOLDRAISE_HELD = HOLDRAISE_HELD;
        LAST_SLAM_HELD = SLAM_HELD; LAST_PAUSE_HELD = PAUSE_HELD; LAST_CONFIRM_HELD = CONFIRM_HELD; LAST_CANCEL_HELD = CANCEL_HELD;

        UP_HELD = false; DOWN_HELD = false; LEFT_HELD = false; RIGHT_HELD = false;
        ROTATECW_HELD = false; ROTATECCW_HELD = false; HOLDRAISE_HELD = false; SLAM_HELD = false;
        PAUSE_HELD = false; CONFIRM_HELD = false; CANCEL_HELD = false;
    }

    public void setPressedButtons() {
        if (UP_HELD && !LAST_UP_HELD) UP_PRESSED = true;
        if (DOWN_HELD && !LAST_DOWN_HELD) DOWN_PRESSED = true;
        if (LEFT_HELD && !LAST_LEFT_HELD) LEFT_PRESSED = true;
        if (RIGHT_HELD && !LAST_RIGHT_HELD) RIGHT_PRESSED = true;
        if (ROTATECW_HELD && !LAST_ROTATECW_HELD) ROTATECW_PRESSED = true;
        if (ROTATECCW_HELD && !LAST_ROTATECCW_HELD) ROTATECCW_PRESSED = true;
        if (HOLDRAISE_HELD && !LAST_HOLDRAISE_HELD) HOLDRAISE_PRESSED = true;
        if (SLAM_HELD && !LAST_SLAM_HELD) SLAM_PRESSED = true;
        if (PAUSE_HELD && !LAST_PAUSE_HELD) PAUSE_PRESSED = true;
        if (CONFIRM_HELD && !LAST_CONFIRM_HELD) CONFIRM_PRESSED = true;
        if (CANCEL_HELD && !LAST_CANCEL_HELD) CANCEL_PRESSED = true;
    }
}
