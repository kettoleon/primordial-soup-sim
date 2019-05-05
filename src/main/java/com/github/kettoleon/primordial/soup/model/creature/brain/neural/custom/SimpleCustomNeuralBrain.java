package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.util.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class SimpleCustomNeuralBrain implements Brain {


    //TODO we will start simple, but add more functionality later on:
    // - Make the weights or activation functions change during the live of the creature to add plasticity
    // - Make the NN "compositable", i.e, one NN that decides which other NN to use next.
    //     This way we will not have one single big NN doing a lot of unnecessary processing.
    // - Improve the algorithm to make it more efficient

    //TODO new thought: The genes can express not only the number of layers and neurons, but also the training results
    // (the expected behaviour table)

    private List<NeuronLayer> neuronLayers = new ArrayList<>();

    public SimpleCustomNeuralBrain(List<NeuronLayer> neuronLayers) {

        this.neuronLayers = neuronLayers;
    }

    @Override
    public void process(float[][] inputs, float[] outputs) {


        //Multiply inputs by the weights
        //Do activation/normalisation function

        double[][] nextInputs = new double[][]{toDoubleArray(MathUtils.flatten(inputs))};
        for (NeuronLayer current : neuronLayers) {

//            nextInputs = NNMath.matrixApply(NNMath.matrixMultiply(nextInputs, current.weights), current.activationFunction);
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

}
