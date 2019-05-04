package com.github.kettoleon.primordial.soup.model.genetics;

public interface ChromosomeBasedBuilder<P> {

    P build(GeneReader reader);

}
