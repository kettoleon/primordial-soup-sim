package com.github.kettoleon.lifesim.soup.system;

import com.github.kettoleon.lifesim.soup.model.Position;
import com.github.kettoleon.lifesim.soup.model.WorldObject;

public class PlantParticle extends WorldObject {

    private boolean eaten;

    public PlantParticle(Position position) {
        super(position);
    }

    public void beEaten(){
        eaten = true;
    }

    public boolean isEaten() {
        return eaten;
    }
}
