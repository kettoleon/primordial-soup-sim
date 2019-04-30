package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;


import java.util.List;

import static com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom.MatrixUtil.apply;
import static com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom.NNMath.*;

/**
 * https://medium.com/technology-invention-and-more/how-to-build-a-multi-layered-neural-network-in-python-53ec3d1d326a#.9kcfharq6
 * http://stevenmiller888.github.io/mind-how-to-build-a-neural-network-part-2/
 */
public class NeuralNet {

    private final List<NeuronLayer> layers;
    private final double[][][] layerOutputs;
    private double[][] outputLayer1;
    private double[][] outputLayer2;
    private final double learningRate;

//    public NeuralNet(NeuronLayer layer1, NeuronLayer layer2) {
//        this(layer1, layer2, 0.1);
//    }
//
//    public NeuralNet(NeuronLayer layer1, NeuronLayer layer2, double learningRate) {
//        this.layer1 = layer1;
//        this.layer2 = layer2;
//        this.learningRate = learningRate;
//    }


    public NeuralNet(List<NeuronLayer> layers, double learningRate) {
        this.layers = layers;
        this.learningRate = learningRate;
        this.layerOutputs = new double[layers.size()][][];
    }

    /**
     * Forward propagation
     * <p>
     * Output of neuron = 1 / (1 + e^(-(sum(weight, input)))
     *
     * @param inputs
     */
    public double[][] think(double[][] inputs) {

        double[][] nextInputs = inputs;
        for (int i = 0; i < layers.size(); i++) {
            NeuronLayer current = layers.get(i);

            nextInputs = apply(matrixMultiply(nextInputs, current.weights), current.activationFunction);
            layerOutputs[i] = nextInputs;
        }

        return nextInputs;
    }

    public void train(double[][] inputs, double[][] outputs, int numberOfTrainingIterations) {
        for (int i = 0; i < numberOfTrainingIterations; ++i) {
            // pass the training set through the network
            thinkAndTrain(inputs, outputs);

            // adjust weights by error * input * output * (1 - output)

            // calculate the error for layer 2
            // (the difference between the desired output and predicted output for each of the training inputs)
//            double[][] errorLayer2 = matrixSubtract(outputs, outputLayer2); // 4x1
//            double[][] deltaLayer2 = scalarMultiply(errorLayer2, apply(outputLayer2, layer2.activationFunctionDerivative)); // 4x1

            // calculate the error for layer 1
            // (by looking at the weights in layer 1, we can determine by how much layer 1 contributed to the error in layer 2)

//            double[][] errorLayer1 = matrixMultiply(deltaLayer2, matrixTranspose(layer2.weights)); // 4x4
//            double[][] deltaLayer1 = scalarMultiply(errorLayer1, apply(outputLayer1, layer1.activationFunctionDerivative)); // 4x4

            // Calculate how much to adjust the weights by
            // Since we’re dealing with matrices, we handle the division by multiplying the delta output sum with the inputs' transpose!

//            double[][] adjustmentLayer1 = matrixMultiply(matrixTranspose(inputs), deltaLayer1); // 4x4
//            double[][] adjustmentLayer2 = matrixMultiply(matrixTranspose(outputLayer1), deltaLayer2); // 4x1

//            adjustmentLayer1 = apply(adjustmentLayer1, (x) -> learningRate * x);
//            adjustmentLayer2 = apply(adjustmentLayer2, (x) -> learningRate * x);

            // adjust the weights
//            this.layer1.adjustWeights(adjustmentLayer1);
//            this.layer2.adjustWeights(adjustmentLayer2);

            // if you only had one layer
            // synaptic_weights += dot(training_set_inputs.T, (training_set_outputs - output) * output * (1 - output))
            // double[][] errorLayer1 = NNMath.matrixSubtract(outputs, outputLayer1);
            // double[][] deltaLayer1 = NNMath.matrixMultiply(errorLayer1, MatrixUtil.apply(outputLayer1, NNMath::sigmoidDerivative));
            // double[][] adjustmentLayer1 = NNMath.matrixMultiply(NNMath.matrixTranspose(inputs), deltaLayer1);

            if (i % 10000 == 0) {
                System.out.println(" Training iteration " + i + " of " + numberOfTrainingIterations);
            }
            //System.out.println(this);

        }
    }

    public double[][] thinkAndTrain(double[][] inputs, double[][] outputs) {
        double[][] result = think(inputs); // 4x3


        double[][][] layerDeltas = new double[layers.size()][][];
        for (int d = layerDeltas.length - 1; d >= 0; d--) {
            double[][] layerError = matrixSubtract(outputs, layerOutputs[d]); // 4x1
            layerDeltas[d] = scalarMultiply(layerError, apply(outputLayer2, layers.get(d).activationFunctionDerivative)); // 4x1
        }

        double[][] nextInputs = inputs;
        for(int l = 0; l < layers.size(); l++){

            double[][] layerAdjustment = matrixMultiply(matrixTranspose(nextInputs), layerDeltas[l]);
            layerAdjustment = apply(layerAdjustment, (x) -> learningRate * x);
            layers.get(l).adjustWeights(layerAdjustment);

            nextInputs = layerOutputs[l];

        }
        return result;
    }

    public double[][] getOutput() {
        return outputLayer2;
    }

//    @Override
//    public String toString() {
//        String result = "Layer 1\n";
//        result += layer1.toString();
//        result += "Layer 2\n";
//        result += layer2.toString();
//
//        if (outputLayer1 != null) {
//            result += "Layer 1 output\n";
//            result += MatrixUtil.matrixToString(outputLayer1);
//        }
//
//        if (outputLayer2 != null) {
//            result += "Layer 2 output\n";
//            result += MatrixUtil.matrixToString(outputLayer2);
//        }
//
//        return result;
//    }
}