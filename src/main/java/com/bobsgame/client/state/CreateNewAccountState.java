package com.bobsgame.client.state;

import com.bobsgame.client.engine.Engine;

public class CreateNewAccountState extends State {
	public CreateNewAccount createNewAccount = null;

	public CreateNewAccountState(Engine engine) {
		createNewAccount = new CreateNewAccount(engine);
	}

	public void update() {
		createNewAccount.update(engineTicksPassed());
	}

	public void render() {
		//createNewAccount.renderBefore();
		//createNewAccount.render();
	}

	public void cleanup() {
	}

	public boolean isActivated() { return createNewAccount.isActivated(); }
	public void setActivated(boolean b) { createNewAccount.setActivated(b); }
}
