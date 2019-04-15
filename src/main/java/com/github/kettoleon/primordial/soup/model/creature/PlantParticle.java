package com.github.kettoleon.primordial.soup.model.creature;

import com.github.kettoleon.primordial.soup.model.Position;
import com.github.kettoleon.primordial.soup.model.WorldObject;

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
