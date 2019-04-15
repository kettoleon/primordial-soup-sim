package com.github.kettoleon.lifesim.soup;

import com.github.kettoleon.lifesim.soup.model.World;

public interface SimulationSystem {

    void init(World world);

    void tick(long id, World world);

}
