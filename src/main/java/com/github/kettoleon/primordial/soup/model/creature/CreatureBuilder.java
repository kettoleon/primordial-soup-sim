package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.creature.brain.BrainBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.DnaReader;
import com.github.kettoleon.primordial.soup.model.genetics.GeneticBuilder;

public class CreatureBuilder implements GeneticBuilder<Creature> {


    @Override
    public Creature build(DnaReader dna) {
        Creature creature = new Creature();
        creature.setBrain(new BrainBuilder(creature).build(dna));

        return creature;
    }

}
