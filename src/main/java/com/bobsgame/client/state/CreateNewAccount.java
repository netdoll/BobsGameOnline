package com.bobsgame.client.state;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.ClientMain;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;

public class CreateNewAccount extends Scene2DPanel {
    private TextField emailField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private Label errorLabel;
    private Label statusLabel;
    private TextButton okButton;
    private TextButton cancelButton;

    public CreateNewAccount(Engine engine) {
        super(engine);
        buildUI();
    }

    private void buildUI() {
        content.clear();
        
        Label titleLabel = new Label("Create Account", engine.uiSkin, "bigLabel");
        errorLabel = new Label(" ", engine.uiSkin, "errorLabel");
        statusLabel = new Label(" ", engine.uiSkin);

        emailField = new TextField("", engine.uiSkin);
        passwordField = new TextField("", engine.uiSkin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        confirmPasswordField = new TextField("", engine.uiSkin);
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        okButton = new TextButton("Ok!", engine.uiSkin);
        cancelButton = new TextButton("Cancel", engine.uiSkin);

        content.add(titleLabel).colspan(2).pad(20).row();
        content.add(errorLabel).colspan(2).row();
        content.add(statusLabel).colspan(2).row();
        
        content.add(new Label("Email Address:", engine.uiSkin)).right().pad(10);
        content.add(emailField).width(300).pad(10).row();
        
        content.add(new Label("Password:", engine.uiSkin)).right().pad(10);
        content.add(passwordField).width(300).pad(10).row();
        
        content.add(new Label("Confirm Password:", engine.uiSkin)).right().pad(10);
        content.add(confirmPasswordField).width(300).pad(10).row();
        
        content.add(okButton).pad(20);
        content.add(cancelButton).pad(20).row();

        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doCreateAccount();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doCancel();
            }
        });
    }

    private void doCancel() {
        setActivated(false);
    }

    private void doCreateAccount() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill out all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        // Mock account creation success
        statusLabel.setText("Account created! Redirecting to login...");
        setActivated(false);
    }

    @Override
    public void update(long deltaTicks) {
        super.update(deltaTicks);
        if (!isActivated && !isScrollingDown) {
            if (ClientMain.introMode) {
                ClientMain.clientMain.stateManager.setState(ClientMain.clientMain.youWillBeNotifiedState);
            } else {
                ClientMain.clientMain.stateManager.setState(ClientMain.clientMain.loginState);
            }
        }
    }
}
