package com.github.kettoleon.primordial.soup.model.genetics;

import com.github.kettoleon.primordial.soup.util.MathUtils;
import com.google.common.collect.Iterators;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class GeneReader {
    private final double[] genes;

    private int pos;

    public GeneReader(double[] genes) {
        this.genes = genes;
    }

    public int remainingGenes() {
        return genes.length - pos;
    }

    public boolean hasMoreGenes() {
        return remainingGenes() > 0;
    }

    public <T> Optional<T> next(Function<GeneReader, T> f) {
        if (hasMoreGenes()) {
            return Optional.ofNullable(f.apply(this));
        }
        return Optional.empty();
    }

    public double nextDouble() {
        return genes[pos++];
    }

    public float nextFloat() {
        return (float) nextDouble();
    }

    public boolean nextBoolean() {
        return nextDouble() > 0.5;
    }

    public int nextInt(int startInclusive, int endExclusive) {
        return MathUtils.clamp((int) (startInclusive + nextDouble() * (endExclusive - startInclusive)), startInclusive, endExclusive);
    }

    public <T> T pickFromList(List<T> list) {
        return list.get(nextInt(0, list.size()));
    }

    public <T> T pickFromList(T[] values) {
        return values[nextInt(0, values.length)];
    }

    public <T> T pickFromList(Set<T> list) {
        return Iterators.get(list.iterator(), nextInt(0, list.size()));
    }

    public long nextLong() {
        return (long) (nextDouble() * Long.MAX_VALUE);
    }
}
