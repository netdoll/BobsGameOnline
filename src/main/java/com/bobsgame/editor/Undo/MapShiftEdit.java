package com.bobsgame.editor.Undo;

import com.bobsgame.editor.Project.Map.Map;

public class MapShiftEdit extends AbstractUndoableEdit {
    private Map map;
    private int dx;
    private int dy;

    public MapShiftEdit(Map map, int dx, int dy) {
        this.map = map;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void undo() {
        super.undo();
        map.shiftMap(-dx, -dy);
        if (com.bobsgame.EditorMain.mapCanvas.useLayerImageBuffer) {
            map.updateAllLayerBufferImages();
        }
        com.bobsgame.EditorMain.mapCanvas.updateAndRepaintAllLayerImagesIntoMapCanvasImageAndRepaintMapCanvas();
    }

    @Override
    public void redo() {
        super.redo();
        map.shiftMap(dx, dy);
        if (com.bobsgame.EditorMain.mapCanvas.useLayerImageBuffer) {
            map.updateAllLayerBufferImages();
        }
        com.bobsgame.EditorMain.mapCanvas.updateAndRepaintAllLayerImagesIntoMapCanvasImageAndRepaintMapCanvas();
    }

    @Override
    public String getPresentationName() {
        return "Shift Map (" + dx + ", " + dy + ")";
    }
}
