package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.World;
import com.github.kettoleon.primordial.soup.model.WorldObject;
import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.model.creature.sense.MetabolicSystem;
import com.github.kettoleon.primordial.soup.model.creature.sense.Sense;
import com.github.kettoleon.primordial.soup.model.creature.sense.Smell;
import com.github.kettoleon.primordial.soup.model.creature.sense.Touch;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Creature extends WorldObject {

    private static final int O_SPEED = 0;
    private static final int O_ROTATION = 1;

    private static final float DEFAULT_BRAIN_ENERGY_CONSUMPTION = 0.001f;
    private static final float MOVEMENT_ENERGY_MULTIPLIER = 0.001f;
    private static final float ROTATION_ENERGY_MULTIPLIER = 0.00001f;
    private final MetabolicSystem metabolicSystem;
    private final Touch touch;
    private final Smell smell;
    private final List<Sense> senses;

    private Brain brain;

    private final float[] outputs = new float[2];
    private boolean dead = false;
    private long deadAtTick;
    private long firstTick;
    private float rotation;

    private int eaten;
    private Position startingPos;
    private LinkedList<Position> trail = new LinkedList<>();
    private float totalDistanceTravelled;
    private Genome genome;

    //TODO Senses to read from world -> later stage

    //TODO Metabolism will have senses (Metabolism sends signal to brain) and will receive items from world

    //TODO brain will send signal to actuators to move

    public Creature() {

        metabolicSystem = new MetabolicSystem();
        touch = new Touch();
        smell = new Smell(this);
        senses = Arrays.asList(metabolicSystem, touch, smell);
    }

    public void tickLogic(long id, World world) {
        if (dead) return;

        touch.reset();

        List<PlantParticle> closePlants = world.getPlantsAround(this, 50);

        smell.update(closePlants);

        closePlants.stream().filter(this::notEatenAndInReachDistance).findFirst().ifPresent(wo -> {
            wo.beEaten();
            eaten++;
            touch.touchFood();
            metabolicSystem.eatFood();
        });

//        world.getCreatures().stream().filter(this::inReachDistance).forEach(wo -> {
//            if (wo.isDead()) {
//                inputs[I_TOUCH_DEAD] = 1.0f;
//            } else {
//                inputs[I_TOUCH_OTHER] = 1.0f;
//            }
//        });

        try {
            brain.process(collectSensorialInputs(), outputs);
        } catch (Throwable e) {
            //TODO died of brain damage due to mutation XD
            dead = true;
            deadAtTick = id;
        }
//        float distance = outputs[O_SPEED] * MAX_SPEED;
//        float rotation = (float) (outputs[O_ROTATION] * 2 * Math.PI);

        float distance = outputs[O_SPEED];
        float rotation = outputs[O_ROTATION];

        //TODO brain.getEnergyConsumption()...
        float energyConsumption = DEFAULT_BRAIN_ENERGY_CONSUMPTION + MOVEMENT_ENERGY_MULTIPLIER * Math.abs(distance) + ROTATION_ENERGY_MULTIPLIER * Math.abs(rotation);
        metabolicSystem.consumeEnergy(energyConsumption);

        totalDistanceTravelled += distance;
        moveInDirection(distance, rotation);
        if (trail.size() > 50) {
            trail.removeLast();
        }
        trail.addFirst(getPosition().copy());

        if (id % 10 == 0) { //Dying if hunger reaches 1 //TODO change to energy reserves?
            if (startingPos == null) {
                startingPos = getPosition().copy();
            }
            if (metabolicSystem.isStarving() || invalid(outputs) || hasNotMovedAtAll(id)) {

                dead = true;
                deadAtTick = id;
            }
        }

    }

    private float[][] collectSensorialInputs() {
        return senses.stream().map(Sense::getInputs).collect(Collectors.toList()).toArray(new float[][]{});
    }

    public LinkedList<Position> getTrail() {
        return trail;
    }

    private boolean hasNotMovedAtAll(long id) {
        return startingPos != null && id - firstTick > 500 && startingPos.distanceTo(getPosition()) < 0.1;
    }

    public int getEaten() {
        return eaten;
    }

    public float getFitness() {
//        return getSurvivedTicks();


//        float v = eaten * getSurvivedTicks() - totalDistanceTravelled;
//
//        if (isNaN(v) || eaten == 0 || totalDistanceTravelled < 1) {
//            return 0;
//        }
//        return v;

//        return (float) eaten / (float) getSurvivedTicks();
//        return totalDistanceTravelled;


        float v = eaten / totalDistanceTravelled;
        if (isNaN(v) || eaten == 0 || totalDistanceTravelled < 1) {
            return 0;
        }
        return eaten * v;
    }


    public float getRotation() {
        return rotation;
    }

    private boolean invalid(float[] outputs) {
        for (float f : outputs) {
            if (isNaN(f)) {
                return true;
            }
        }
//        for (float f : inputs) {
//            if (isNaN(f)) {
//                return true;
//            }
//        }
        return false;
    }

    boolean isNaN(float x) {
        return x != x;
    }

    private void moveInDirection(float distance, float direction) {
        rotation += direction;
        getPosition().translate(distance * Math.cos(rotation), distance * Math.sin(rotation));
    }

    private boolean notEatenAndInReachDistance(PlantParticle wo) {
        return !wo.isEaten() && inReachDistance(wo);
    }

    private boolean inReachDistance(WorldObject wo) {
        return getPosition().distanceTo(wo.getPosition()) < 2;
    }

    public boolean isDead() {
        return dead;
    }

    public Brain getBrain() {
        return brain;
    }

    public void setBrain(Brain brain) {

        this.brain = brain;
    }

    public int getInputsSize() {
        return senses.stream().mapToInt(Sense::getInputsLength).sum();
    }

    public int getOutputsSize() {
        return outputs.length;
    }

    public void setFirstTick(long firstTick) {
        this.firstTick = firstTick;
    }

    public long getSurvivedTicks() {
        return deadAtTick - firstTick;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Genome getGenome() {
        return genome;
    }

    public Position[] getSmellTriangle() {
        return smell.getSmellTriangle();
    }
}
