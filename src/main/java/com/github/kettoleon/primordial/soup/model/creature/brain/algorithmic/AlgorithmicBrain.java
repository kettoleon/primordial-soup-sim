package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic;

import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;

public class AlgorithmicBrain implements Brain {

    private float[] memory;

    private BrainAlgorithm brainAlgorithm;

    public AlgorithmicBrain(float[] memory, BrainAlgorithm brainAlgorithm) {
        this.memory = memory;
        this.brainAlgorithm = brainAlgorithm;
    }

    @Override
    public void process(float[] inputs, float[] outputs) {

        brainAlgorithm.process(inputs, memory, outputs);

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
