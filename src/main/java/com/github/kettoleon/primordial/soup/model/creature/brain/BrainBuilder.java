package com.github.kettoleon.primordial.soup.model.creature.brain;

import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.AlgorithmicBrainBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.DnaReader;
import com.github.kettoleon.primordial.soup.model.genetics.GeneticBuilder;

import java.util.Arrays;
import java.util.List;

public class BrainBuilder implements GeneticBuilder<Brain> {

    private Creature creature;

    public BrainBuilder(Creature creature) {

        this.creature = creature;
    }

    @Override
    public Brain build(DnaReader dna) {
        if(dna.hasMoreGenes()) {
            return dna.pickFromList(brainTypeBuilders()).build(dna);
        }
        return new NoBrain();
    }

    private List<GeneticBuilder<Brain>> brainTypeBuilders() {
        return Arrays.asList(
                new AlgorithmicBrainBuilder(creature)
        );
    }

}
