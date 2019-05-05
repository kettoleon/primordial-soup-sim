package com.github.kettoleon.primordial.soup.model.creature.sense;

public class MetabolicSystem implements Sense {

    private float[] hunger = new float[]{0};

    @Override
    public int getInputsLength() {
        return hunger.length;
    }

    @Override
    public float[] getInputs() {
        return hunger;
    }

    public void eatFood() {
        hunger[0] = 0;
    }

    public void consumeEnergy(float energyConsumption) {
        hunger[0] = hunger[0] + energyConsumption;
    }

    public boolean isStarving() {
        return hunger[0] > 1.0;
    }
}
