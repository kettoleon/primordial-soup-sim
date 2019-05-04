package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.model.creature.brain.NoBrain;
import com.github.kettoleon.primordial.soup.model.genetics.ChromosomeBasedBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.GeneReader;

import java.util.ArrayList;
import java.util.List;

public class NeuralBrainBuilder implements ChromosomeBasedBuilder<Brain> {

    private static final int MAX_LAYERS = 10;
    private Creature creature;

    public NeuralBrainBuilder(Creature creature) {

        this.creature = creature;
    }

    @Override
    public Brain build(GeneReader reader) {//TODO I'm sure there must be better ways to do that ^.^"
        if (reader.hasMoreGenes()) {
            int brainGenes = reader.remainingGenes();
            int sqrSize = computeSquareSize(brainGenes);


            //layer,inputsPerNeuron,numberOfNeurons
            List<LayerWeightsBuilder> layers = new ArrayList<>();
            List<NeuronLayer> neuronLayers = new ArrayList<>();

            layers.add(new LayerWeightsBuilder(creature.getInputsSize(), sqrSize));
            for (int i = 0; i < sqrSize - 2; i++) {
                layers.add(new LayerWeightsBuilder(sqrSize, sqrSize));
            }
            layers.add(new LayerWeightsBuilder(sqrSize, creature.getOutputsSize()));

            for (LayerWeightsBuilder lwb : layers) {
                neuronLayers.add(new NeuronLayer(readWeights(reader, lwb.inputsSize, lwb.neurons)));
            }

            return new SimpleCustomNeuralBrain(neuronLayers);

            //TODO make the brains "growable" with the dna by populating them in another order
//            int currentLayer = 0;
//            int currentNeuron = 0;
//            int currentInput = 0;
//            while (dna.hasMoreGenes()) {
//                LayerWeightsBuilder lwb = layers.get(currentLayer);
//
//
//            }
//
//            new SimpleCustomNeuralBrain.NeuronLayer(weights);
        }
        return new NoBrain();
    }

    private double[][] readWeights(GeneReader dna, int inputsSize, int neurons) {
        double[][] weights = new double[inputsSize][neurons];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                weights[i][j] = dna.nextFloat();
            }
        }

        return weights;
    }

    private int computeSquareSize(int brainGenes) {
        int squareSize = 0;
        while (true) {

            if (neededGenes(squareSize) > brainGenes) {
                return squareSize-1;
            }

            squareSize++;
        }
    }

    private int neededGenes(int squareSize) {
        return creature.getInputsSize() * squareSize + squareSize * squareSize * (squareSize - 1) + squareSize * creature.getOutputsSize();
    }


    private class LayerWeightsBuilder {
        private final double[][] weights;
        private int neurons;
        private int inputsSize;

        public LayerWeightsBuilder(int inputsSize, int neurons) {
            this.inputsSize = inputsSize;
            this.neurons = neurons;
            this.weights = new double[inputsSize][neurons];
        }

        public void setNeuronInputWeights(int neuron, double[] inputWeights) {

        }

        public void setPreviousNeuronOutputWeights(int neuron, double[] outputWeights) {

        }
    }
}
