package com.bobsgame.shared;

public class Easing {
    public static double linearTween(double t, double b, double c, double d) { return c * t / d + b; }
    public static double easeInQuadratic(double t, double b, double c, double d) { return c * (t /= d) * t + b; }
    public static double easeOutQuadratic(double t, double b, double c, double d) { return -c * (t /= d) * (t - 2) + b; }
    public static double easeInOutQuadratic(double t, double b, double c, double d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return -c / 2 * ((--t) * (t - 2) - 1) + b;
    }
    public static double easeInCubic(double t, double b, double c, double d) { return c * (t /= d) * t * t + b; }
    public static double easeOutCubic(double t, double b, double c, double d) { return c * ((t = t / d - 1) * t * t + 1) + b; }
    public static double easeInOutCubic(double t, double b, double c, double d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    }
    public static double easeInQuartic(double t, double b, double c, double d) { return c * (t /= d) * t * t * t + b; }
    public static double easeOutQuartic(double t, double b, double c, double d) { return -c * ((t = t / d - 1) * t * t * t - 1) + b; }
    public static double easeInOutQuartic(double t, double b, double c, double d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return -c / 2 * ((t -= 2) * t * t * t - 2) + b;
    }
    public static double easeInQuintic(double t, double b, double c, double d) { return c * (t /= d) * t * t * t * t + b; }
    public static double easeOutQuintic(double t, double b, double c, double d) { return c * ((t = t / d - 1) * t * t * t * t + 1) + b; }
    public static double easeInOutQuintic(double t, double b, double c, double d) {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    }
    public static double easeInSinusoidal(double t, double b, double c, double d) { return -c * Math.cos(t / d * (Math.PI / 2)) + c + b; }
    public static double easeOutSinusoidal(double t, double b, double c, double d) { return c * Math.sin(t / d * (Math.PI / 2)) + b; }
    public static double easeInOutSinusoidal(double t, double b, double c, double d) { return -c / 2 * (Math.cos(Math.PI * t / d) - 1) + b; }
    public static double easeInExponential(double t, double b, double c, double d) { return (t == 0) ? b : c * Math.pow(2, 10 * (t / d - 1)) + b; }
    public static double easeOutExponential(double t, double b, double c, double d) { return (t == d) ? b + c : c * (-Math.pow(2, -10 * t / d) + 1) + b; }
    public static double easeInOutExponential(double t, double b, double c, double d) {
        if (t == 0) return b;
        if (t == d) return b + c;
        if ((t /= d / 2) < 1) return c / 2 * Math.pow(2, 10 * (t - 1)) + b;
        return c / 2 * (-Math.pow(2, -10 * --t) + 2) + b;
    }
    public static double easeInCircular(double t, double b, double c, double d) { return -c * (Math.sqrt(1 - (t /= d) * t) - 1) + b; }
    public static double easeOutCircular(double t, double b, double c, double d) { return c * Math.sqrt(1 - (t = t / d - 1) * t) + b; }
    public static double easeInOutCircular(double t, double b, double c, double d) {
        if ((t /= d / 2) < 1) return -c / 2 * (Math.sqrt(1 - t * t) - 1) + b;
        return c / 2 * (Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    }

    public static double easeOutParabolicBounce(double t, double b, double c, double d) {
        if ((t /= d) < (1 / 2.75)) return c * (7.5625 * t * t) + b;
        else if (t < (2 / 2.75)) return c * (7.5625 * (t -= (1.5 / 2.75)) * t + .75) + b;
        else if (t < (2.5 / 2.75)) return c * (7.5625 * (t -= (2.25 / 2.75)) * t + .9375) + b;
        else return c * (7.5625 * (t -= (2.625 / 2.75)) * t + .984375) + b;
    }

    public static double easeInBackSlingshot(double t, double b, double c, double d) {
        double s = 1.70158;
        return c * (t /= d) * t * ((s + 1) * t - s) + b;
    }
}
