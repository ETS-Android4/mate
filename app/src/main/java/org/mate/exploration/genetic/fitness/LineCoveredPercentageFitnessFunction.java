package org.mate.exploration.genetic.fitness;

import org.mate.MATE;
import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.model.TestCase;
import org.mate.utils.FitnessUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineCoveredPercentageFitnessFunction implements IFitnessFunction<TestCase> {
    public static final String FITNESS_FUNCTION_ID = "line_covered_percentage_fitness_function";

    //todo: find better solution than static map... (i know its ugly)
    private static final Map<String, Map<IChromosome<TestCase>, Double>> cache = new HashMap<>();
    private static List<String> lines = new ArrayList<>();
    private final String line;

    public LineCoveredPercentageFitnessFunction(String line) {
        this.line = line;
        lines.add(line);
    }

    @Override
    public double getFitness(IChromosome<TestCase> chromosome) {
        if (!cache.get(line).containsKey(chromosome)) {
            throw new IllegalStateException("Fitness for chromosome " + chromosome + " not in cache. Must fetch fitness previously or performance reasons");
        }
        return cache.get(line).get(chromosome);

    }

    public static void retrieveFitnessValues(IChromosome<TestCase> chromosome) {
        if (lines.size() == 0) {
            return;
        }

        if (cache.size() == 0) {
            for (String line : lines) {
                cache.put(line, new HashMap<IChromosome<TestCase>, Double>());
            }
        }

        MATE.log_acc("retrieving fitness values for chromosome " + chromosome);
        List<Double> coveredPercentage = FitnessUtils.getFitness(chromosome, lines);
        for (int i = 0; i < coveredPercentage.size(); i++) {
            cache.get(lines.get(i)).put(chromosome, coveredPercentage.get(i));
        }
    }

    /**
     * remove chromosome from cache that are no longer in use. (to avoid memory issues)
     */
    public static <T> void cleanCache(List<IChromosome<T>> activeChromosomesAnon) {
        if (lines.size() == 0 || cache.size() == 0) {
            return;
        }

        List<IChromosome<TestCase>> activeChromosomes = new ArrayList<>();
        for (IChromosome<T> chromosome: activeChromosomesAnon) {
            activeChromosomes.add((IChromosome<TestCase>) chromosome);
        }

        int count = 0;
        for (String line : lines) {
            Map<IChromosome<TestCase>, Double> lineCache =  cache.get(line);
            for (IChromosome<TestCase> chromosome: new ArrayList<>(lineCache.keySet())) {
                if (!activeChromosomes.contains(chromosome)) {
                    lineCache.remove(chromosome);
                    count++;
                }
            }
        }
        MATE.log_acc("Cleaning cache: " + count + " inactive chromosome removed");
    }
}
