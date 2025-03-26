package org.group21.trainsearch.camunda;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.*;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.group21.trainsearch.camunda.exceptions.CompensationActivityException;
import org.group21.trainsearch.camunda.exceptions.ParallelGatewayException;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Class used to build BPMN models in code. It provides a builder pattern to create a BPMN model instance.
 * The main advantage of building models in code is that they can be versioned with git, which is easier to maintain.
 */
public class ModelBuilderHelper {

    private final String name;
    private final Deque<ParallelGatewayData> parallelGateways = new ArrayDeque<>();
    private final ProcessBuilder process;
    private final String retryTimeCycle;
    private int parallelGatewayNumber = 0;
    @SuppressWarnings("rawtypes")
    private AbstractFlowNodeBuilder saga;
    private BpmnModelInstance bpmnModelInstance;

    /**
     * Create a new model builder with default retry time cycle.
     * The default retry time cycle is set to 2 retries with 6 seconds between them.
     * @param name name of the process
     */
    public ModelBuilderHelper(String name) {
        this(name, "R2/PT6S");
    }

    /**
     * Create a new model builder with custom retry time cycle.
     * The retry time cycle follows the ISO_8601 standard.
     * @see <a href="https://en.wikipedia.org/wiki/ISO_8601#Durations">ISO 8601 Durations</a>
     * @param name name of the process
     * @param retryTimeCycle retry time cycle
     */
    public ModelBuilderHelper(String name, String retryTimeCycle) {
        this.name = name;
        this.retryTimeCycle = retryTimeCycle;
        process = Bpmn.createExecutableProcess(name);
    }

    /**
     * Create a new model. This is the entry point for any workflow definition.
     * @param name
     * @return
     */
    public static ModelBuilderHelper newModel(String name) {
        return new ModelBuilderHelper(name);
    }

    /**
     * Build the BPMN model instance.
     * @return BPMN model instance
     */
    public BpmnModelInstance build() {
        if (bpmnModelInstance == null) {
            bpmnModelInstance = saga.done();
        }
        return bpmnModelInstance;
    }

    /**
     * Start node of the process.
     * @return builder
     */
    public ModelBuilderHelper start() {
        saga = process.startEvent("Start-" + name);
        return this;
    }

    /**
     * End node of the process.
     * @return builder
     */
    public ModelBuilderHelper endSuccess() {
        saga = saga.endEvent("EndSuccess-" + name);
        return this;
    }

    /**
     * End node of the process in case of failure.
     * @return builder
     */
    public ModelBuilderHelper endFail() {
        saga = saga.endEvent("EndFail-" + name);
        return this;
    }

    /**
     * Define an activity in the BPMN flow.
     * The activity is asynchronous and is retried according to the retry time cycle.
     * @param name name of the activity
     * @param adapterClass class implementing the activity logic (must implement {@link org.camunda.bpm.engine.delegate.JavaDelegate})
     * @return builder
     */
    @SuppressWarnings("rawtypes")
    public ModelBuilderHelper activity(String name, Class adapterClass) {
        // this is very handy and could also be done inline above directly
        String id = "Activity-" + stringToID(name);
        saga = saga.serviceTask(id)
                .name(name)
                .camundaClass(adapterClass)
                .camundaAsyncBefore()
                .camundaFailedJobRetryTimeCycle(retryTimeCycle);
        return this;
    }

    /**
     * Define a SAGA compensation activity for the last defined activity on the BPMN flow.
     * @param name name of the compensation activity
     * @param adapterClass class implementing the compensation logic (must implement {@link org.camunda.bpm.engine.delegate.JavaDelegate})
     * @return builder
     */
    @SuppressWarnings("rawtypes")
    public ModelBuilderHelper compensationActivity(String name, Class adapterClass) {
        if (!(saga instanceof AbstractActivityBuilder)) {
            throw new CompensationActivityException("Compensation activity can only be specified right after activity");
        }

        String id = "Activity-" + stringToID(name) + "-compensation";

        ((AbstractActivityBuilder) saga)
                .boundaryEvent()
                .compensateEventDefinition()
                .compensateEventDefinitionDone()
                .compensationStart()
                .serviceTask(id)
                .name(name)
                .camundaClass(adapterClass)
                .camundaAsyncBefore()
                .camundaFailedJobRetryTimeCycle(retryTimeCycle)
                .compensationDone();

        return this;
    }

    /**
     * Trigger a compensation strategy on error. Used to define a compensation flow for a given error code globally.
     * @param errorCode error code to trigger the compensation
     * @return builder
     */
    public ModelBuilderHelper triggerCompensationOnError(String errorCode) {
        saga = process.eventSubProcess()
                .startEvent("CatchError-" + stringToID(errorCode))
                .error(errorCode)
                .intermediateThrowEvent("Compensate-" + stringToID(errorCode))
                .compensateEventDefinition().compensateEventDefinitionDone()
                .endEvent("EndError-" + stringToID(errorCode));
        return this;
    }

    /**
     * Allows to register listener to events and activities. Call it immediately after building an event or activity.
     *
     * @param event One of {@link ExecutionListener#EVENTNAME_START}, {@link ExecutionListener#EVENTNAME_END},
     *                      {@link ExecutionListener#EVENTNAME_TAKE}
     * @param listenerClass Class implementing {@link ExecutionListener} interface that will be notified
     * @return builder
     */
    public ModelBuilderHelper addListener(String event, Class<? extends ExecutionListener> listenerClass) {
        saga = saga.camundaExecutionListenerClass(event, listenerClass);
        return this;
    }

    /**
     * Create an exclusive gateway to choose an execution path based on a condition expression {@link org.camunda.bpm.engine.delegate.Expression}
     * @param name name of the gateway
     * @return builder
     */
    public ModelBuilderHelper exclusiveGateway(String name) {
        String id= "ExclusiveGateway-" + stringToID(name);
        saga = saga.exclusiveGateway(id);
        return this;
    }

    /**
     * Define a condition expression for the last defined exclusive gateway. The expression uses the Unified
     * Expression Language (EL) specification. @link <a href="https://docs.camunda.org/manual/latest/user-guide/process-engine/expression-language/">Camunda EL</a>
     * @param name name of the condition branch
     * @param expression expression to evaluate
     * @return builder
     */
    public ModelBuilderHelper conditionExpression(String name, String expression) {
        if (!(saga instanceof ExclusiveGatewayBuilder)) {
            throw new ParallelGatewayException(
                    "Error. You are trying to define condition expression but no exclusive gateway has been started " +
                            "before!");
        }
        saga = saga.condition(name, expression);
        return this;
    }

    /**
     * Move to the last defined gateway. To be used after defining a condition expression.
     * @return builder
     */
    public ModelBuilderHelper moveToLastGateway() {
        saga = saga.moveToLastGateway();
        return this;
    }

    /**
     * Create a parallel gateway for the next activities. All activities defined after this method will be executed in
     * parallel.
     * @return builder
     */
    public ModelBuilderHelper parallelStart() {
        parallelGatewayNumber++;
        int depth = parallelGateways.size();
        String id = parallelGatewayNumber + "-" + depth;
        var gateway = new ParallelGatewayData(id, false);
        parallelGateways.push(gateway);

        saga = saga.parallelGateway(gateway.forkId());

        return this;
    }

    /**
     * Move to the next parallel activity. This method should be called after defining each parallel activity.
     * The first time this method is called, a fork gateway is created. The next times, the activities need to be
     * connected to a join gateway (without creating a new fork).
     * @return builder
     */
    public ModelBuilderHelper parallelNext() {
        if (parallelGateways.isEmpty()) {
            throw new ParallelGatewayException(
                    "Error. You are trying to start parallel thread but no parallel gateway has been started before!");
        }
        ParallelGatewayData gateway = parallelGateways.peek();
        if (!gateway.hasParallelTasks) {
            // first time, need to create join gateway
            saga = saga.parallelGateway(gateway.joinId())
                    // and move to the fork
                    .moveToNode(gateway.forkId());
            // and raise the flag
            gateway.hasParallelTasks = true;
        } else {
            // 2nd or more-th time, need to connect to proper join gateway
            saga = saga.connectTo(gateway.joinId())
                    // and move to the fork again
                    .moveToNode(gateway.forkId());
        }
        return this;
    }

    /**
     * End the parallel gateway. This method should be called after all parallel activities have been defined.
     * @return builder
     */
    public ModelBuilderHelper parallelEnd() {
        if (parallelGateways.isEmpty()) {
            throw new ParallelGatewayException(
                    "Error. You are trying to end parallel gateway while it has not started before!");
        }
        ParallelGatewayData gateway = parallelGateways.pop();
        if (!gateway.hasParallelTasks) {
            throw new ParallelGatewayException(
                    "Error. This parallel gateway did not have any parallel tasks! Such configuration is not " +
                            "supported");
        }
        saga = saga.connectTo(gateway.joinId());

        return this;
    }

    /**
     * Helper method to change BPMN element names to IDs (replace spaces with dashes)
     * It's unsafe as hell, but it is much easier to work this way for now.
     *
     * @param name Activity name
     * @return ID safe for BPMN
     */
    private String stringToID(String name) {
        return name.replace(" ", "-");
    }

    /**
     * Definition class for a parallel gateway. It holds the gateway ID and a flag indicating if the gateway has
     * parallel tasks (activities).
     */
    private class ParallelGatewayData {
        private final String id;
        private boolean hasParallelTasks;

        public ParallelGatewayData(String id, boolean hasParallelTasks) {
            this.id = id;
            this.hasParallelTasks = hasParallelTasks;
        }

        private String forkId() {
            return String.format("fork-%s", id);
        }

        private String joinId() {
            return String.format("join-%s", id);
        }

    }
}
