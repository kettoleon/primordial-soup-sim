package com.github.kettoleon.primordial.soup.util;

import java.util.Arrays;

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

    public static float[] flatten(float[][] inputs) {

        float[] flatten = new float[Arrays.stream(inputs).mapToInt(i -> i.length).sum()];
        int p = 0;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputs[i].length; j++) {
                flatten[p] = inputs[i][j];
                p++;
            }
        }

        return flatten;
    }
}
