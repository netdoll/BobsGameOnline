package com.bobsgame.client.engine.game.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.bobsgame.client.engine.Engine;

public class Scene2DRenameDialog extends Scene2DPanel {
    private Label titleLabel;
    private TextField textField;
    private TextButton okButton;
    private TextButton cancelButton;
    private boolean confirmed = false;

    public interface RenameDialogListener {
        void onResult(String text);
        void onCancel();
    }

    private RenameDialogListener listener;

    public Scene2DRenameDialog(Engine engine, String initialValue, RenameDialogListener listener) {
        super(engine);
        this.listener = listener;

        content.clear();
        
        titleLabel = new Label("Rename", engine.uiSkin);
        textField = new TextField(initialValue, engine.uiSkin);
        okButton = new TextButton("OK", engine.uiSkin);
        cancelButton = new TextButton("Cancel", engine.uiSkin);

        content.add(titleLabel).colspan(2).pad(10).row();
        content.add(textField).colspan(2).width(400).pad(10).row();
        content.add(okButton).pad(10).uniform().fill();
        content.add(cancelButton).pad(10).uniform().fill();

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (isAcceptable(textField.getText())) {
                    confirmed = true;
                    setActivated(false);
                    if (listener != null) listener.onResult(textField.getText());
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setActivated(false);
            }
        });
    }

    private boolean isAcceptable(String ns) {
        for(int i = 0; i < ns.length(); i++) {
            char c = ns.charAt(i);
            if(!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == '_'))) {
                return false;
            }
        }
        return true;
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
