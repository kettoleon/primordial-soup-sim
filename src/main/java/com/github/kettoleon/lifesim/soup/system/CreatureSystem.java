package com.github.kettoleon.lifesim.soup.system;

import com.github.kettoleon.lifesim.soup.SimulationSystem;
import com.github.kettoleon.lifesim.soup.model.Position;
import com.github.kettoleon.lifesim.soup.model.World;
import com.github.kettoleon.lifesim.soup.model.creature.Creature;
import com.github.kettoleon.lifesim.soup.model.creature.CreatureBuilder;
import com.github.kettoleon.lifesim.soup.model.genetics.Dna;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.github.kettoleon.lifesim.soup.model.genetics.DnaUtils.breed;
import static com.github.kettoleon.lifesim.soup.model.genetics.DnaUtils.mutate;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class CreatureSystem implements SimulationSystem {

    private static final int INITIAL_POPULATION = 100;
    private static final int INITIAL_GENES = 128;
    private List<Creature> creatures = new ArrayList<>();
    private List<Creature> fittestGenePool = new ArrayList<>();

    private boolean allDead;
    private int generation = 0;
    private long generationTickStart = 0;
    private CreatureBuilder creatureBuilder = new CreatureBuilder();

    @Override
    public void init(World world) {

        loadGenePool();
        if (generation == 0) {
            for (int i = 0; i < INITIAL_POPULATION; i++) {
                Dna dna = new Dna(INITIAL_GENES);
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
            System.out.println("Best individual's food/ticks (fitness): " + winner.getFitness());
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

        saveGenePool();
    }

    private void loadGenePool() {
        try {
            FileInputStream fis = new FileInputStream("prev.genepool");
            DataInputStream dis = new DataInputStream(fis);

            generation = dis.readInt();
            int poolSize = dis.readInt();
            for (int i = 0; i < poolSize; i++) {
                float[] genes = new float[dis.readInt()];
                for (int j = 0; j < genes.length; j++) {
                    genes[j] = dis.readFloat();
                }

                Dna dna = new Dna(genes);

                Creature build = creatureBuilder.build(dna.getNewReader());
                build.setDna(dna);
                fittestGenePool.add(build);

            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void saveGenePool() {
        try {
            FileOutputStream fos = new FileOutputStream("prev.genepool");
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(generation);
            dos.writeInt(fittestGenePool.size());
            for (Creature creature : fittestGenePool) {
                float[] genes = creature.getDna().getGenes();
                dos.writeInt(genes.length);
                for (float f : genes) dos.writeFloat(f);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void repopulate(long id, World world) {

        Supplier<Integer> fgpp = cuadraticProbability(fittestGenePool.size());
        Supplier<Integer> bp = cuadraticProbability(2);
        for (int i = 0; i < INITIAL_POPULATION; i++) {

            float[][] parents = new float[2][];
            parents[0] = fittestGenePool.get(fgpp.get()).getDna().getGenes();
            parents[1] = fittestGenePool.get(fgpp.get()).getDna().getGenes();
            float[] offspring = mutate(breed(bp, parents), 0.1f);

            addNewCreature(id, world, new Dna(offspring));

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


    private void addNewCreature(long id, World world, Dna dna) {
        Creature creature = creatureBuilder.build(dna.getNewReader());
        creature.setDna(dna);
        creature.place(randomStartingPosition());
        creature.setFirstTick(id);
        creatures.add(creature);
        world.addCreature(creature);
    }
}
