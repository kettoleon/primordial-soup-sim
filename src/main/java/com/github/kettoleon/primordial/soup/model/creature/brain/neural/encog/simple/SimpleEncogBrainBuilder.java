package com.github.kettoleon.primordial.soup.model.creature.brain.neural.encog.simple;

import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.model.creature.brain.NoBrain;
import com.github.kettoleon.primordial.soup.model.genetics.ChromosomeBasedBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.GeneReader;
import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.github.kettoleon.primordial.soup.util.MathUtils.clamp;

public class SimpleEncogBrainBuilder implements ChromosomeBasedBuilder<Brain> {

    private Creature creature;

    public SimpleEncogBrainBuilder(Creature creature) {

        this.creature = creature;
    }

    @Override
    public Brain build(GeneReader reader) {
        if (reader.hasMoreGenes()) {

            try {
                //TODO here it would be nice to have a chromosome for the network structure
                //TODO and another chromosome for the training data

                BasicNetwork network = createNetworkFromSeed(reader.nextLong());

                List<double[]> inputs = new ArrayList<>();
                List<double[]> outputs = new ArrayList<>();

                while (reader.remainingGenes() > creature.getInputsSize() + creature.getOutputsSize()) {
                    double[] rowInputs = new double[creature.getInputsSize()];
                    for (int i = 0; i < creature.getInputsSize(); i++) {
                        rowInputs[i] = reader.nextFloat();
                    }
                    double[] rowOutputs = new double[creature.getOutputsSize()];
                    for (int i = 0; i < creature.getOutputsSize(); i++) {
                        rowOutputs[i] = reader.nextFloat();
                    }
                    inputs.add(rowInputs);
                    outputs.add(rowOutputs);
                }

                System.out.println("Training brain with " + inputs.size() + " (" + outputs.size() + ") data rows");
                // create training data
                MLDataSet trainingSet = new BasicMLDataSet(inputs.toArray(new double[][]{}), outputs.toArray(new double[][]{}));

                // train the neural network
                //TODO different types of propagation?
                final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

                int epoch = 1;

                do {
                    train.iteration();
//                System.out.println("Epoch #" + epoch + " Error:" + train.getError());
                    epoch++;
                } while (train.getError() > 0.01 && epoch < 10000);
                System.out.println("It took " + epoch + " iterations and we got an error of: " + train.getError());

                train.finishTraining();
                return new SimpleEncogBrain(network);
            } catch (Throwable e) {
                System.err.println("Brain generation failed"); //TODO We need to figure out what is going on with the indexOutOfBounds exception
                return new NoBrain();
            }
        }
        return new NoBrain();
    }

    private BasicNetwork createNetworkFromSeed(long seed) {
        Random random = new Random(seed);

        BasicNetwork network = new BasicNetwork();
        for (int i = 0; i < clamp(random.nextInt(4), 2, 16); i++) {
            network.addLayer(new BasicLayer(pickActivationFunction(random.nextDouble()), random.nextBoolean(), clamp(random.nextInt(4), 1, 16)));
        }
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    private static ActivationFunction[] afs = {
            null,
            null,
            new ActivationSigmoid(),
            new ActivationTANH(),
            new ActivationSoftMax(),
            new ActivationReLU(),
            new ActivationCompetitive(),
            new ActivationSteepenedSigmoid(),
            new ActivationRamp(),
            new ActivationElliott(),
            new ActivationLOG(),
            new ActivationBiPolar(),
            new ActivationStep(),
            new ActivationClippedLinear(),
            new ActivationElliottSymmetric(),
            new ActivationBipolarSteepenedSigmoid(),
            new ActivationSIN(),
            new ActivationLinear(),
            new ActivationGaussian(),
            null,
            null
    };

    private ActivationFunction pickActivationFunction(double nextDouble) {
        return afs[clamp((int) (nextDouble * afs.length), 0, afs.length)];
    }
}
