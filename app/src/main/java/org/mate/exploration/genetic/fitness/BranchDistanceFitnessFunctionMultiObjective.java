package org.mate.exploration.genetic.fitness;

import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.utils.FitnessUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates the fitness value for a given test case as defined in:
 * "It Does Matter How You Normalise the Branch Distance in Search Based Software Testing"
 *
 * @author Michael Auer
 */
public class BranchDistanceFitnessFunctionMultiObjective<T> implements IFitnessFunction<T> {

    public static final String FITNESS_FUNCTION_ID = "branch_distance_fitness_function_multi_objective";

    // a cache that stores for each branch the set of test cases and its fitness value
    private static final Map<String, Map<IChromosome, Double>> cache = new HashMap<>();

    // all branches (shared by instances)
    private static List<String> branches = new ArrayList<>();

    // the current branch we want to evaluate this fitness function against
    private final String branch;

    /**
     * Initialises the fitness function with the given branch as target.
     *
     * @param branch The target branch.
     */
    public BranchDistanceFitnessFunctionMultiObjective(String branch) {
        this.branch = branch;
        branches.add(branch);
        cache.put(branch, new HashMap<IChromosome, Double>());
    }

    /**
     * Retrieves the branch distance fitness value for the given chromosome.
     * A cache is employed to make subsequent requests faster.
     *
     * @param chromosome The chromosome for which we want to retrieve its fitness value.
     * @return Returns the fitness value for the given chromosome.
     */
    @Override
    public double getFitness(IChromosome<T> chromosome) {

        double branchDistance;

        if (cache.get(branch).containsKey(chromosome)) {
            branchDistance = cache.get(branch).get(chromosome);
        } else {
            // retrieves the fitness value for every single branch
            List<Double> branchDistanceVector = FitnessUtils.getFitness(chromosome, null);

            // insert them into the cache
            for (int i = 0; i < branchDistanceVector.size(); i++) {
                cache.get(branches.get(i)).put(chromosome, branchDistanceVector.get(i));
            }

            branchDistance = cache.get(branch).get(chromosome);
        }

        return branchDistance;
    }
}
