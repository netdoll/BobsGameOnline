package com.bobsgame.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.bobsgame.client.engine.game.nd.ND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibGDXApp extends ApplicationAdapter {
    public static final Logger log = LoggerFactory.getLogger(LibGDXApp.class);

    private BobsGame bobsGame;
    private ND nD;

    @Override
    public void create() {
        GLUtils.init();
        nD = new ND();
        bobsGame = new BobsGame(nD);
        bobsGame.init();
        log.info("LibGDXApp initialized");
    }

    @Override
    public void render() {
        bobsGame.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        GLUtils.batch.begin();
        bobsGame.render();
        GLUtils.drawOutlinedString("bobsgame LibGDX", 10, 10, com.bobsgame.shared.BobColor.white);
        GLUtils.batch.end();
    }

    @Override
    public void dispose() {
        GLUtils.dispose();
    }
}
