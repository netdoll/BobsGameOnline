package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bobsgame.client.engine.Engine;

public class Scene2DNumberDialog extends Scene2DPanel {
    private Label titleLabel;
    private TextField textField;
    private TextButton okButton;
    private Integer result = null;
    private boolean confirmed = false;

    public interface NumberDialogListener {
        void onResult(int value);
        void onCancel();
    }

    private NumberDialogListener listener;

    public Scene2DNumberDialog(Engine engine, String title, int initialValue, NumberDialogListener listener) {
        super(engine);
        this.listener = listener;

        content.clear();
        
        titleLabel = new Label(title, engine.uiSkin);
        textField = new TextField(String.valueOf(initialValue), engine.uiSkin);
        textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        okButton = new TextButton("OK", engine.uiSkin);

        content.add(titleLabel).pad(10).row();
        content.add(textField).width(200).pad(10).row();
        content.add(okButton).pad(10).row();

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    result = Integer.parseInt(textField.getText());
                    confirmed = true;
                    setActivated(false);
                    if (listener != null) listener.onResult(result);
                } catch (NumberFormatException e) {
                    // Ignore or show error
                }
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
