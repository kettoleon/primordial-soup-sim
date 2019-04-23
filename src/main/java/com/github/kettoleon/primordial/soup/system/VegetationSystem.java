package com.github.kettoleon.primordial.soup.system;

import com.github.kettoleon.primordial.soup.SimulationSystem;
import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.World;
import com.github.kettoleon.primordial.soup.model.creature.PlantParticle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VegetationSystem implements SimulationSystem {

    private static final int INITIAL_FOOD = 500;

    private List<PlantParticle> plantParticles = new ArrayList<>();

    @Override
    public void init(World world) {

        for (int i = 0; i < INITIAL_FOOD; i++) {
            addNewPlantParticle(CreatureSystem.randomStartingPosition(), world);
        }
    }

    @Override
    public void tick(long id, World world) {

        List<PlantParticle> eaten = plantParticles.stream().filter(PlantParticle::isEaten).collect(Collectors.toList());
        plantParticles.removeAll(eaten);
        world.removeAllPlants(eaten);

        for (int i = 0; i < eaten.size(); i++) {
            addNewPlantParticle(CreatureSystem.randomStartingPosition(), world);
        }


    }

    private void addNewPlantParticle(Position position, World world) {
        PlantParticle plantParticle = new PlantParticle(position);
        plantParticles.add(plantParticle);
        world.addPlantParticle(plantParticle);
    }

}
