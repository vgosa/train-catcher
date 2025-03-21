package org.group21.trainsearch.camunda;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.group21.trainsearch.camunda.activities.*;
import org.group21.trainsearch.model.Route;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class TicketOrderWorkflow implements ExecutionListener {

    public static final String ORDER_WORKFLOW_NAME = "TicketOrderWorkflow";

    public static final String FAILURE_REASON = "failureReason";

    public static final String DO_NOT_RETRY = "DoNotRetry";

    public static final String VARIABLE_BOOKING_ID = "bookingId";

    public static final String VARIABLE_ROUTE = "route";

    public static final String VARIABLE_USER_ID = "userId";
    public static final String VARIABLE_TICKET = "ticket";

    public static final String VARIABLE_PAYMENT_METHOD = "payment_method";
    public static final String VARIABLE_PAYMENT_ID = "paymentId";

    private final ProcessEngine camunda;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TicketOrderWorkflow(ProcessEngine camunda) {
        this.camunda = camunda;
    }

    @PostConstruct
    void defineCamundaWorkflow() {
        BpmnModelInstance workflow = ModelBuilderHelper.newModel(ORDER_WORKFLOW_NAME)
                .start()
                .activity("Create booking", BookingCreateBooking.class)
                .compensationActivity("Compensate booking creation", BookingCompensateCreateBooking.class)
                .parallelStart()
                .activity("Create ticket for booking", TicketCreateTicket.class)
                .compensationActivity("Compensate ticket creation", TicketCompensateCreateTicket.class)
                .parallelNext()
                .activity("Issue payment", PaymentIssuePayment.class)
                .compensationActivity("Compensate payment", PaymentCompensateIssuePayment.class)
                .parallelEnd()
                .activity("Send ticket via email", EmailSendTicket.class)
                .end()
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .triggerCompensationOnError(DO_NOT_RETRY)
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .build();
        camunda.getRepositoryService().createDeployment()
                .addModelInstance("buyTicket.bpmn", workflow)
                .deploy();
    }

    public void startTicketOrderWorkflow(long userId, Route route) {
        camunda.getProcessEngineConfiguration().setDefaultNumberOfRetries(10);

        camunda.getRuntimeService()
                .startProcessInstanceByKey(ORDER_WORKFLOW_NAME, Map.of(VARIABLE_ROUTE, route,
                        VARIABLE_USER_ID, userId,
                        VARIABLE_PAYMENT_METHOD, "DEBIT"));
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        //TODO: This is triggered when a job is finally executed
        log.info("Notification listener was called because a job is complete {}", execution);
        if (execution.getCurrentActivityName().contains(DO_NOT_RETRY)) {
            log.error("An error occurred during the execution of the job. The job will not be retried.");
        } else {
            log.info("The job was successfully executed. Booking ID: {}",
                    execution.getVariable(VARIABLE_BOOKING_ID));
        }
    }
}
