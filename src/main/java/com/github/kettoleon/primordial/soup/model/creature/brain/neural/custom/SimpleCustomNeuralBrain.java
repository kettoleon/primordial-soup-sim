package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SimpleCustomNeuralBrain implements Brain {


    //TODO we will start simple, but add more functionality later on:
    // - Make the weights or activation functions change during the live of the creature to add plasticity
    // - Make the NN "compositable", i.e, one NN that decides which other NN to use next.
    //     This way we will not have one single big NN doing a lot of unnecessary processing.
    // - Improve the algorithm to make it more efficient

    private List<NeuronLayer> neuronLayers = new ArrayList<>();

    public SimpleCustomNeuralBrain(List<NeuronLayer> neuronLayers) {

        this.neuronLayers = neuronLayers;
    }

    @Override
    public void process(float[] inputs, float[] outputs) {


        //Multiply inputs by the weights
        //Do activation/normalisation function

        double[][] nextInputs = new double[][]{toDoubleArray(inputs)};
        for (NeuronLayer current : neuronLayers) {

            nextInputs = NNMath.matrixApply(NNMath.matrixMultiply(nextInputs, current.weights), current.activationFunction);
        }

        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = (float) nextInputs[0][i];
        }

    }


    private double[] toDoubleArray(float[] inputs) {
        double[] doubles = new double[inputs.length];
        for (int i = 0; i < doubles.length; i++)
            doubles[i] = inputs[i];
        return doubles;
    }

    @Override
    public float getEnergyConsumption() {
        return neuronLayers.size() * neuronLayers.size();
    }

    @Override
    public String getDescription() {
        return String.format("%dx%d Neural network", neuronLayers.size(), neuronLayers.size());
    }

    public SimpleCustomNeuralBrain mutate(double chance) {
        List<NeuronLayer> layers = new ArrayList<>();
        for (NeuronLayer nl : this.neuronLayers) {
            layers.add(nl.mutate(chance));
        }
        return new SimpleCustomNeuralBrain(layers);
    }

    public static class NeuronLayer {

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
    }
}
