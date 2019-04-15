package com.github.kettoleon.lifesim.soup.model.creature;

import com.github.kettoleon.lifesim.soup.model.Position;
import com.github.kettoleon.lifesim.soup.model.World;
import com.github.kettoleon.lifesim.soup.model.WorldObject;
import com.github.kettoleon.lifesim.soup.model.creature.brain.Brain;
import com.github.kettoleon.lifesim.soup.model.genetics.Dna;
import com.github.kettoleon.lifesim.soup.system.PlantParticle;

import java.util.List;

import static com.github.kettoleon.lifesim.soup.util.MathUtils.norm;

public class Creature extends WorldObject {

    private static final int I_HUNGER = 0;
    private static final int I_TOUCH_FOOD = 1;
    private static final int I_TOUCH_DEAD = 2;
    private static final int I_TOUCH_OTHER = 3;
    private static final int I_SMELL_INTENSITY_LEFT = 4;
    private static final int I_SMELL_INTENSITY_RIGHT = 5;
    public static final int O_SPEED = 0;
    public static final int O_ROTATION = 1;
    public static final int SMELL_DISTANCE = 20;

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

    //TODO Senses to read from world -> later stage

    //TODO Metabolism will have senses (Metabolism sends signal to brain) and will receive items from world

    //TODO brain will send signal to actuators to move

    public void tickLogic(long id, World world) {
        if (dead) return;

        inputs[I_TOUCH_FOOD] = 0.0f;
        inputs[I_TOUCH_DEAD] = 0.0f;
        inputs[I_TOUCH_OTHER] = 0.0f;

        List<PlantParticle> closePlants = world.getPlantsAround(this, 50);

        inputs[I_SMELL_INTENSITY_LEFT] = norm((int) closePlants.stream().filter(this::smellLeft).count(), 10);
        inputs[I_SMELL_INTENSITY_RIGHT] = norm((int) closePlants.stream().filter(this::smellRight).count(), 10);

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
        inputs[I_HUNGER] += 0.0000001f * brain.getEnergyConsumption(); //big brains cost to run
        inputs[I_HUNGER] += 0.001f * Math.abs(outputs[O_SPEED]); //moving also costs energy
        inputs[I_HUNGER] += 0.0000001f * Math.abs(outputs[O_ROTATION]); //moving also costs energy

        moveInDirection(outputs[O_SPEED], outputs[O_ROTATION]);
        smellTriangle = null;

        if (id % 10 == 0) { //Dying if hunger reaches 1 //TODO change to energy reserves?
            if(startingPos == null){
                startingPos = getPosition();
            }
            if(inputs[I_HUNGER] > 1.0 || invalid(inputs, outputs) || hasNotMovedAtAll(id)){

                dead = true;
                deadAtTick = id;
            }
        }

    }

    private boolean hasNotMovedAtAll(long id) {
        return startingPos != null && id - firstTick > 1000 && startingPos.equals(getPosition());
    }

    public int getEaten() {
        return eaten;
    }

    public float getFitness() {
        return eaten;
//        return (float) eaten / (float) getSurvivedTicks();
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

    public void setDna(Dna dna) {
        this.dna = dna;
    }

    public Dna getDna() {
        return dna;
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
}
