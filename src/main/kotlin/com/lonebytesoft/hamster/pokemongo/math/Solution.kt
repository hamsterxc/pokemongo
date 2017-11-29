package com.lonebytesoft.hamster.pokemongo.math

class Solution
private constructor(
        val type: SolutionType,
        val values: List<Double>
) {

    internal constructor(values: List<Double>) : this(SolutionType.UNIQUE, values)

    internal constructor(type: SolutionType) : this(type, emptyList()) {
        if(type == SolutionType.UNIQUE) {
            throw IllegalArgumentException("Illegal solution type")
        }
    }

}

enum class SolutionType {
    UNIQUE,
    INFINITE,
    INCONSISTENT,
}
