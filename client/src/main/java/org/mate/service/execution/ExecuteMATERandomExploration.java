package org.mate.service.execution;

import org.mate.IRepresentationLayerInterface;
import org.mate.MATE;
import org.mate.Properties;
import org.mate.commons.utils.MATELog;
import org.mate.exploration.heuristical.RandomExploration;

public class ExecuteMATERandomExploration {


    public static void run(String packageName, IRepresentationLayerInterface representationLayer) {

        MATELog.log_acc("Starting Random Exploration...");

        MATE mate = new MATE(packageName, representationLayer);

        final RandomExploration randomExploration
                = new RandomExploration(true, Properties.MAX_NUMBER_EVENTS());

        mate.testApp(randomExploration);
    }
}
