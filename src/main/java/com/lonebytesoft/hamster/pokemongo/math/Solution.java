package com.lonebytesoft.hamster.pokemongo.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {

    private final SolutionType type;
    private final List<Double> values;

    Solution(final List<Double> values) {
        if(values == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        type = SolutionType.UNIQUE;
        this.values = Collections.unmodifiableList(new ArrayList<>(values));
    }

    Solution(final SolutionType type) {
        if((type == null) || (type == SolutionType.UNIQUE)) {
            throw new IllegalArgumentException("Illegal solution type");
        }

        this.type = type;
        this.values = null;
    }

    public SolutionType getType() {
        return type;
    }

    public List<Double> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "Solution{" +
                "type=" + type +
                ", values=" + values +
                '}';
    }

}
