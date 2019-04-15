package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic;

import static java.util.Arrays.copyOf;

public class Instruction {

    private String dest;

    private String[] operands;

    private Operation operation;

    public Instruction(String dest, Operation operation, String... operands) {

        this.dest = dest;
        this.operation = operation;
        this.operands = operands;
    }

    public String toString() {
        return dest + " = " + operation.toString(operands) + ";";
    }

    public Instruction copy() {
        return new Instruction(dest, operation, copyOf(operands, operands.length));
    }

    public String getDest() {
        return dest;
    }

    public String[] getOperands() {
        return operands;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setOperands(String[] operands) {
        this.operands = operands;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
