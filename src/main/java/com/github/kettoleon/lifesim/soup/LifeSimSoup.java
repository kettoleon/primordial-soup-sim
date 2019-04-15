package com.github.kettoleon.lifesim.soup;

import com.github.kettoleon.lifesim.soup.model.World;
import com.github.kettoleon.lifesim.soup.system.CreatureSystem;
import com.github.kettoleon.lifesim.soup.system.VegetationSystem;
import com.github.kettoleon.lifesim.soup.system.render.SimulationRenderingSystem;

import java.util.Arrays;
import java.util.List;

public class LifeSimSoup implements Runnable {

    public static void main(String[] args) {

        new LifeSimSoup().run();
    }

    private World world = new World();
    private List<SimulationSystem> simulationSystems = Arrays.asList(
            new VegetationSystem(),
            new CreatureSystem(),
            new SimulationRenderingSystem()
    );

    public LifeSimSoup() {


    }


    @Override
    public void run() {

        for (SimulationSystem ss : simulationSystems) {
            ss.init(world);
        }

        long tick = 0;
        while (tick < Long.MAX_VALUE) {
            for (SimulationSystem ss : simulationSystems) {
                ss.tick(tick, world);
            }
            tick++;
        }
    }
}
