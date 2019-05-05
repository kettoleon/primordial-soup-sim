package com.github.kettoleon.primordial.soup.model.creature.brain.neural.encog.simple;

import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import org.encog.neural.networks.BasicNetwork;

import static com.github.kettoleon.primordial.soup.util.MathUtils.flatten;

public class SimpleEncogBrain implements Brain {


    private BasicNetwork network;

    public SimpleEncogBrain(BasicNetwork network) {

        this.network = network;
    }

    @Override
    public void process(float[][] inputs, float[] outputs) {

        double[] outs = new double[outputs.length];
        network.compute(toDoubleArray(flatten(inputs)), outs);

        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = (float) outs[i];
        }
    }

    private double[] toDoubleArray(float[] inputs) {
        double[] doubles = new double[inputs.length];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = inputs[i];
        }
        return doubles;
    }

    @Override
    public float getEnergyConsumption() {
        return 0;
    }

    @Override
    public String getDescription() {
        return "TODO: Description of a " + SimpleEncogBrain.class.getSimpleName();
    }
}
