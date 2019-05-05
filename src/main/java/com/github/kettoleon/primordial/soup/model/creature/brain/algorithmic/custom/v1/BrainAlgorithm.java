package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1;

public interface BrainAlgorithm {

    void process(float[] i, float[] m, float[] o);

    int getNumInstructions();

    String getAlgorithm();
}
