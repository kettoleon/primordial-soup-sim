package com.github.kettoleon.lifesim.soup.system;

import com.github.kettoleon.lifesim.soup.SimulationSystem;
import com.github.kettoleon.lifesim.soup.model.Position;
import com.github.kettoleon.lifesim.soup.model.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VegetationSystem implements SimulationSystem {

    private int initialPopulation = 400;

    private List<PlantParticle> plantParticles = new ArrayList<>();

    @Override
    public void init(World world) {

        for (int i = 0; i < initialPopulation; i++) {
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
