package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1;

import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;

import static com.github.kettoleon.primordial.soup.util.MathUtils.flatten;

/**
 * Later on I discovered this is called genetic programming!
 */
public class AlgorithmicBrain implements Brain {

    private float[] memory;

    private BrainAlgorithm brainAlgorithm;

    public AlgorithmicBrain(float[] memory, BrainAlgorithm brainAlgorithm) {
        this.memory = memory;
        this.brainAlgorithm = brainAlgorithm;
    }

    @Override
    public void process(float[][] inputs, float[] outputs) {

        brainAlgorithm.process(flatten(inputs), memory, outputs);

    }

    @Override
    public float getEnergyConsumption() {
        return memory.length + brainAlgorithm.getNumInstructions();
    }

    @Override
    public String getDescription() {
        return "Memory: " + memory.length + "\n" + brainAlgorithm.getAlgorithm();
    }


}
