package com.github.kettoleon.lifesim.soup.model.genetics;

import java.util.function.Supplier;

import static java.util.Arrays.copyOf;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextFloat;

public class DnaUtils {

    public static float[] mutate(final float[] original, float chance) { //TODO in which amount should it mutate? (For now, change the gene entirely)

        float[] mutated = copyOf(original, original.length);

        for (int i = 0; i < mutated.length; i++) {
            if (nextFloat(0, 1) < chance) {
                mutated[i] = nextFloat(0, 1);
            }
        }

        //Mutation to grow or decrease number of genes
        while (nextFloat(0, 1) < chance) {
            if (nextBoolean()) {
                mutated = copyOf(mutated, mutated.length + 1);
                mutated[mutated.length - 1] = nextFloat(0, 1);
            } else {
                mutated = copyOf(mutated, mutated.length - 1);
            }
        }
        return mutated;
    }

    public static float[] breed(Supplier<Integer> parentProbability, float[]... parents) {

        int maxGenes = getMaxGenes(parents);

        float[] offspring = new float[maxGenes];
        for (int i = 0; i < maxGenes; i++) {

            float[] pickedParent = parents[parentProbability.get()];
            if (i >= pickedParent.length) {
                offspring = copyOf(offspring, i);
                break;
            }
            offspring[i] = pickedParent[i];
        }

        return offspring;
    }

    private static int getMaxGenes(float[][] parents) {
        int max = 0;
        for (float[] parent : parents) {
            max = Math.max(max, parent.length);
        }
        return max;
    }

}
