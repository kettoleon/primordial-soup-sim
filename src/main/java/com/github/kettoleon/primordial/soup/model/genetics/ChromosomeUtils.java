package com.github.kettoleon.primordial.soup.model.genetics;

import java.util.function.Supplier;

import static java.util.Arrays.copyOf;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class ChromosomeUtils {

    public static double[] mutateWithoutGrowth(final double[] original, double chance) { //TODO in which amount should it mutate? (For now, change the gene entirely)

        double[] mutated = copyOf(original, original.length);

        for (int i = 0; i < mutated.length; i++) {
            if (nextDouble(0, 1) < chance) {
                mutated[i] = nextDouble(0, 1);
            }
        }
        return mutated;
    }

    public static double[] mutate(final double[] original, double chance) { //TODO in which amount should it mutate? (For now, change the gene entirely)

        double[] mutated = copyOf(original, original.length);

        for (int i = 0; i < mutated.length; i++) {
            if (nextDouble(0, 1) < chance) {
                mutated[i] = nextDouble(0, 1);
            }
        }

        //Mutation to grow or decrease number of genes
        while (nextDouble(0, 1) < chance) {
            if (nextBoolean()) {
                mutated = copyOf(mutated, mutated.length + 1);
                mutated[mutated.length - 1] = nextDouble(0, 1);
            } else {
                mutated = copyOf(mutated, mutated.length - 1);
            }
        }
        return mutated;
    }

    public static double[] breed(Supplier<Integer> parentProbability, double[]... parents) {

        int maxGenes = getMaxGenes(parents);

        double[] offspring = new double[maxGenes];
        for (int i = 0; i < maxGenes; i++) {

            double[] pickedParent = parents[parentProbability.get()];
            if (i >= pickedParent.length) {
                offspring = copyOf(offspring, i);
                break;
            }
            offspring[i] = pickedParent[i];
        }

        return offspring;
    }

    private static int getMaxGenes(double[][] parents) {
        int max = 0;
        for (double[] parent : parents) {
            max = Math.max(max, parent.length);
        }
        return max;
    }

}
