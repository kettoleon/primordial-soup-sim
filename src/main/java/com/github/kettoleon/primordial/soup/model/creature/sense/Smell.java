package com.github.kettoleon.primordial.soup.model.creature.sense;

import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.creature.Creature;
import com.github.kettoleon.primordial.soup.model.creature.PlantParticle;

import java.util.List;
import java.util.Optional;

public class Smell implements Sense {

    private static final int SMELL_DISTANCE = 50;

    private float[] smell = new float[]{0, 0};

    private Creature creature;

    private Position[] smellTriangle;

    public Smell(Creature creature) {

        this.creature = creature;
    }

    @Override
    public int getInputsLength() {
        return smell.length;
    }

    @Override
    public float[] getInputs() {
        return smell;
    }


    public void update(List<PlantParticle> closePlants) {
        smellTriangle = null;
        smell[0] = getClosestLeftPlant(closePlants).map(PlantParticle::getPosition).map(p -> p.distanceTo(creature.getPosition())).orElse((double) SMELL_DISTANCE).floatValue();
        smell[1] = getClosestRightPlant(closePlants).map(PlantParticle::getPosition).map(p -> p.distanceTo(creature.getPosition())).orElse((double) SMELL_DISTANCE).floatValue();
    }

    private Optional<PlantParticle> getClosestLeftPlant(List<PlantParticle> closePlants) {
        return closePlants.stream().filter(this::smellLeft).sorted(this::distance).findFirst();
    }

    private Optional<PlantParticle> getClosestRightPlant(List<PlantParticle> closePlants) {
        return closePlants.stream().filter(this::smellRight).sorted(this::distance).findFirst();
    }

    private int distance(PlantParticle plantParticle, PlantParticle plantParticle1) {
        return (int) (plantParticle.getPosition().distanceTo(creature.getPosition()) - plantParticle1.getPosition().distanceTo(creature.getPosition()));
    }

    private Position positionAtDistanceAndRad(float smellDistance, float degrees) {

        float rotation = creature.getRotation() + degrees;

        float x = (float) (creature.getPosition().getX() + Math.cos(rotation) * smellDistance);
        float y = (float) (creature.getPosition().getY() + Math.sin(rotation) * smellDistance);

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

    private boolean smellRight(PlantParticle plantParticle) {

        return isPointInTriangle(plantParticle.getPosition(), creature.getPosition(), getSmellTriangle()[1], getSmellTriangle()[2]);
    }

    private boolean smellLeft(PlantParticle plantParticle) {
        return isPointInTriangle(plantParticle.getPosition(), creature.getPosition(), getSmellTriangle()[1], getSmellTriangle()[0]);
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
}
