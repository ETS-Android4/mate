package org.mate.model;

import org.mate.MATE;
import org.mate.Properties;
import org.mate.Registry;
import org.mate.interaction.UIAbstractionLayer;
import org.mate.serialization.TestCaseSerializer;
import org.mate.state.IScreenState;
import org.mate.ui.Action;
import org.mate.ui.ActionType;
import org.mate.ui.PrimitiveAction;
import org.mate.ui.Widget;
import org.mate.ui.WidgetAction;
import org.mate.utils.Optional;
import org.mate.utils.Randomness;
import org.mate.utils.TestCaseStatistics;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestCase {
    private String id;
    private Set<String> visitedActivities;
    private Set<String> visitedStates;
    private List<Action> eventSequence;
    private List<String> activitySequence;
    private float novelty;
    private boolean crashDetected;
    private double sparseness;
    private HashMap<String, String> statesMap;
    private HashMap<String, Integer> featureVector;
    private Optional<Integer> desiredSize = Optional.none();
    private String crashStackTrace = null;


    public TestCase(String id){
        MATE.log("Initialising new test case!");
        setId(id);
        crashDetected = false;
        visitedActivities = new HashSet<>();
        visitedStates = new HashSet<>();
        eventSequence = new ArrayList<>();
        sparseness = 0;
        statesMap = new HashMap<>();
        featureVector = new HashMap<String, Integer>();
        activitySequence = new ArrayList<>();
    }

    /**
     * Should be called after the test case has been executed.
     * Among other things, this method is responsible for creating
     * coverage information if desired.
     */
    // TODO: ensure that finish() is properly called after each test case
    public void finish() {
        MATE.log("Finishing test case!");

        MATE.log("Found crash: " + getCrashDetected());

        // serialization of test case
        if (Properties.RECORD_TEST_CASE()) {
            TestCaseSerializer.serializeTestCase(this);
        }

        // record stats about a test case, in particular about intent based actions
        if (Properties.RECORD_TEST_CASE_STATS()) {
            TestCaseStatistics.recordStats(this);
        }

        MATE.log("Visited activities in order: " + activitySequence);

        // TODO: ensure that this log only appears here -> required for analysis framework
        MATE.log("Visited activities: " + getVisitedActivities());

        // TODO: log the test case actions in a proper format
    }

    /**
     * Prints how often each widget has been triggered by a certain action.
     */
    public void print() {

        // count how many actions per widgets have been executed
        Map<String, Integer> widgetActions = new HashMap<>();

        EnumSet<ActionType> widgetRelatedActions = EnumSet.of(ActionType.CLICK, ActionType.LONG_CLICK,
                ActionType.CLEAR_WIDGET, ActionType.TYPE_TEXT);

        // track the number of unrelated widget actions
        int widgetUnrelatedActions = 0;

        for (Action action : eventSequence) {
            if (action instanceof WidgetAction) {

                ActionType actionType = ((WidgetAction) action).getActionType();

                if (!widgetRelatedActions.contains(actionType)) {
                    widgetUnrelatedActions++;
                    continue;
                }

                Widget widget = ((WidgetAction) action).getWidget();

                String widgetName = widget.getClazz() + ":" + widget.getId();

                // with API level 24 (Java 8): Map.merge(key, 1, Integer::sum)
                if (widgetActions.containsKey(widgetName)) {
                    int currentCount = widgetActions.get(widgetName);
                    widgetActions.put(widgetName, currentCount+1);
                } else {
                    widgetActions.put(widgetName, 1);
                }
            }
        }

        // print how often each widget has been triggered
        for (Map.Entry<String, Integer> widgetEntry : widgetActions.entrySet()) {
            MATE.log_acc("Widget " + widgetEntry.getKey()
                    + " has been triggered: " + widgetEntry.getValue() + " times!");
        }

        // print the number of unrelated widget actions
        MATE.log_acc("Number of unrelated widget actions: " + widgetUnrelatedActions);
    }

    /**
     * Returns the activity name before the execution of the given action.
     * @param actionIndex The action index.
     * @return Returns the activity in foreground before the given action was executed.
     */
    public String getActivityBeforeAction(int actionIndex) {
        return activitySequence.get(actionIndex);
    }

    /**
     * Returns the name of the activity that is in the foreground after the execution
     * of the n-th {@param actionIndex} action.
     *
     * @param actionIndex The action index.
     * @return Returns the activity name after the execution of the {@param actionIndex} action.
     */
    public String getActivityAfterAction(int actionIndex) {
        // the activity sequence models a 'activity-before-action' relation
        return activitySequence.get(actionIndex + 1);
    }

    public void setDesiredSize(Optional<Integer> desiredSize) {
        this.desiredSize = desiredSize;
    }

    public Optional<Integer> getDesiredSize() {
        return desiredSize;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public void addEvent(Action event) {
        this.eventSequence.add(event);
    }

    public void updateVisitedActivities(String activity) {
        this.visitedActivities.add(activity);
    }

    public Set<String> getVisitedActivities() {
        return visitedActivities;
    }

    public void updateVisitedStates(IScreenState GUIState) {
        this.visitedStates.add(GUIState.getId());
    }

    public Set<String> getVisitedStates() {
        return visitedStates;
    }

    public List<Action> getEventSequence() {
        return this.eventSequence;
    }

    public boolean getCrashDetected() {
        return this.crashDetected;
    }

    public void setCrashDetected() {
        this.crashDetected=true;
    }

    public String getCrashStackTrace() {
        return crashStackTrace;
    }

    public void setNovelty(float novelty) {
        this.novelty = novelty;
    }

    public float getNovelty() {
        return novelty;
    }

    public double getSparseness() {
        return sparseness;
    }

    public void setSparseness(double sparseness) {
        this.sparseness = sparseness;
    }

    public void updateStatesMap(String state, String event) {
        if (!statesMap.containsKey(state)){
            statesMap.put(state, event);
            //MATE.log_acc("TEST___added to states map the state: "+state+" at event: "+event);
        }
    }
    public HashMap<String, String> getStatesMap() {
        return statesMap;
    }

    public HashMap<String, Integer> getFeatureVector() {
        return featureVector;
    }

    public void updateFeatureVector(IGUIModel guiModel) {
        List<IScreenState> guiStates = guiModel.getStates();
        for(IScreenState state : guiStates){
            if(this.visitedStates.contains(state.getId())){
                featureVector.put(state.getId(),1);
            } else {
                featureVector.put(state.getId(),0);
            }
        }
    }

    public static TestCase newDummy() {
        return new TestCase("dummy");
    }

    //TODO: Load test case from cache if it was executed before
    public static TestCase fromDummy(TestCase testCase) {
        MATE.uiAbstractionLayer.resetApp();
        TestCase resultingTc = newInitializedTestCase();

        int finalSize = testCase.eventSequence.size();

        if (testCase.desiredSize.hasValue()) {
            finalSize = testCase.desiredSize.getValue();
        }

        try {
            int count = 0;
            for (Action action0 : testCase.eventSequence) {
                if (count < finalSize) {
                    if (!(action0 instanceof WidgetAction) || MATE.uiAbstractionLayer.getExecutableActions().contains(action0)) {
                        if (!resultingTc.updateTestCase(action0, count)) {
                            return resultingTc;
                        }
                        count++;
                    } else {
                        break;
                    }
                } else {
                    return resultingTc;
                }
            }
            for (; count < finalSize; count++) {
                Action action;
                if (Properties.WIDGET_BASED_ACTIONS()) {
                    action = Randomness.randomElement(MATE.uiAbstractionLayer.getExecutableActions());
                } else {
                    action = PrimitiveAction.randomAction();
                }
                if (!resultingTc.updateTestCase(action, count)) {
                    return resultingTc;
                }
            }

            return resultingTc;
        } finally {
            // serialize test case, record test case stats, etc.
            resultingTc.finish();
        }
    }

    @Override
    public String toString() {
        return getId();
    }

    /**
     * Initializes
     * @return
     */
    public static TestCase newInitializedTestCase() {
        TestCase tc = new TestCase(UUID.randomUUID().toString());
        tc.updateTestCase("init");
        return tc;
    }

    /**
     * Perform action and update TestCase accordingly.
     *
     * @param action The action to be performed.
     * @param actionID The id of the action.
     * @return True if action successful inbound false if outbound, crash, or some unkown failure
     */
    public boolean updateTestCase(Action action, int actionID) {

        if (action instanceof WidgetAction
                && !MATE.uiAbstractionLayer.getExecutableActions().contains(action)) {
            throw new IllegalStateException("Action not applicable to current state!");
        }

        String activityBeforeAction = MATE.uiAbstractionLayer.getLastScreenState().getActivityName();
        MATE.log("executing action " + actionID);

        addEvent(action);
        UIAbstractionLayer.ActionResult actionResult = MATE.uiAbstractionLayer.executeAction(action);

        // track the activity transitions of each action
        String activityAfterAction = MATE.uiAbstractionLayer.getLastScreenState().getActivityName();

        if (actionID == 0) {
            activitySequence.add(activityBeforeAction);
            activitySequence.add(activityAfterAction);
        } else {
            activitySequence.add(activityAfterAction);
        }

        MATE.log("executed action " + actionID + ": " + action);
        MATE.log("Activity Transition for action " +  actionID
                + ":" + activityBeforeAction  + "->" + activityAfterAction);

        switch (actionResult) {
            case SUCCESS:
            case SUCCESS_NEW_STATE:
                updateTestCase(String.valueOf(actionID));
                return true;
            case FAILURE_APP_CRASH:
                setCrashDetected();
                if (Properties.RECORD_STACK_TRACE()) {
                    crashStackTrace = Registry.getEnvironmentManager().getLastCrashStackTrace();
                }
            case SUCCESS_OUTBOUND:
                return false;
            case FAILURE_UNKNOWN:
            case FAILURE_EMULATOR_CRASH:
                return false;
            default:
                throw new UnsupportedOperationException("Encountered an unknown action result. Cannot continue.");
        }
    }

    private void updateTestCase(String event) {
        IScreenState currentScreenstate = MATE.uiAbstractionLayer.getLastScreenState();

        updateVisitedStates(currentScreenstate);
        updateVisitedActivities(currentScreenstate.getActivityName());
        updateStatesMap(currentScreenstate.getId(), event);
    }
}
