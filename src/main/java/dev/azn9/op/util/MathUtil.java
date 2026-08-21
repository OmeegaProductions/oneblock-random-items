package dev.azn9.op.util;

public class MathUtil {

    private MathUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static double getCircleRadius(int pointCount, double pointDistance) {
        return pointDistance / (2 * Math.sin(Math.toRadians(360D / pointCount) / 2));
    }

}
