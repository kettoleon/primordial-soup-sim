package com.github.kettoleon.lifesim.soup.model;

import org.apache.commons.math3.util.MathUtils;

public class Position {

    private float x;

    private float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double distanceTo(Position to) {
        return Math.hypot(x - to.x, y - to.y);
    }

    public void translate(double x, double y) {
        this.x += x;
        this.y += y;
    }
}
