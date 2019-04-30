package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import java.util.Arrays;
import java.util.function.Function;

public class NeuronLayer {

    public final Function<Double, Double> activationFunction, activationFunctionDerivative;

    double[][] weights;

    public NeuronLayer(double[][] weights) {
        this.weights = weights;
        activationFunction = NNMath::sigmoid;
        activationFunctionDerivative = NNMath::sigmoidDerivative;
    }

    public NeuronLayer(int numberOfNeurons, int numberOfInputsPerNeuron) {
        weights = new double[numberOfInputsPerNeuron][numberOfNeurons];

        for (int i = 0; i < numberOfInputsPerNeuron; ++i) {
            for (int j = 0; j < numberOfNeurons; ++j) {
                weights[i][j] = (2 * Math.random()) - 1; // shift the range from 0-1 to -1 to 1
            }
        }

        activationFunction = NNMath::sigmoid;
        activationFunctionDerivative = NNMath::sigmoidDerivative;
    }

    public void adjustWeights(double[][] adjustment) {
        this.weights = NNMath.matrixAdd(weights, adjustment);
    }

    public NeuronLayer mutate(double chance) {
        return new NeuronLayer(mutate(weights, chance));
    }

    private double[][] mutate(double[][] weights, double chance) {
        double[][] copy = new double[weights.length][];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = mutatew(weights[i], chance);
        }
        return copy;
    }

    private double[] mutatew(double[] weight, double chance) {
        double[] doubles = Arrays.copyOf(weight, weight.length);
        for (int i = 0; i < doubles.length; i++) {
            if (Math.random() < chance) {
                doubles[i] = Math.random();
            }
        }
        return doubles;
    }

    @Override
    public String toString() {
        return MatrixUtil.matrixToString(weights);
    }
}
