package com.github.kettoleon.primordial.soup.model.creature.brain;

import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic.custom.v1.AlgorithmicBrainBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.ChromosomeBasedBuilder;
import com.github.kettoleon.primordial.soup.model.genetics.GeneReader;

import java.util.Arrays;
import java.util.List;

public class BrainBuilder implements ChromosomeBasedBuilder<Brain> {

    private Creature creature;

    public BrainBuilder(Creature creature) {

        this.creature = creature;
    }

    @Override
    public Brain build(GeneReader reader) {
        if (reader.hasMoreGenes()) {
            return reader.pickFromList(brainTypeBuilders()).build(reader);
        }
        return new NoBrain();
    }

    private List<ChromosomeBasedBuilder<Brain>> brainTypeBuilders() {
        return Arrays.asList(
//                new SimpleEncogBrainBuilder(creature)
                new AlgorithmicBrainBuilder(creature)
//                new NeuralBrainBuilder(creature)
        );
    }

}
