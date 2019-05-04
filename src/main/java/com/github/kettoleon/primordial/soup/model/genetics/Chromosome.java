package com.github.kettoleon.primordial.soup.model.genetics;

import java.io.Serializable;

import static org.apache.commons.lang3.RandomUtils.nextDouble;

public class Chromosome implements Serializable {

    private double[] genes;

    public Chromosome(int geneNumber) {
        this(generateRandomGenes(geneNumber));
    }

    public Chromosome(double[] genes) {
        this.genes = genes;
    }

    public double[] getGenes() {
        return genes;
    }

    public GeneReader getNewReader() {
        return new GeneReader(genes);
    }

    private static double[] generateRandomGenes(int geneNumber) {
        double[] genes = new double[geneNumber];
        for (int i = 0; i < genes.length; i++) {
            genes[i] = nextDouble(0, 1);
        }
        return genes;
    }

}
