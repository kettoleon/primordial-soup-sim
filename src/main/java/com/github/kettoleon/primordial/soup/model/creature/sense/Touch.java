package com.github.kettoleon.primordial.soup.model.creature.sense;

public class Touch implements Sense {

    private float[] touch = new float[]{0};

    @Override
    public int getInputsLength() {
        return touch.length;
    }

    @Override
    public float[] getInputs() {
        return touch;
    }

    public void touchFood() {
        touch[0] = 1;
    }

    public void reset() {
        touch[0] = 0;
    }
}
