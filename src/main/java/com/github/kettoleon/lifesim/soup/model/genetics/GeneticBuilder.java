package com.github.kettoleon.lifesim.soup.model.genetics;

public interface GeneticBuilder<P> {

    P build(DnaReader dna);

}
