package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1.AlgorithmicBrainBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.model.genetics.GenomeBasedBuilder;

public class SimpleGeneticProgrammingCreatureBuilder implements GenomeBasedBuilder<Creature> {


    @Override
    public Creature build(Genome genome) {
        Creature creature = new Creature();
        creature.setBrain(new AlgorithmicBrainBuilder(creature).build(genome.getChromosomes().get(0).getNewReader()));
        creature.setGenome(genome);
        return creature;
    }
}
