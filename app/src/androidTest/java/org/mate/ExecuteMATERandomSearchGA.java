package org.mate;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mate.exploration.genetic.algorithm.RandomSearch;
import org.mate.exploration.genetic.builder.GeneticAlgorithmBuilder;
import org.mate.exploration.genetic.chromosome_factory.AndroidRandomChromosomeFactory;
import org.mate.exploration.genetic.core.IGeneticAlgorithm;
import org.mate.exploration.genetic.fitness.BranchDistanceFitnessFunction;
import org.mate.exploration.genetic.termination.ConditionalTerminationCondition;
import org.mate.model.TestCase;

@RunWith(AndroidJUnit4.class)
public class ExecuteMATERandomSearchGA {

    @Test
    public void useAppContext() {
        MATE.log_acc("Starting Random Search GA ....");

        MATE mate = new MATE();

        final IGeneticAlgorithm<TestCase> randomSearchGA = new GeneticAlgorithmBuilder()
                .withAlgorithm(RandomSearch.ALGORITHM_NAME)
                .withChromosomeFactory(AndroidRandomChromosomeFactory.CHROMOSOME_FACTORY_ID)
                .withFitnessFunction(BranchDistanceFitnessFunction.FITNESS_FUNCTION_ID)
                .withTerminationCondition(ConditionalTerminationCondition.TERMINATION_CONDITION_ID)
                .withMaxNumEvents(Properties.MAX_NUMBER_EVENTS())
                .build();

        mate.testApp(randomSearchGA);
    }
}
