package com.github.kettoleon.primordial.soup.util;

public class MathUtils {

    public static int clamp(int val, int minIncl, int maxExcl) {
        if (val < minIncl) {
            return minIncl;
        }
        if (val >= maxExcl && maxExcl > minIncl) {
            return maxExcl - 1;
        }
        return val;
    }

    public static float norm(int value, int max) {
        if (max == 0) return 0;
        return clamp(value / max, 0, max);
    }
}
