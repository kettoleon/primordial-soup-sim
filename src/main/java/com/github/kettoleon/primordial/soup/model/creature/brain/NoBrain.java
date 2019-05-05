package com.github.kettoleon.primordial.soup.model.creature.brain;

public class NoBrain implements Brain {
    @Override
    public void process(float[][] inputs, float[] outputs) {

    }

    @Override
    public float getEnergyConsumption() {
        return Float.MAX_VALUE;
    }

    @Override
    public String getDescription() {
        return "NoBrain";
    }
}
