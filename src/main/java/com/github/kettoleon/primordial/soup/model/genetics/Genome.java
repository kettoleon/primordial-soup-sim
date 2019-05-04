package com.github.kettoleon.primordial.soup.model.genetics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Genome implements Serializable {

    private List<Chromosome> chromosomes = new ArrayList<>();

    public Genome(int... chromosomeLengths) {
        for (int chromosomeLength : chromosomeLengths) {
            chromosomes.add(new Chromosome(chromosomeLength));
        }
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public void setChromosomes(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
    }
}
