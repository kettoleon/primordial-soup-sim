package com.github.kettoleon.lifesim.soup.model;

public class WorldObject {

    private Position position;

    public WorldObject() {
    }

    public WorldObject(Position position) {

        this.position = position;
    }

    public void place(Position position) {
        this.position = position;
    }

    public boolean isPlaced() {
        return this.position != null;
    }

    public Position getPosition() {
        return position;
    }
}
