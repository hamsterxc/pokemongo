package com.lonebytesoft.hamster.pokemongo.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class LinearSystemSolver {

    public static Solution solve(final Collection<LinearEquation> equations) {
        final Collection<LinearEquation> system = new ArrayList<>(equations);
        Integer countVariables = null;
        for(final Iterator<LinearEquation> systemIterator = system.iterator(); systemIterator.hasNext(); ) {
            final LinearEquation equation = systemIterator.next();
            final int countCoeffs = equation.getCoeffs().size();
            if(countVariables == null) {
                countVariables = countCoeffs;
            } else {
                if(countCoeffs != countVariables) {
                    throw new IllegalArgumentException("Inconsistent number of coefficients");
                }
            }

            boolean allZero = true;
            for(final double coeff : equation.getCoeffs()) {
                allZero = isEqual(coeff, 0.0);
                if(!allZero) {
                    break;
                }
            }
            if(allZero) {
                if(isEqual(equation.getFree(), 0.0)) {
                    systemIterator.remove();
                } else {
                    return new Solution(SolutionType.INCONSISTENT);
                }
            }
        }

        if((countVariables == null) || (system.size() == 0) || (countVariables > system.size())) {
            return new Solution(SolutionType.INFINITE);
        }

        final List<Double> variables;
        try {
            variables = solveInternal(system);
        } catch (InconsistentSystemException e) {
            return new Solution(SolutionType.INCONSISTENT);
        } catch (InfiniteSolutionsException e) {
            return new Solution(SolutionType.INFINITE);
        }
        return new Solution(variables);
    }

    private static List<Double> solveInternal(final Collection<LinearEquation> equations)
            throws InconsistentSystemException, InfiniteSolutionsException {
        final int countVariables = equations.iterator().next().getCoeffs().size();
        if(countVariables == 1) {
            Double solution = null;
            boolean isInfinite = false;
            for(final LinearEquation equation : equations) {
                final double coeff = equation.getCoeffs().get(0);
                if(isEqual(coeff, 0.0)) {
                    if(isEqual(equation.getFree(), 0.0)) {
                        isInfinite = true;
                        continue;
                    } else {
                        throw new InconsistentSystemException();
                    }
                }

                final double candidate = equation.getFree() / coeff;
                if(solution == null) {
                    solution = candidate;
                } else {
                    if(!isEqual(candidate, solution)) {
                        throw new InconsistentSystemException();
                    }
                }
            }

            if(isInfinite) {
                throw new InfiniteSolutionsException();
            }

            final List<Double> variables = new ArrayList<>();
            variables.add(solution);
            return variables;
        } else {
            LinearEquation reference = null;
            for(final Iterator<LinearEquation> equationsIterator = equations.iterator(); equationsIterator.hasNext(); ) {
                final LinearEquation equation = equationsIterator.next();
                if(!isEqual(equation.getCoeffs().get(countVariables - 1), 0.0)) {
                    reference = equation;
                    equationsIterator.remove();
                    break;
                }
            }
            if(reference == null) {
                throw new InfiniteSolutionsException();
            }

            final Collection<LinearEquation> equationsNew = new ArrayList<>();
            for(final LinearEquation equation : equations) {
                equationsNew.add(eliminateLast(equation, reference));
            }

            final List<Double> solutionsPartial = solveInternal(equationsNew);

            double value = reference.getFree();
            for(int i = 0; i < countVariables - 1; i++) {
                value -= solutionsPartial.get(i) * reference.getCoeffs().get(i);
            }
            solutionsPartial.add(value / reference.getCoeffs().get(countVariables - 1));

            return solutionsPartial;
        }
    }

    private static LinearEquation eliminateLast(final LinearEquation subject, final LinearEquation reference) {
        final List<Double> coeffsSubject = subject.getCoeffs();
        final List<Double> coeffsReference = reference.getCoeffs();

        final int count = coeffsReference.size();
        if(coeffsSubject.size() != count) {
            throw new IllegalArgumentException("Inconsistent number of coefficients");
        }

        final double multiplier = coeffsSubject.get(count - 1) / coeffsReference.get(count - 1);
        final double free = subject.getFree() - reference.getFree() * multiplier;
        final List<Double> coeffs = new ArrayList<>();
        for(int i = 0; i < count - 1; i++) {
            coeffs.add(coeffsSubject.get(i) - coeffsReference.get(i) * multiplier);
        }

        return new LinearEquation(coeffs, free);
    }
    
    private static boolean isEqual(final double first, final double second) {
        return Math.abs(first - second) <= 2.0 * Double.MIN_VALUE;
    }

}
