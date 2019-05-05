package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v2.AlgorithmicBrainBuilderV2;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.model.genetics.GenomeBasedBuilder;

public class ComplexGeneticProgrammingCreatureBuilder implements GenomeBasedBuilder<Creature> {

    @Override
    public Creature build(Genome genome) {
        Creature creature = new Creature();
        creature.setBrain(new AlgorithmicBrainBuilderV2(creature).buildBrain());
        creature.setGenome(genome);
        return creature;
    }
}
