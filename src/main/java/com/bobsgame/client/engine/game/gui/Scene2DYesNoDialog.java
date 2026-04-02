package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bobsgame.client.engine.Engine;

public class Scene2DYesNoDialog extends Scene2DPanel {
    private Label messageLabel;
    private TextButton yesButton;
    private TextButton noButton;

    public interface YesNoDialogListener {
        void onYes();
        void onNo();
    }

    private YesNoDialogListener listener;

    public Scene2DYesNoDialog(Engine engine, String message, YesNoDialogListener listener) {
        super(engine);
        this.listener = listener;

        content.clear();
        
        messageLabel = new Label(message, engine.uiSkin);
        yesButton = new TextButton("Yes", engine.uiSkin);
        noButton = new TextButton("No", engine.uiSkin);

        content.add(messageLabel).colspan(2).pad(20).row();
        content.add(yesButton).pad(10).uniform().fill();
        content.add(noButton).pad(10).uniform().fill();

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setActivated(false);
                if (listener != null) listener.onYes();
            }
        });

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setActivated(false);
                if (listener != null) listener.onNo();
            }
        });
    }
}
