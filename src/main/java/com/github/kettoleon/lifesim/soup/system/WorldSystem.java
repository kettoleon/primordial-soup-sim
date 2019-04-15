package com.github.kettoleon.lifesim.soup.system;

import com.github.kettoleon.lifesim.soup.SimulationSystem;
import com.github.kettoleon.lifesim.soup.model.World;

public class WorldSystem implements SimulationSystem {

    //This thing tries to improve performance by sorting into grid at the end or start of a tick

    @Override
    public void init(World world) {

    }

    @Override
    public void tick(long id, World world) {
        world.recomputeGrid();
    }
}
