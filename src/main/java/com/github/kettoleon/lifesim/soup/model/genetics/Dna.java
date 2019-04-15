package com.github.kettoleon.lifesim.soup.model.genetics;

import org.apache.commons.lang3.RandomUtils;

public class Dna {

    private float[] genes;

    public Dna(int geneNumber) {
        this(generateRandomGenes(geneNumber));
    }

    public Dna(float[] genes) {
        this.genes = genes;
    }

    public float[] getGenes() {
        return genes;
    }

    public DnaReader getNewReader(){
        return new DnaReader(genes);
    }


    private static float[] generateRandomGenes(int geneNumber) {
        float[] genes = new float[geneNumber];
        for (int i = 0; i < genes.length; i++) {
            genes[i] = RandomUtils.nextFloat(0, 1);
        }
        return genes;
    }

}
