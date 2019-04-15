package com.github.kettoleon.primordial.soup.model.creature.brain;

public interface Brain {

    void process(float[] inputs, float[] outputs);

    float getEnergyConsumption();

    String getDescription();
}
