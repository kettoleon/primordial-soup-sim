package com.github.kettoleon.lifesim.soup.util;

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
        return clamp(value/max,0,max);
    }
}
