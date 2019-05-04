package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.World;
import com.github.kettoleon.primordial.soup.model.WorldObject;
import com.github.kettoleon.primordial.soup.model.creature.brain.Brain;
import com.github.kettoleon.primordial.soup.model.genetics.Dna;
import com.github.kettoleon.primordial.soup.model.genetics.Genome;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Creature extends WorldObject {

    private static final int I_HUNGER = 0;
    private static final int I_TOUCH_FOOD = 1;
    private static final int I_TOUCH_DEAD = 2;
    private static final int I_TOUCH_OTHER = 3;
    private static final int I_SMELL_INTENSITY_LEFT = 4;
    private static final int I_SMELL_INTENSITY_RIGHT = 5;
    private static final int O_SPEED = 0;
    private static final int O_ROTATION = 1;
    private static final int SMELL_DISTANCE = 50;
    private static final float MAX_SPEED = 10;

    private Brain brain;

    private final float[] inputs = new float[6];
    private final float[] outputs = new float[2];
    private boolean dead = false;
    private long deadAtTick;
    private Brain build;
    private Dna dna;
    private long firstTick;
    private float rotation;
    private Position[] smellTriangle;
    private int eaten;
    private Position startingPos;
    private LinkedList<Position> trail = new LinkedList<>();
    private float totalDistanceTravelled;
    private Genome genome;

    //TODO Senses to read from world -> later stage

    //TODO Metabolism will have senses (Metabolism sends signal to brain) and will receive items from world

    //TODO brain will send signal to actuators to move

    public void tickLogic(long id, World world) {
        if (dead) return;

        inputs[I_TOUCH_FOOD] = 0.0f;
        inputs[I_TOUCH_DEAD] = 0.0f;
        inputs[I_TOUCH_OTHER] = 0.0f;

        List<PlantParticle> closePlants = world.getPlantsAround(this, 50);

        inputs[I_SMELL_INTENSITY_LEFT] = getClosestLeftPlant(closePlants).map(PlantParticle::getPosition).map(p -> p.distanceTo(this.getPosition())).orElse((double) SMELL_DISTANCE).floatValue();
        inputs[I_SMELL_INTENSITY_RIGHT] = getClosestRightPlant(closePlants).map(PlantParticle::getPosition).map(p -> p.distanceTo(this.getPosition())).orElse((double) SMELL_DISTANCE).floatValue();

        closePlants.stream().filter(this::notEatenAndInReachDistance).findFirst().ifPresent(wo -> {
            wo.beEaten();
            eaten++;
            inputs[I_TOUCH_FOOD] = 1.0f;
            inputs[I_HUNGER] = 0.0f;
        });

        world.getCreatures().stream().filter(this::inReachDistance).forEach(wo -> {
            if (wo.isDead()) {
                inputs[I_TOUCH_DEAD] = 1.0f;
            } else {
                inputs[I_TOUCH_OTHER] = 1.0f;
            }
        });

        try {
            brain.process(inputs, outputs);
        } catch (Throwable e) {
            //TODO died of brain damage due to mutation XD
            dead = true;
            deadAtTick = id;
        }
//        float distance = outputs[O_SPEED] * MAX_SPEED;
//        float rotation = (float) (outputs[O_ROTATION] * 2 * Math.PI);

        float distance = outputs[O_SPEED];
        float rotation = outputs[O_ROTATION];

//        inputs[I_HUNGER] += 0.00001f * brain.getEnergyConsumption(); //big brains cost to run
        inputs[I_HUNGER] += 0.001f;
        inputs[I_HUNGER] += 0.001f * Math.abs(distance); //moving also costs energy
        inputs[I_HUNGER] += 0.00001f * Math.abs(rotation); //moving also costs energy


        totalDistanceTravelled += distance;
        moveInDirection(distance, rotation);
        smellTriangle = null;
        if (trail.size() > 50) {
            trail.removeLast();
        }
        trail.addFirst(getPosition().copy());

        if (id % 10 == 0) { //Dying if hunger reaches 1 //TODO change to energy reserves?
            if (startingPos == null) {
                startingPos = getPosition().copy();
            }
            if (inputs[I_HUNGER] > 1.0 || invalid(inputs, outputs) || hasNotMovedAtAll(id)) {

                dead = true;
                deadAtTick = id;
            }
        }

    }

    private Optional<PlantParticle> getClosestLeftPlant(List<PlantParticle> closePlants) {
        return closePlants.stream().filter(this::smellLeft).sorted(this::distance).findFirst();
    }

    private Optional<PlantParticle> getClosestRightPlant(List<PlantParticle> closePlants) {
        return closePlants.stream().filter(this::smellRight).sorted(this::distance).findFirst();
    }

    private int distance(PlantParticle plantParticle, PlantParticle plantParticle1) {
        return (int) (plantParticle.getPosition().distanceTo(this.getPosition()) - plantParticle1.getPosition().distanceTo(this.getPosition()));
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

    private boolean smellRight(PlantParticle plantParticle) {

        return isPointInTriangle(plantParticle.getPosition(), getPosition(), getSmellTriangle()[1], getSmellTriangle()[2]);
    }

    private Position positionAtDistanceAndRad(float smellDistance, float degrees) {

        float rotation = this.rotation + degrees;

        float x = (float) (getPosition().getX() + Math.cos(rotation) * smellDistance);
        float y = (float) (getPosition().getY() + Math.sin(rotation) * smellDistance);

        return new Position(x, y);
    }

    public Position[] getSmellTriangle() {
        if (smellTriangle == null) {
            smellTriangle = new Position[]{
                    positionAtDistanceAndRad(SMELL_DISTANCE, (float) (Math.PI / 2.0f)),
                    positionAtDistanceAndRad(SMELL_DISTANCE, 0),
                    positionAtDistanceAndRad(SMELL_DISTANCE, (float) (3 * Math.PI / 2.0f))
            };
        }
        return smellTriangle;
    }

    private boolean smellLeft(PlantParticle plantParticle) {
        return isPointInTriangle(plantParticle.getPosition(), getPosition(), getSmellTriangle()[1], getSmellTriangle()[0]);
    }

    float sign(Position p1, Position p2, Position p3) {
        return (p1.getX() - p3.getX()) * (p2.getY() - p3.getY()) - (p2.getX() - p3.getX()) * (p1.getY() - p3.getY());
    }

    boolean isPointInTriangle(Position pt, Position v1, Position v2, Position v3) {
        float d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(pt, v1, v2);
        d2 = sign(pt, v2, v3);
        d3 = sign(pt, v3, v1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    public float getRotation() {
        return rotation;
    }

    private boolean invalid(float[] inputs, float[] outputs) {
        for (float f : outputs) {
            if (isNaN(f)) {
                return true;
            }
        }
        for (float f : inputs) {
            if (isNaN(f)) {
                return true;
            }
        }
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

    private boolean atSmellDistance(WorldObject wo) {
        return getPosition().distanceTo(wo.getPosition()) < SMELL_DISTANCE;
    }

    private boolean inReachDistance(WorldObject wo) {
        return getPosition().distanceTo(wo.getPosition()) < 2;
    }

    private PlantParticle toPlantParticle(WorldObject wo) {
        return (PlantParticle) wo;
    }

    private boolean isAPlantParticle(WorldObject wo) {
        return wo instanceof PlantParticle;
    }

    public boolean isDead() {
        return dead;
    }

    public float hunger() {
        return inputs[0];
    }

    public long deadAtTick() {
        return deadAtTick;
    }

    public Brain getBrain() {
        return brain;
    }

    public void setBrain(Brain brain) {

        this.brain = brain;
    }

    public int getInputsSize() {
        return inputs.length;
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
}
