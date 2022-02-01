package org.mate.service.execution;

import org.mate.IRepresentationLayerInterface;
import org.mate.MATE;
import org.mate.Properties;
import org.mate.exploration.genetic.algorithm.Algorithm;
import org.mate.exploration.genetic.builder.GeneticAlgorithmBuilder;
import org.mate.exploration.genetic.chromosome_factory.ChromosomeFactory;
import org.mate.exploration.genetic.core.IGeneticAlgorithm;
import org.mate.exploration.genetic.crossover.CrossOverFunction;
import org.mate.exploration.genetic.fitness.FitnessFunction;
import org.mate.exploration.genetic.mutation.MutationFunction;
import org.mate.exploration.genetic.selection.SelectionFunction;

public class ExecuteMATESapienz {

    public static void run(String packageName, IRepresentationLayerInterface representationLayer) {
        MATE.log_acc("Starting Evolutionary Search...");
        MATE.log_acc("Sapienz implementation");

        MATE mate = new MATE(packageName, representationLayer);

        final IGeneticAlgorithm sapienz =
                new GeneticAlgorithmBuilder()
                        .withAlgorithm(Algorithm.SAPIENZ)
                        .withChromosomeFactory(ChromosomeFactory.SAPIENZ_SUITE_RANDOM_CHROMOSOME_FACTORY)
                        .withCrossoverFunction(CrossOverFunction.TEST_SUITE_UNIFORM_CROSS_OVER)
                        .withSelectionFunction(SelectionFunction.RANDOM_SELECTION)
                        .withMutationFunction(MutationFunction.SAPIENZ_MUTATION)
                        .withFitnessFunction(FitnessFunction.BRANCH_COVERAGE)
                        .withFitnessFunction(FitnessFunction.NUMBER_OF_CRASHES)
                        .withFitnessFunction(FitnessFunction.TEST_LENGTH)
                        .withTerminationCondition(Properties.TERMINATION_CONDITION())
                        .withPopulationSize(Properties.POPULATION_SIZE())
                        .withBigPopulationSize(Properties.BIG_POPULATION_SIZE())
                        .withPMutate(Properties.P_MUTATE())
                        .withPCrossover(Properties.P_CROSSOVER())
                        .withNumTestCases(Properties.NUMBER_TESTCASES())
                        .build();

        mate.testApp(sapienz);
    }
}
