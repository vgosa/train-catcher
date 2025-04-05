package org.group21.trainsearch.camunda.workflows;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.group21.trainsearch.camunda.ModelBuilderHelper;
import org.group21.trainsearch.camunda.activities.*;
import org.springframework.stereotype.Service;

/**
 * @deprecated This class is not used directly, but is kept for reference purposes. The activities inside it
 * are used in the TicketOrderWorkflow class.
 */
@Slf4j
@Service
@Deprecated(forRemoval = true)
public class SeatBookingWorkflow implements ExecutionListener {

    public static final String SEAT_WORKFLOW_NAME = "SeatBookingWorkflow";
    public static final String PAYMENT_SUCCESS_VARIABLE = "paymentSuccessful";
    public static final String DO_NOT_RETRY = "DoNotRetry";

    private final ProcessEngine camunda;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SeatBookingWorkflow(ProcessEngine camunda) {
        this.camunda = camunda;
    }

    @PostConstruct
    public void defineSeatBookingWorkflow() {
        log.info("Deploying SeatBookingWorkflow BPMN model.");
        BpmnModelInstance workflow = ModelBuilderHelper.newModel(SEAT_WORKFLOW_NAME)
                .start()
                .activity("Block Seats", SeatBookingBlockSeats.class)
                .intermediateCatchEvent("Wait for Payment Outcome", "PaymentOutcomeSignal")
                .exclusiveGateway("Payment Outcome Decision")
                .conditionExpression("Payment Successful", "${" + PAYMENT_SUCCESS_VARIABLE + "}")
                .activity("Confirm Seats", SeatBookingConfirmSeats.class)
                .end("SuccessEnd")
                .moveToLastGateway()
                .conditionExpression("Payment Failed", "${!" + PAYMENT_SUCCESS_VARIABLE + "}")
                .activity("Cancel Seats", SeatBookingCancelSeats.class)
                .end("CancelEnd")
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .triggerCompensationOnError(DO_NOT_RETRY)
                .build();

        camunda.getRepositoryService().createDeployment()
                .addModelInstance(SEAT_WORKFLOW_NAME + ".bpmn", workflow)
                .deploy();
        log.info("SeatBookingWorkflow deployed successfully.");
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        if (execution.hasVariable(TicketOrderWorkflow.FAILURE_REASON)) {
            log.error("SeatBookingWorkflow failed: {}", execution.getVariable(TicketOrderWorkflow.FAILURE_REASON));
        } else {
            log.info("SeatBookingWorkflow completed successfully.");
        }
    }
}
