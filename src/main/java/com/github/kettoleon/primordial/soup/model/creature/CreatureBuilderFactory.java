package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.model.genetics.GenomeBasedBuilder;

import static com.github.kettoleon.primordial.soup.model.creature.CreatureBuilderFactory.BrainType.COMPLEX_GP;

public class CreatureBuilderFactory {

    public static Genome aGenomeFor(BrainType bt) {
        if (bt.equals(COMPLEX_GP)) {
            return new Genome(64, 64, 64, 64);
        }
        return new Genome(128);
    }

    public static GenomeBasedBuilder<Creature> aCreatureBuilderFor(BrainType bt) {
        if (bt.equals(COMPLEX_GP)) {
            return new ComplexGeneticProgrammingCreatureBuilder();
        }
        return new SimpleGeneticProgrammingCreatureBuilder();
    }

    public enum BrainType {
        SIMPLE_GP,
        SIMPLE_NN,
        COMPLEX_MIXED,
        COMPLEX_NN,
        COMPLEX_GP
    }

}
