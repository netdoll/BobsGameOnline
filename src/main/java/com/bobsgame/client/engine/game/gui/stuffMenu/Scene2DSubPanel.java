package com.bobsgame.client.engine.game.gui.stuffMenu;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bobsgame.client.engine.Engine;

public class Scene2DSubPanel extends Table {
    public ScrollPane scrollPane;
    public Table content;
    protected Engine engine;

    public Scene2DSubPanel(Engine engine) {
        this.engine = engine;
        this.setFillParent(true);
        
        content = new Table();
        scrollPane = new ScrollPane(content, engine.uiSkin);
        
        this.add(scrollPane).grow();
        this.setVisible(false);
    }

    public void init() {}
    public void update(long deltaTicks) {}
    public void render() {}
}
