package com.github.kettoleon.primordial.soup.model.creature.brain.algorithmic;

import java.util.function.Function;

import static java.lang.String.join;

public enum Operation {
    SUM(ops -> join(" + ", ops)),
    SUBSTRACT(ops -> join(" - ", ops)),
    MULTIPLY(ops -> join(" * ", ops)),
    DIVIDE(ops -> join(" / ", ops), 2);

    private int max;
    private Function<String[], String> printer;

    Operation(Function<String[], String> printer) {
        this.printer = printer;
    }

    Operation(Function<String[], String> printer, int max) {
        this.printer = printer;
        this.max = max;
    }

    public String toString(String[] operands) {
        return printer.apply(operands);
    }

    public int minOperands() {
        return 2;
    }

    public int maxOperands() {
        return max;
    }
}
