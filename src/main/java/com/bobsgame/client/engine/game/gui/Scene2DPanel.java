package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bobsgame.client.LWJGLUtils;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.shared.Easing;

public class Scene2DPanel extends Table {
    public Table mainPanel;
    public ScrollPane scrollPane;
    public Table content;

    protected boolean isActivated = false;
    protected boolean isScrollingDown = false;
    protected boolean isScrolledUp = false;

    public int ticksSinceTurnedOn = 0;
    public int ticksSinceTurnedOff = 0;

    public float screenY = 0;
    public float fadeInTime = 600.0f;
    public float fadeOutTime = 1000.0f;

    protected Engine engine;

    public Scene2DPanel(Engine engine) {
        this.engine = engine;
        this.setFillParent(true);
        
        mainPanel = new Table();
        content = new Table();
        scrollPane = new ScrollPane(content, engine.uiSkin);
        
        mainPanel.add(scrollPane).grow();
        this.add(mainPanel).center().size(LWJGLUtils.SCREEN_SIZE_X * 0.9f, LWJGLUtils.SCREEN_SIZE_Y * 0.9f);
        
        this.setVisible(false);
    }

    public void update(long deltaTicks) {
        if (isActivated) {
            if (!isScrollingDown) {
                ticksSinceTurnedOff = 0;
                ticksSinceTurnedOn += deltaTicks;
                scrollUp();
            } else {
                ticksSinceTurnedOn = 0;
                ticksSinceTurnedOff += deltaTicks;
                scrollDown();
            }
        }
    }

    public boolean isActivated() { return isActivated; }
    public boolean isScrollingDown() { return isScrollingDown; }
    public void toggleActivated() { setActivated(!isActivated); }
    public void setEnabled(boolean b) {} // Stub for now

    public void setActivated(boolean b) {
        if (b) {
            isScrollingDown = false;
            isActivated = true;
            ticksSinceTurnedOn = 0;
            screenY = LWJGLUtils.SCREEN_SIZE_Y;
            this.setVisible(true);
            engine.uiStage.addActor(this);
        } else {
            if (isActivated && !isScrollingDown) {
                isScrollingDown = true;
                isScrolledUp = false;
                ticksSinceTurnedOff = 0;
                if (ticksSinceTurnedOn < fadeInTime) {
                    ticksSinceTurnedOff = (int)(fadeOutTime - ((ticksSinceTurnedOn / fadeInTime) * fadeOutTime));
                }
            }
        }
    }

    private void scrollUp() {
        if (ticksSinceTurnedOn <= fadeInTime) {
            screenY = (float) (LWJGLUtils.SCREEN_SIZE_Y - Easing.easeOutCubic(ticksSinceTurnedOn, 0, LWJGLUtils.SCREEN_SIZE_Y, fadeInTime));
            updatePosition();
        } else if (!isScrolledUp) {
            isScrolledUp = true;
            screenY = 0;
            updatePosition();
            onScrolledUp();
        }
    }

    private void scrollDown() {
        if (ticksSinceTurnedOff <= fadeOutTime) {
            screenY = (float) Easing.easeOutParabolicBounce(ticksSinceTurnedOff, 0, LWJGLUtils.SCREEN_SIZE_Y, fadeOutTime);
            updatePosition();
        } else {
            isActivated = false;
            isScrollingDown = false;
            this.setVisible(false);
            this.remove();
        }
    }

    protected void updatePosition() {
        mainPanel.setY(-screenY);
    }

    protected void onScrolledUp() {}
}
