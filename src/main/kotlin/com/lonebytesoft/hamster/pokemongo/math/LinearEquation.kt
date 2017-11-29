package com.lonebytesoft.hamster.pokemongo.math

data class LinearEquation(
        val coeffs: List<Double>,
        val free: Double
) {

    internal operator fun times(scalar: Double) = LinearEquation(
            coeffs.map { it * scalar },
            free * scalar
    )

    internal operator fun plus(another: LinearEquation) = LinearEquation(
            coeffs.mapIndexed { index, coeff -> coeff + another.coeffs[index] },
            free + another.free
    )

}
