package com.bobsgame.client.state;

import com.bobsgame.client.engine.Engine;

public class LoginState extends State {
	public LoginScreen loginScreen = null;

	public LoginState(Engine engine) {
		loginScreen = new LoginScreen(engine);
	}

	public void update() {
		loginScreen.update(engineTicksPassed());
	}

	public void render() {
		//loginScreen.renderBefore();
		//loginScreen.render();
	}

	public void cleanup() {
	}
}
