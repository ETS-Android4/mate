package org.mate.exploration.genetic.crossover;

import org.mate.Properties;
import org.mate.Registry;
import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.Randomness;
import org.mate.exploration.genetic.chromosome.Chromosome;
import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.model.TestCase;
import org.mate.utils.FitnessUtils;
import org.mate.utils.coverage.CoverageUtils;

import java.util.Collections;
import java.util.List;

/**
 * Provides a crossover function for {@link TestCase}s produced by the
 * {@link org.mate.exploration.genetic.chromosome_factory.PrimitiveAndroidRandomChromosomeFactory}.
 */
public class PrimitiveTestCaseMergeCrossOverFunction implements ICrossOverFunction<TestCase> {

    /**
     * Whether to directly execute the actions during crossover or not.
     */
    private boolean executeActions;

    /**
     * Initialises a new crossover function that is used for test cases produced by the primitive
     * chromosome factory.
     */
    public PrimitiveTestCaseMergeCrossOverFunction() {
        executeActions = false;
    }

    /**
     * Sets whether the actions should be directly or not.
     *
     * @param executeActions Whether the actions should be directly executed.
     */
    public void setExecuteActions(boolean executeActions) {
        this.executeActions = executeActions;
    }

    /**
     * Performs a crossover on the given parents.
     *
     * @param parents The parents that undergo crossover.
     * @return Returns the generated offsprings.
     */
    @Override
    public List<IChromosome<TestCase>> cross(List<IChromosome<TestCase>> parents) {

        if (parents.size() == 1) {
            MATELog.log_warn("PrimitiveTestCaseMergeCrossOverFunction not applicable on single chromosome!");
            return Collections.singletonList(parents.get(0));
        }
        
        TestCase parent0 = parents.get(0).getValue();
        TestCase parent1 = parents.get(1).getValue();
        TestCase offspring = TestCase.newDummy();
        int l0 = parent0.getEventSequence().size() / 2;
        int l1 = parent1.getEventSequence().size() / 2;
        int start0 = Randomness.getRnd().nextInt(l0 + 1);
        int start1 = Randomness.getRnd().nextInt(l1 + 1);
        offspring.getEventSequence().addAll(parent0.getEventSequence().subList(start0, start0 + l0));
        offspring.getEventSequence().addAll(parent1.getEventSequence().subList(start1, start1 + l1));

        if (executeActions) {
            TestCase executedTestCase = TestCase.fromDummy(offspring);
            Chromosome<TestCase> chromosome = new Chromosome<>(executedTestCase);

            if(Properties.SURROGATE_MODEL()) {
                Registry.getUiAbstractionLayer().storeTraces();
            }

            FitnessUtils.storeTestCaseChromosomeFitness(chromosome);
            CoverageUtils.storeTestCaseChromosomeCoverage(chromosome);
            CoverageUtils.logChromosomeCoverage(chromosome);

            executedTestCase.finish();

            return Collections.singletonList(chromosome);
        }
        return Collections.singletonList(new Chromosome<>(offspring));
    }
}
