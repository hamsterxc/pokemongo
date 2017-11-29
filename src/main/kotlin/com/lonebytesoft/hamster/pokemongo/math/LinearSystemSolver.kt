package com.lonebytesoft.hamster.pokemongo.math

fun solve(equations: Collection<LinearEquation>): Solution {
    if(equations.isEmpty()) {
        return Solution(emptyList())
    }

    val countVariables = equations.countVariables()
    equations
            .filter { it.coeffs.size != countVariables }
            .forEach { throw IllegalArgumentException("Inconsistent number of coefficients") }

    val system = try {
        equations.filter { !it.isZero() }
    } catch(e: InconsistentSystemException) {
        return Solution(SolutionType.INCONSISTENT)
    }

    return when {
        system.isEmpty() -> Solution(emptyList())
        countVariables > system.size -> Solution(SolutionType.INFINITE)
        else -> try {
            Solution(solveInternal(system))
        } catch(e: InconsistentSystemException) {
            Solution(SolutionType.INCONSISTENT)
        } catch(e: InfiniteSolutionsException) {
            Solution(SolutionType.INFINITE)
        }
    }
}

private fun solveInternal(equations: Collection<LinearEquation>): List<Double> {
    val countVariables = equations.countVariables() // >= 1
    if(countVariables == 1) {
        val solutions = equations
                .filter { !it.isZero() }
                .map { it.free / it.coeffs[0] }
        if(solutions.isEmpty()) {
            throw InfiniteSolutionsException()
        } else {
            val solution = solutions[0]
            if(solutions.all { it.isEqual(solution) }) {
                return listOf(solution)
            } else {
                throw InconsistentSystemException()
            }
        }
    } else {
        val equation = equations.first { !it.coeffs.last().isZero() }
        val coeffLast = equation.coeffs.last()

        val equationsNew = equations.map { (it + equation * (-it.coeffs.last() / coeffLast)).dropLast() }
        val solutions = solveInternal(equationsNew)
        val solutionLast = (equation.free - solutions.mapIndexed { index, solution -> solution * equation.coeffs[index] }.sum()) / coeffLast

        return solutions + listOf(solutionLast)
    }
}

private fun Double.isEqual(other: Double) = Math.abs(this - other) <= 2.0 * Double.MIN_VALUE

private fun Double.isZero() = isEqual(0.0)

private fun Collection<LinearEquation>.countVariables() = first().coeffs.size

private fun LinearEquation.isZero() =
        if(coeffs.all { it.isZero() }) {
            if(free.isZero()) {
                true
            } else {
                throw InconsistentSystemException()
            }
        } else {
            false
        }

private fun LinearEquation.dropLast() = LinearEquation(coeffs.dropLast(1), free)
