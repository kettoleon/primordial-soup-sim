package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import java.util.function.Function;

public class NNMath {
    public static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public static double sigmoidDerivative(double x) {
        return x * (1 - x);
    }

    public static double[][] matrixAdd(double[][] a, double[][] b) {
        if (a.length == 0 || b.length == 0 || a.length != b.length || a[0].length != b[0].length) {
            throw new IllegalArgumentException("Cannot add unequal matrices");
        }

        double result[][] = new double[a.length][a[0].length];

        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a[i].length; ++j) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }

        return result;
    }

    public static double[][] matrixMultiply(double[][] a, double[][] b) {
        if (a.length == 0 || b.length == 0 || a[0].length != b.length) {
            throw new IllegalArgumentException("Cannot multiply non n x m and m x p matrices");
        }

        int n = a.length;
        int m = a[0].length;
        int p = b[0].length;

        double[][] result = new double[n][p];

        for (int nIter = 0; nIter < n; ++nIter) {
            for (int pIter = 0; pIter < p; ++pIter) {

                double sum = 0;
                for (int mIter = 0; mIter < m; ++mIter) {
                    sum += (a[nIter][mIter] * b[mIter][pIter]);
                }

                result[nIter][pIter] = sum;
            }
        }

        return result;
    }

    public static double[][] matrixApply(double[][] matrix, Function<Double, Double> fn) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Invalid matrix");
        }

        double[][] result = new double[matrix.length][matrix[0].length];

        for (int i = 0; i < matrix.length; ++i) {
            for (int j = 0; j < matrix[i].length; ++j) {
                result[i][j] = fn.apply(matrix[i][j]);
            }
        }

        return result;
    }

}
