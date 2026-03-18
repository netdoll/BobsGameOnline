package com.bobsgame.client.state;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bobsgame.ClientMain;
import com.bobsgame.client.engine.Engine;
import com.bobsgame.client.engine.game.gui.Scene2DPanel;

public class LoginScreen extends Scene2DPanel {
    private TextField emailField;
    private TextField passwordField;
    private Label errorLabel;
    private Label statusLabel;
    private CheckBox stayLoggedInCheckbox;
    private CheckBox sendStatsCheckbox;
    private TextButton loginButton;
    private TextButton createAccountButton;
    private TextButton forgotPasswordButton;

    private boolean loggedIn = false;

    public LoginScreen(Engine engine) {
        super(engine);
        buildUI();
    }

    private void buildUI() {
        content.clear();

        Label titleLabel = new Label("Login", engine.uiSkin, "bigLabel");
        errorLabel = new Label(" ", engine.uiSkin, "errorLabel");
        statusLabel = new Label(" ", engine.uiSkin);

        emailField = new TextField("", engine.uiSkin);
        passwordField = new TextField("", engine.uiSkin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        stayLoggedInCheckbox = new CheckBox(" Stay Logged In", engine.uiSkin);
        stayLoggedInCheckbox.setChecked(true);
        sendStatsCheckbox = new CheckBox(" Send PC Stats", engine.uiSkin);
        sendStatsCheckbox.setChecked(true);

        loginButton = new TextButton("Login", engine.uiSkin);
        createAccountButton = new TextButton("Create Account", engine.uiSkin);
        forgotPasswordButton = new TextButton("Forgot Password?", engine.uiSkin);

        content.add(titleLabel).colspan(2).pad(20).row();
        content.add(errorLabel).colspan(2).row();
        content.add(statusLabel).colspan(2).row();

        content.add(new Label("Email:", engine.uiSkin)).right().pad(10);
        content.add(emailField).width(300).pad(10).row();

        content.add(new Label("Password:", engine.uiSkin)).right().pad(10);
        content.add(passwordField).width(300).pad(10).row();

        content.add(forgotPasswordButton).pad(10);
        content.add(loginButton).width(100).pad(10).row();

        content.add(stayLoggedInCheckbox).colspan(2).left().padLeft(100).pad(5).row();
        content.add(sendStatsCheckbox).colspan(2).left().padLeft(100).pad(5).row();

        content.add(createAccountButton).colspan(2).pad(20).row();

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doLogin();
            }
        });

        createAccountButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doCreateNewAccount();
            }
        });

        forgotPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                doForgotPassword();
            }
        });
    }

    private void doLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter email and password.");
            return;
        }
        
        // Mock login success
        statusLabel.setText("Logging in...");
        loggedIn = true;
        setActivated(false);
    }

    private void doCreateNewAccount() {
        ClientMain.clientMain.stateManager.setState(ClientMain.clientMain.createNewAccountState);
        ClientMain.clientMain.createNewAccountState.setActivated(true);
    }

    private void doForgotPassword() {
        // TODO: Port logic
    }

    @Override
    public void update(long deltaTicks) {
        super.update(deltaTicks);
        if (loggedIn && !isActivated && !isScrollingDown) {
            ClientMain.clientMain.stateManager.setState(ClientMain.clientMain.clientGameEngine);
        }
    }
}
