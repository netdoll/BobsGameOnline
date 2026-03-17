package com.bobsgame.shared;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.FloatBuffer;
import java.util.Locale;

public class BobColor extends java.awt.Color {
    public String name = "";
    public int r, g, b, a;

    public BobColor(int r, int g, int b, int a) {
        super(r, g, b, a);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public BobColor(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public BobColor(float r, float g, float b, float a) {
        this((int)(r * 255), (int)(g * 255), (int)(b * 255), (int)(a * 255));
    }

    public BobColor(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public BobColor(BobColor color) {
        this(color.ri(), color.gi(), color.bi(), color.ai());
    }

    public BobColor(BobColor color, float a) {
        this(color.ri(), color.gi(), color.bi(), (int)(a * 255));
    }

    public BobColor(java.awt.Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public BobColor(int rgb) {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, 255);
    }

    public BobColor(int argb, boolean hasAlpha) {
        this((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, hasAlpha ? (argb >> 24) & 0xFF : 255);
    }

    public float rf() { return getRed() / 255.0f; }
    public float gf() { return getGreen() / 255.0f; }
    public float bf() { return getBlue() / 255.0f; }
    public float af() { return getAlpha() / 255.0f; }

    public float r() { return rf(); }
    public float g() { return gf(); }
    public float b() { return bf(); }
    public float a() { return af(); }

    public int ri() { return getRed(); }
    public int gi() { return getGreen(); }
    public int bi() { return getBlue(); }
    public int ai() { return getAlpha(); }

    static public BobColor clear = new BobColor(0, 0, 0, 0);
    static public BobColor black = new BobColor(0, 0, 0);
    static public BobColor white = new BobColor(255, 255, 255);
    static public BobColor gray = new BobColor(128, 128, 128);
    static public BobColor darkGray = new BobColor(64, 64, 64);
    static public BobColor lightGray = new BobColor(192, 192, 192);
    static public BobColor red = new BobColor(255, 0, 0);
    static public BobColor green = new BobColor(0, 255, 0);
    static public BobColor blue = new BobColor(0, 0, 255);
    static public BobColor yellow = new BobColor(255, 255, 0);
    static public BobColor magenta = new BobColor(255, 0, 255);
    static public BobColor cyan = new BobColor(0, 255, 255);
    static public BobColor orange = new BobColor(255, 165, 0);
    static public BobColor pink = new BobColor(255, 192, 203);
    static public BobColor purple = new BobColor(128, 0, 128);

    public static final BobColor CLEAR = clear;
    public static final BobColor BLACK = black;
    public static final BobColor WHITE = white;
    public static final BobColor GRAY = gray;
    public static final BobColor DARKGRAY = darkGray;
    public static final BobColor LIGHTGRAY = lightGray;
    public static final BobColor RED = red;
    public static final BobColor GREEN = green;
    public static final BobColor BLUE = blue;
    public static final BobColor YELLOW = yellow;
    public static final BobColor MAGENTA = magenta;
    public static final BobColor CYAN = cyan;
    public static final BobColor ORANGE = orange;
    public static final BobColor PINK = pink;
    public static final BobColor PURPLE = purple;

    static public BobColor darkRed = new BobColor(128, 0, 0);
    static public BobColor lightRed = new BobColor(255, 128, 128);
    static public BobColor darkGreen = new BobColor(0, 128, 0);
    static public BobColor lightGreen = new BobColor(128, 255, 128);
    static public BobColor darkBlue = new BobColor(0, 0, 128);
    static public BobColor lightBlue = new BobColor(128, 128, 255);
    static public BobColor darkYellow = new BobColor(128, 128, 0);
    static public BobColor lightYellow = new BobColor(255, 255, 128);
    static public BobColor darkOrange = new BobColor(200, 100, 0);
    static public BobColor lightOrange = new BobColor(255, 200, 128);
    static public BobColor darkPurple = new BobColor(64, 0, 128);
    static public BobColor lightPurple = new BobColor(192, 128, 255);
    static public BobColor darkPink = new BobColor(128, 0, 128);
    static public BobColor lightPink = new BobColor(255, 128, 255);

    public static final BobColor DARKRED = darkRed;
    public static final BobColor LIGHTRED = lightRed;
    public static final BobColor DARKGREEN = darkGreen;
    public static final BobColor LIGHTGREEN = lightGreen;
    public static final BobColor DARKBLUE = darkBlue;
    public static final BobColor LIGHTBLUE = lightBlue;
    public static final BobColor DARKYELLOW = darkYellow;
    public static final BobColor LIGHTYELLOW = lightYellow;
    public static final BobColor DARKORANGE = darkOrange;
    public static final BobColor LIGHTORANGE = lightOrange;
    public static final BobColor DARKPURPLE = darkPurple;
    public static final BobColor LIGHTPURPLE = lightPurple;
    public static final BobColor DARKPINK = darkPink;
    public static final BobColor LIGHTPINK = lightPink;

    static public BobColor darkerGray = new BobColor(32, 32, 32);
    static public BobColor lighterGray = new BobColor(224, 224, 224);
    static public BobColor darkerGreen = new BobColor(0, 64, 0);
    static public BobColor darkerRed = new BobColor(64, 0, 0);
    static public BobColor darkerBlue = new BobColor(0, 0, 64);
    static public BobColor darkerPurple = new BobColor(32, 0, 64);
    static public BobColor darkerOrange = new BobColor(100, 50, 0);
    static public BobColor darkerYellow = new BobColor(64, 64, 0);
    static public BobColor darkerPink = new BobColor(64, 0, 64);

    public static final BobColor DARKERGRAY = darkerGray;
    public static final BobColor LIGHTERGRAY = lighterGray;
    public static final BobColor DARKERGREEN = darkerGreen;
    public static final BobColor DARKERRED = darkerRed;
    public static final BobColor DARKERBLUE = darkerBlue;
    public static final BobColor DARKERPURPLE = darkerPurple;
    public static final BobColor DARKERORANGE = darkerOrange;
    public static final BobColor DARKERYELLOW = darkerYellow;
    public static final BobColor DARKERPINK = darkerPink;

    public BobColor lighter() {
        float[] hsb = java.awt.Color.RGBtoHSB(ri(), gi(), bi(), null);
        int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], Math.min(1.0f, hsb[2] * 1.1f));
        BobColor c = new BobColor(rgb);
        return c;
    }

    public BobColor darker() {
        float[] hsb = java.awt.Color.RGBtoHSB(ri(), gi(), bi(), null);
        int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * 0.9f);
        BobColor c = new BobColor(rgb);
        return c;
    }

    public BobColor lighter(float ratio) {
        float[] hsb = java.awt.Color.RGBtoHSB(ri(), gi(), bi(), null);
        int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], Math.min(1.0f, hsb[2] * (1.0f + ratio)));
        BobColor c = new BobColor(rgb);
        return c;
    }

    public BobColor darker(float ratio) {
        float[] hsb = java.awt.Color.RGBtoHSB(ri(), gi(), bi(), null);
        int rgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2] * (1.0f - ratio));
        BobColor c = new BobColor(rgb);
        return c;
    }

    public BobColor clone() {
        return new BobColor(this);
    }
}
