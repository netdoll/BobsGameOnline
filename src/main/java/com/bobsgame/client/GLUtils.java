package com.bobsgame.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bobsgame.shared.BobColor;
import java.util.HashMap;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GLUtils {
    public static final Logger log = LoggerFactory.getLogger(GLUtils.class);

    public static SpriteBatch batch;
    public static ShapeRenderer shapeRenderer;
    public static BitmapFont font;

    public static Texture blankTexture;
    public static Texture boxTexture;

    public static int texturesLoaded = 0;
    public static long textureBytesLoaded = 0;

    public static float globalDrawScale = 1.0f;

    public static final int FILTER_FBO_LINEAR_NO_MIPMAPPING = -2;
    public static final int FILTER_FBO_NEAREST_NO_MIPMAPPING = -1;
    public static final int FILTER_NEAREST = 0;
    public static final int FILTER_LINEAR = 1;

    private static HashMap<String, Texture> textureCache = new HashMap<>();

    public static void init() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blankTexture = new Texture(pixmap);
        pixmap.dispose();

        try {
            if (Gdx.files.internal("res/fonts/bobsgame.fnt").exists()) {
                font = new BitmapFont(Gdx.files.internal("res/fonts/bobsgame.fnt"));
            } else {
                font = new BitmapFont();
            }
            boxTexture = loadTexture("res/misc/box.png");
        } catch (Exception e) {
            log.error("Error initializing GLUtils", e);
        }
    }

    public static void initSkin(Skin skin) {
        if (font == null) init();
        skin.add("default", font);

        TextureRegionDrawable white = new TextureRegionDrawable(new TextureRegion(blankTexture));
        skin.add("white", white);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        LabelStyle bigLabelStyle = new LabelStyle();
        bigLabelStyle.font = font;
        bigLabelStyle.fontColor = Color.YELLOW;
        skin.add("bigLabel", bigLabelStyle);

        LabelStyle errorLabelStyle = new LabelStyle();
        errorLabelStyle.font = font;
        errorLabelStyle.fontColor = Color.RED;
        skin.add("errorLabel", errorLabelStyle);

        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = white;
        textButtonStyle.down = white;
        textButtonStyle.over = white;
        textButtonStyle.fontColor = Color.BLACK;
        textButtonStyle.overFontColor = Color.BLUE;
        skin.add("default", textButtonStyle);

        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.background = white;
        textFieldStyle.cursor = white;
        textFieldStyle.selection = white;
        skin.add("default", textFieldStyle);
    }

    public static Texture loadTexture(String path) {
        if (textureCache.containsKey(path)) return textureCache.get(path);
        if (!Gdx.files.internal(path).exists()) {
            log.error("Texture not found: " + path);
            return blankTexture;
        }
        try {
            Texture texture = new Texture(Gdx.files.internal(path));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureCache.put(path, texture);
            texturesLoaded++;
            textureBytesLoaded += texture.getWidth() * texture.getHeight() * 4;
            return texture;
        } catch (Exception e) {
            log.error("Could not load texture: " + path, e);
            return blankTexture;
        }
    }

    public static Texture loadTexture(String name, int width, int height, ByteBuffer buffer) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        buffer.rewind();
        pixmap.getPixels().put(buffer);
        buffer.rewind();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texturesLoaded++;
        textureBytesLoaded += width * height * 4;
        return texture;
    }

    public static Texture wrapGLTexture(int id, int width, int height) {
        return new Texture(width, height, Pixmap.Format.RGBA8888) {
            @Override
            public int getTextureObjectHandle() {
                return id;
            }
        };
    }

    public static Texture releaseTexture(Texture t) {
        if (t != null && t != blankTexture) {
            texturesLoaded--;
            textureBytesLoaded -= t.getWidth() * t.getHeight() * 4;
            t.dispose();
        }
        return null;
    }

    public static void drawTexture(Texture texture, float x, float y, float alpha) {
        if (texture == null) return;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x * globalDrawScale, y * globalDrawScale, texture.getWidth() * globalDrawScale, texture.getHeight() * globalDrawScale);
    }

    public static void drawTexture(Texture texture, float x, float y, BobColor color, int filter) {
        if (texture == null) return;
        batch.setColor(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        batch.draw(texture, x * globalDrawScale, y * globalDrawScale, texture.getWidth() * globalDrawScale, texture.getHeight() * globalDrawScale);
    }

    public static void drawTexture(Texture texture, float x, float y, float width, float height, float alpha) {
        if (texture == null) return;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x * globalDrawScale, y * globalDrawScale, width * globalDrawScale, height * globalDrawScale);
    }

    public static void drawTexture(Texture texture, float x0, float x1, float y0, float y1, float alpha, int filter) {
        if (texture == null) return;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x0 * globalDrawScale, y0 * globalDrawScale, (x1 - x0) * globalDrawScale, (y1 - y0) * globalDrawScale);
    }

    public static void drawTexture(Texture texture, float tx0, float tx1, float ty0, float ty1, float x0, float x1, float y0, float y1, float alpha, int filter) {
        if (texture == null) return;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x0 * globalDrawScale, y0 * globalDrawScale, (x1 - x0) * globalDrawScale, (y1 - y0) * globalDrawScale, tx0, ty0, tx1, ty1);
    }

    public static void drawTexture(Texture texture, float tx0, float tx1, float ty0, float ty1, float x0, float x1, float y0, float y1, float r, float g, float b, float a, int filter) {
        if (texture == null) return;
        if (shapeRenderer.isDrawing()) shapeRenderer.end();
        if (!batch.isDrawing()) batch.begin();
        batch.setColor(r, g, b, a);
        batch.draw(texture, x0 * globalDrawScale, y0 * globalDrawScale, (x1 - x0) * globalDrawScale, (y1 - y0) * globalDrawScale, tx0, ty0, tx1, ty1);
    }

    public static void drawTexture(float tx0, float tx1, float ty0, float ty1, float x0, float x1, float y0, float y1, float alpha, int filter) {
        drawTexture(blankTexture, tx0, tx1, ty0, ty1, x0, x1, y0, y1, 1, 1, 1, alpha, filter);
    }

    public static void drawFilledRectXYWH(float x, float y, float w, float h, float r, float g, float b, float a) {
        if (batch.isDrawing()) batch.end();
        if (!shapeRenderer.isDrawing()) {
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }
        shapeRenderer.setColor(r, g, b, a);
        shapeRenderer.rect(x * globalDrawScale, y * globalDrawScale, w * globalDrawScale, h * globalDrawScale);
    }

    public static void drawFilledRect(int ri, int gi, int bi, float x0, float x1, float y0, float y1, float alpha) {
        drawFilledRectXYWH(x0, y0, x1 - x0, y1 - y0, ri / 255f, gi / 255f, bi / 255f, alpha);
    }

    public static void drawBox(float x1, float x2, float y1, float y2, int r, int g, int b) {
        if (batch.isDrawing()) batch.end();
        if (!shapeRenderer.isDrawing() || shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Line) {
            if (shapeRenderer.isDrawing()) shapeRenderer.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }
        shapeRenderer.setColor(r / 255f, g / 255f, b / 255f, 1f);
        shapeRenderer.rect(x1 * globalDrawScale, y1 * globalDrawScale, (x2 - x1) * globalDrawScale, (y2 - y1) * globalDrawScale);
    }

    public static void drawLine(float x1, float y1, float x2, float y2, float r, float g, float b, float a) {
        if (batch.isDrawing()) batch.end();
        if (!shapeRenderer.isDrawing() || shapeRenderer.getCurrentType() != ShapeRenderer.ShapeType.Line) {
            if (shapeRenderer.isDrawing()) shapeRenderer.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }
        shapeRenderer.setColor(r, g, b, a);
        shapeRenderer.line(x1 * globalDrawScale, y1 * globalDrawScale, x2 * globalDrawScale, y2 * globalDrawScale);
    }

    public static void drawLine(float x1, float y1, float x2, float y2, int r, int g, int b) {
        drawLine(x1, y1, x2, y2, r / 255f, g / 255f, b / 255f, 1f);
    }

    public static void drawArrowLine(float x1, float y1, float x2, float y2, int r, int g, int b) {
        drawLine(x1, y1, x2, y2, r, g, b);
    }

    public static void drawOutlinedString(String text, float x, float y, BobColor color) {
        if (font == null) return;
        Color gdxColor = new Color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f);
        font.setColor(Color.BLACK);
        font.draw(batch, text, (x - 1) * globalDrawScale, (y - 1) * globalDrawScale);
        font.draw(batch, text, (x + 1) * globalDrawScale, (y - 1) * globalDrawScale);
        font.draw(batch, text, (x - 1) * globalDrawScale, (y + 1) * globalDrawScale);
        font.draw(batch, text, (x + 1) * globalDrawScale, (y + 1) * globalDrawScale);
        font.setColor(gdxColor);
        font.draw(batch, text, x * globalDrawScale, y * globalDrawScale);
    }

    public static float getStringWidth(String text) {
        if (font == null) return 0;
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(font, text);
        return layout.width;
    }

    public static void bind(Texture texture) {
        if (texture != null) texture.bind();
    }

    public static byte[] getTextureData(Texture texture) {
        if (texture == null) return null;
        if (!texture.getTextureData().isPrepared()) texture.getTextureData().prepare();
        Pixmap pixmap = texture.getTextureData().consumePixmap();
        ByteBuffer buffer = pixmap.getPixels();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }

    public static void drawTexture(Texture texture, float x, float y, float alpha, int filter) {
        if (texture == null) return;
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, x * globalDrawScale, y * globalDrawScale, texture.getWidth() * globalDrawScale, texture.getHeight() * globalDrawScale);
    }

    public static void dispose() {
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        for (Texture t : textureCache.values()) if (t != null) t.dispose();
        textureCache.clear();
    }
}
