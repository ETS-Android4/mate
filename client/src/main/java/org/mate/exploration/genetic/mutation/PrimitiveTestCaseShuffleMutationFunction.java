package org.mate.exploration.genetic.mutation;

import org.mate.commons.interaction.action.ui.PrimitiveAction;
import org.mate.commons.utils.MATELog;
import org.mate.Properties;
import org.mate.Registry;
import org.mate.exploration.genetic.chromosome.Chromosome;
import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.commons.interaction.action.Action;
import org.mate.model.TestCase;
import org.mate.utils.FitnessUtils;
import org.mate.commons.utils.Randomness;
import org.mate.utils.coverage.CoverageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a shuffle mutation function for {@link TestCase}s that is only applicable in combination
 * with {@link PrimitiveAction}.
 */
public class PrimitiveTestCaseShuffleMutationFunction implements IMutationFunction<TestCase> {

    /**
     * Performs a test case shuffle mutation. This is possible since the underlying primitive
     * actions are not associated with any widget.
     *
     * @param chromosome The chromosome to be mutated.
     * @return Returns the mutated chromosome.
     */
    @Override
    public IChromosome<TestCase> mutate(IChromosome<TestCase> chromosome) {

        List<Action> actions = new ArrayList<>(chromosome.getValue().getEventSequence());
        Randomness.shuffleList(actions);
        TestCase testCase = TestCase.newDummy();
        testCase.getEventSequence().addAll(actions);
        TestCase executedTestCase = TestCase.fromDummy(testCase);
        IChromosome<TestCase> mutatedChromosome = new Chromosome<>(executedTestCase);

        if(Properties.SURROGATE_MODEL()) {
            Registry.getUiAbstractionLayer().storeTraces();
        }

        FitnessUtils.storeTestCaseChromosomeFitness(mutatedChromosome);
        CoverageUtils.storeTestCaseChromosomeCoverage(mutatedChromosome);
        CoverageUtils.logChromosomeCoverage(mutatedChromosome);
        MATELog.log_acc("Found crash: " + chromosome.getValue().hasCrashDetected());

        executedTestCase.finish();

        return mutatedChromosome;
    }
}
