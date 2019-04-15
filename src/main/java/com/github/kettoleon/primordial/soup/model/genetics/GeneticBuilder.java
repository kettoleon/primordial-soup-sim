package com.github.kettoleon.primordial.soup.model.genetics;

public interface GeneticBuilder<P> {

    P build(DnaReader dna);

}
