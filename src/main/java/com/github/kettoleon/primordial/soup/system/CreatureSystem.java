package com.github.kettoleon.primordial.soup.system;

import com.github.kettoleon.primordial.soup.SimulationSystem;
import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.World;
import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.CreatureBuilderFactory.BrainType;
import com.github.kettoleon.primordial.soup.model.genetics.Chromosome;
import com.github.kettoleon.primordial.soup.model.genetics.ChromosomeUtils;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;
import com.github.kettoleon.primordial.soup.model.genetics.GenomeBasedBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.kettoleon.primordial.soup.model.creature.CreatureBuilderFactory.aCreatureBuilderFor;
import static com.github.kettoleon.primordial.soup.model.creature.CreatureBuilderFactory.aGenomeFor;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class CreatureSystem implements SimulationSystem {

    private static final BrainType BRAIN_TYPE = BrainType.SIMPLE_GP;
    private static final int INITIAL_POPULATION = 100;
    private static final int INITIAL_GENES = 128;
    private static final float RADIATION = 0.1f;

    private List<Creature> creatures = new ArrayList<>();
    private List<Creature> fittestGenePool = new ArrayList<>();

    private boolean allDead;
    private int generation = 0;
    private long generationTickStart = 0;
    private GenomeBasedBuilder<Creature> creatureBuilder = aCreatureBuilderFor(BRAIN_TYPE);
    private File simFile = new File("prev.sd");

    @Override
    public void init(World world) {

        loadSimulation();
        if (generation == 0) {
            for (int i = 0; i < INITIAL_POPULATION; i++) {
                Genome dna = aGenomeFor(BRAIN_TYPE);
                addNewCreature(0, world, dna);
            }
        } else {
            System.out.println("Resuming simulation from last run!");
            repopulate(0, world);
            generation++;
        }
    }

    public static Position randomStartingPosition() {
        return new Position(-400 + nextInt(0, 800), -400 + nextInt(0, 800));
    }

    @Override
    public void tick(long id, World world) {

        allDead = true;
        creatures.forEach(c -> {
            c.tickLogic(id, world);
            if (!c.isDead()) {
                allDead = false;
            }
        });

        if (allDead) {
            List<Creature> deadGenerationFittest = getFittest(creatures);
            Creature winner = deadGenerationFittest.get(0);
            System.out.printf("Generation %d extinct\n", generation);
            System.out.println("Best individual's ticks: " + winner.getSurvivedTicks());
            System.out.println("Best individual's eaten food: " + winner.getEaten());
            System.out.println("Best individual's (fitness): " + winner.getFitness());
            System.out.println("Best individual's brain: ");
            System.out.println(winner.getBrain().getDescription());

            System.out.println("Repopulating with best individuals from gene pool...");
            world.getCreatures().clear();
            creatures.clear();

            updateGenePool(deadGenerationFittest);
            repopulate(id, world);

            generation++;
            generationTickStart = id;
        }

    }

    private List<Creature> getFittest(List<Creature> creatures) {
        return creatures.stream().sorted(comparingDouble(Creature::getFitness).reversed()).collect(toList()).subList(0, 20);
    }

    private void updateGenePool(List<Creature> deadGenerationFittest) {
        List<Creature> newGenePool = new ArrayList<>();
        newGenePool.addAll(fittestGenePool);
        newGenePool.addAll(deadGenerationFittest);
        fittestGenePool = getFittest(newGenePool);

        saveSimulation();
    }

    public static class SimulationData implements Serializable {
        private int generation;
        private List<Genome> genePool;

        public SimulationData(int generation, List<Genome> genePool) {

            this.generation = generation;
            this.genePool = genePool;
        }

        public int getGeneration() {
            return generation;
        }

        public void setGeneration(int generation) {
            this.generation = generation;
        }

        public List<Genome> getGenePool() {
            return genePool;
        }

        public void setGenePool(List<Genome> genePool) {
            this.genePool = genePool;
        }
    }

    private void loadSimulation() {
        if (simFile.exists()) {
            try {

                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(simFile));
                SimulationData simData = (SimulationData) ois.readObject();

                generation = simData.generation;
                simData.genePool.stream().map(creatureBuilder::build).forEach(fittestGenePool::add);

                ois.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void saveSimulation() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(simFile));
            SimulationData simData = new SimulationData(generation, collectFittestGenes());
            oos.writeObject(simData);
            oos.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private List<Genome> collectFittestGenes() {
        return fittestGenePool.stream().map(Creature::getGenome).collect(toList());
    }

    private void repopulate(long id, World world) {

        //TODO use proper breeding interchanging chromosomes for complex brain types
        Supplier<Integer> fgpp = cuadraticProbability(fittestGenePool.size());
        Supplier<Integer> bp = cuadraticProbability(2);
        for (int i = 0; i < INITIAL_POPULATION; i++) {

//            float[][] parents = new float[2][];
//            parents[0] = fittestGenePool.get(fgpp.get()).getDna().getGenes();
//            parents[1] = fittestGenePool.get(fgpp.get()).getDna().getGenes();
//            float[] offspring = ChromosomeUtils.mutateWithoutGrowth(ChromosomeUtils.breed(bp, parents), RADIATION);
            double[] offspring = ChromosomeUtils.mutateWithoutGrowth(fittestGenePool.get(fgpp.get()).getGenome().getChromosomes().get(0).getGenes(), RADIATION);

            Genome genome = new Genome();
            genome.getChromosomes().add(new Chromosome(offspring));
            addNewCreature(id, world, genome);

        }
        for (Creature c : fittestGenePool) {
            addNewCreature(id, world, c.getGenome());
            addNewCreature(id, world, aGenomeFor(BRAIN_TYPE));
        }
    }

    private Supplier<Integer> cuadraticProbability(int items) {
        return new Supplier<Integer>() {

            List<Integer> probArray = cuadraticProbabilityArray(items);

            @Override
            public Integer get() {
                return probArray.get(nextInt(0, probArray.size()));
            }
        };
    }

    private List<Integer> cuadraticProbabilityArray(int items) {
        List<Integer> itemIdx = new ArrayList<>();
        int power = 1;
        for (int i = items - 1; i >= 0; i--) {
            for (int j = 0; j < power; j++) {
                itemIdx.add(i);
            }
            power *= 2;
        }
        return itemIdx;
    }


    private void addNewCreature(long id, World world, Genome genome) {
        Creature creature = creatureBuilder.build(genome);
        creature.setGenome(genome);
        creature.place(randomStartingPosition());
        creature.setFirstTick(id);
        creatures.add(creature);
        world.addCreature(creature);
    }
}
