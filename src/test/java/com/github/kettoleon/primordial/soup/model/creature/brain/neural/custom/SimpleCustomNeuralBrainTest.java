package com.github.kettoleon.primordial.soup.model.creature.brain.neural.custom;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class SimpleCustomNeuralBrainTest {

    @Test
    public void learnsXOR() {

        List<SimpleCustomNeuralBrain> testSubjects = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            testSubjects.add(aBrainForXOR());
        }


//        while(true){
        for(int c = 0; c < 10000; c++) {
            SimpleCustomNeuralBrain best = pickBest(testSubjects);

            testSubjects.clear();
            testSubjects.add(best);
            for (int i = 0; i < 100; i++) {
                testSubjects.add(best.mutate(0.1));
            }
        }


    }

    private SimpleCustomNeuralBrain pickBest(List<SimpleCustomNeuralBrain> testSubjects) {
        double minDte = 1000;
        SimpleCustomNeuralBrain bestbrain = null;
        for (int i = 0; i < 100; i++) {
            double dte = testSubjectForXOR(testSubjects.get(i));
            if (dte < minDte) {
                minDte = dte;
                bestbrain = testSubjects.get(i);
            }
        }
        System.out.println("Best fitness: " + minDte);
        return bestbrain;
    }

    private double testSubjectForXOR(SimpleCustomNeuralBrain brain) {
        double distanceToExpected = 0;
        distanceToExpected += testIO(brain, 0, 0, 0);
        distanceToExpected += testIO(brain, 0, 1, 1);
        distanceToExpected += testIO(brain, 1, 0, 1);
        distanceToExpected += testIO(brain, 1, 1, 0);
        return distanceToExpected;
    }

    private double testIO(SimpleCustomNeuralBrain brain, int a, int b, int expected) {
        float[] outputs = {0};
        brain.process(new float[]{a, b}, outputs);
        return Math.abs(expected - outputs[0]);
    }

    private SimpleCustomNeuralBrain aBrainForXOR() {
        return new SimpleCustomNeuralBrain(asList(
                new NeuronLayer(3, 2),
                new NeuronLayer(1, 3)

        ));
    }

}