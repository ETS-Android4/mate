package org.mate.exploration.genetic.selection;

import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.exploration.genetic.fitness.IFitnessFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Select chromosomes strictly by the first
 * {@link org.mate.exploration.genetic.fitness.IFitnessFunction} given
 * @param <T> Type wrapped by the chromosome implementation
 */
public class FitnessSelectionFunction<T> implements ISelectionFunction<T> {

    @Override
    public List<IChromosome<T>> select(List<IChromosome<T>> population, final List<IFitnessFunction<T>> fitnessFunctions) {
        List<IChromosome<T>> list = new ArrayList<>(population);
        Collections.sort(list, new Comparator<IChromosome<T>>() {
            @Override
            public int compare(IChromosome<T> o1, IChromosome<T> o2) {
                double c = fitnessFunctions.get(0).getFitness(o1)
                           - fitnessFunctions.get(0).getFitness(o2);
                if (c > 0) {
                    return 1;
                } else if (c < 0) {
                    return -1;
                }
                return 0;
            }
        });
        return list;
    }
}
