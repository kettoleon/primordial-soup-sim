package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.creature.brain.BrainBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.model.genetics.GenomeBasedBuilder;

public class CreatureBuilder implements GenomeBasedBuilder<Creature> {


    @Override
    public Creature build(Genome genome) {
        Creature creature = new Creature();
        creature.setBrain(new BrainBuilder(creature).build(genome.getChromosomes().get(0).getNewReader()));
        creature.setGenome(genome);
        return creature;
    }

}
