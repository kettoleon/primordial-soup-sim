package com.github.kettoleon.lifesim.soup.model;

import com.github.kettoleon.lifesim.soup.model.creature.Creature;
import com.github.kettoleon.lifesim.soup.system.PlantParticle;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class World {

    private static final int GRID_SIZE = 25;

    private Multimap<String, PlantParticle> plantsGrid = ArrayListMultimap.create();

    private List<Creature> creatures = new ArrayList<>();

    public void addCreature(Creature creature) {
        creatures.add(creature);
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public void addPlantParticle(PlantParticle plantParticle) {
        String gridId = getGridId(plantParticle);
        plantsGrid.put(gridId, plantParticle);
    }

    private String getGridId(PlantParticle plantParticle) {
        Position position = plantParticle.getPosition();
        int x = (int) (position.getX() / GRID_SIZE);
        int y = (int) (position.getY() / GRID_SIZE);
        return x + "," + y;
    }

    public void removeAllPlants(List<PlantParticle> eaten) {
        for (PlantParticle tr : eaten) {
            plantsGrid.remove(getGridId(tr), tr);
        }
    }

    public List<PlantParticle> getPlantsAround(WorldObject wo, float distance) {
        List<String> gridIds = getGridIds(wo, distance);
        List<PlantParticle> plants = new ArrayList<>();
        for (String gridId : gridIds) {
            plants.addAll(plantsGrid.get(gridId));
        }

        return plants;
    }

    private List<String> getGridIds(WorldObject wo, float distance) {
        Position position = wo.getPosition();
        int x = (int) (position.getX() / GRID_SIZE);
        int y = (int) (position.getY() / GRID_SIZE);
        return Arrays.asList(
                (x - 1) + "," + (y - 1), x + "," + (y - 1), (x + 1) + "," + (y - 1),
                (x - 1) + "," + y, x + "," + y, (x + 1) + "," + y,
                (x - 1) + "," + (y + 1), x + "," + (y + 1), (x + 1) + "," + (y + 1)
        );
    }

    public void recomputeGrid() {

    }

    public Collection<PlantParticle> getPlantParticles() {
        return plantsGrid.values();
    }
}
