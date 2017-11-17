package com.lonebytesoft.hamster.pokemongo.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinearEquation {

    private final List<Double> coeffs;
    private final double free;

    public LinearEquation(final List<Double> coeffs, final double free) {
        this.coeffs = Collections.unmodifiableList(new ArrayList<>(coeffs));
        this.free = free;
    }

    public List<Double> getCoeffs() {
        return coeffs;
    }

    public double getFree() {
        return free;
    }

    @Override
    public String toString() {
        return "LinearEquation{" +
                "coeffs=" + coeffs +
                ", free=" + free +
                '}';
    }

}
