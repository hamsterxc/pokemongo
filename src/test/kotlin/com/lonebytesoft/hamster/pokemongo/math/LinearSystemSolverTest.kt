package com.lonebytesoft.hamster.pokemongo.math

import org.junit.Assert
import org.junit.Test

class LinearSystemSolverTest {

    @Test
    fun testSimple() {
        assertSolution(
                listOf(1.5, 1.0),
                LinearEquation(listOf(2.0, 3.0), 6.0),
                LinearEquation(listOf(4.0, 9.0), 15.0)
        )

        assertSolution(
                listOf(1.0, -2.0, -2.0),
                LinearEquation(listOf(3.0, 2.0, -1.0), 1.0),
                LinearEquation(listOf(2.0, -2.0, 4.0), -2.0),
                LinearEquation(listOf(-1.0, 0.5, -1.0), 0.0)
        )

        assertSolution(
                listOf(-15.0, 8.0, 2.0),
                LinearEquation(listOf(1.0, 3.0, -2.0), 5.0),
                LinearEquation(listOf(3.0, 5.0, 6.0), 7.0),
                LinearEquation(listOf(2.0, 4.0, 3.0), 8.0)
        )
    }

    @Test
    fun testExcess() {
        assertSolution(
                SolutionType.INFINITE,
                LinearEquation(listOf(3.0, 2.0), 6.0),
                LinearEquation(listOf(6.0, 4.0), 12.0)
        )

        assertSolution(
                listOf(1.0, 1.0),
                LinearEquation(listOf(1.0, -2.0), -1.0),
                LinearEquation(listOf(3.0, 5.0), 8.0),
                LinearEquation(listOf(4.0, 3.0), 7.0)
        )

        assertSolution(
                SolutionType.INFINITE,
                LinearEquation(listOf(1.0, 3.0, -2.0), 5.0),
                LinearEquation(listOf(3.0, 5.0, 6.0), 7.0)
        )
    }

    @Test
    fun testInconsistent() {
        assertSolution(
                SolutionType.INCONSISTENT,
                LinearEquation(listOf(3.0, 2.0), 6.0),
                LinearEquation(listOf(3.0, 2.0), 12.0)
        )

        assertSolution(
                SolutionType.INCONSISTENT,
                LinearEquation(listOf(1.0, 1.0), 1.0),
                LinearEquation(listOf(2.0, 1.0), 1.0),
                LinearEquation(listOf(3.0, 2.0), 3.0)
        )
    }

    private fun assertSolution(expected: List<Double>, vararg equations: LinearEquation) {
        val solution = solve(equations.asList())
        Assert.assertEquals(SolutionType.UNIQUE, solution.type)
        Assert.assertArrayEquals(expected.toDoubleArray(), solution.values.toDoubleArray(), 1e-12)
    }

    private fun assertSolution(expected: SolutionType, vararg equations: LinearEquation) {
        Assert.assertNotEquals(SolutionType.UNIQUE, expected)

        val solution = solve(equations.asList())
        Assert.assertEquals(expected, solution.type)
    }

}
