package com.github.kettoleon.primordial.soup.model.genetics;

import com.github.kettoleon.primordial.soup.util.MathUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DnaReader {
    private final float[] genes;

    private int pos;

    public DnaReader(float[] genes) {
        this.genes = genes;
    }

    public boolean hasMoreGenes() {
        return pos < genes.length;
    }

    public <T> Optional<T> next(Function<DnaReader, T> f) {
        if (hasMoreGenes()) {
            return Optional.ofNullable(f.apply(this));
        }
        return Optional.empty();
    }

    public float nextFloat() {
        return genes[pos++];
    }

    public boolean nextBoolean() {
        return nextFloat() > 0.5;
    }

    public int nextInt(int startInclusive, int endExclusive) {
        return MathUtils.clamp((int) (startInclusive + nextFloat() * (endExclusive - startInclusive)), startInclusive, endExclusive);
    }


    public <T> T pickFromList(List<T> list) {
        return list.get(nextInt(0, list.size()));
    }

    public <T> T pickFromList(T[] values) {
        return values[nextInt(0, values.length)];
    }
}
