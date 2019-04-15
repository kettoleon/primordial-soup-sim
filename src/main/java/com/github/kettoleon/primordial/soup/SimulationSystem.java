package com.github.kettoleon.primordial.soup;

import com.github.kettoleon.primordial.soup.model.World;

public interface SimulationSystem {

    void init(World world);

    void tick(long id, World world);

}
