package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bobsgame.client.engine.Engine;

public class Scene2DStringDialog extends Scene2DPanel {
    private Label titleLabel;
    private TextField textField;
    private TextButton okButton;
    private String result = null;
    private boolean confirmed = false;

    public interface StringDialogListener {
        void onResult(String text);
        void onCancel();
    }

    private StringDialogListener listener;

    public Scene2DStringDialog(Engine engine, String title, String initialValue, StringDialogListener listener) {
        super(engine);
        this.listener = listener;

        content.clear();
        
        titleLabel = new Label(title, engine.uiSkin);
        textField = new TextField(initialValue, engine.uiSkin);
        okButton = new TextButton("OK", engine.uiSkin);

        content.add(titleLabel).pad(10).row();
        content.add(textField).width(400).pad(10).row();
        content.add(okButton).pad(10).row();

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                result = textField.getText();
                confirmed = true;
                setActivated(false);
                if (listener != null) listener.onResult(result);
            }
        });
    }

    @Override
    public void setActivated(boolean b) {
        super.setActivated(b);
        if (b) {
            engine.uiStage.setKeyboardFocus(textField);
            textField.selectAll();
        } else {
            if (!confirmed && listener != null) {
                listener.onCancel();
            }
        }
    }
}
