package org.group21.trainsearch.camunda.workflows;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.group21.trainsearch.camunda.ModelBuilderHelper;
import org.group21.trainsearch.camunda.activities.*;
import org.group21.trainsearch.model.Route;
import org.group21.trainsearch.model.message.Notification;
import org.group21.trainsearch.service.WebSocketService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * This class is responsible for defining the Camunda workflow for the ticket order process.
 */
@Service
@Slf4j
public class TicketOrderWorkflow implements ExecutionListener {

    public static final String ORDER_WORKFLOW_NAME = "TicketOrderWorkflow";
    public static final String FAILURE_REASON = "failureReason";
    public static final String DO_NOT_RETRY = "DoNotRetry";
    public static final String VARIABLE_BOOKING_ID = "bookingId";
    public static final String VARIABLE_ROUTE = "route";
    public static final String VARIABLE_USER_ID = "userId";
    public static final String VARIABLE_USER = "user";
    public static final String VARIABLE_TICKET = "ticket";
    public static final String VARIABLE_TICKET_ID = "ticketId";
    public static final String VARIABLE_PAYMENT_METHOD = "payment_method";
    public static final String VARIABLE_PAYMENT_ID = "paymentId";
    public static final String BOOKING_SERVICE_URL = "http://booking/booking";
    public static final String TICKET_SERVICE_URL = "http://ticket/ticket";
    public static final String PAYMENT_SERVICE_URL = "http://payment/payment";

    private final ProcessEngine camunda;
    private final WebSocketService wsService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TicketOrderWorkflow(ProcessEngine camunda, WebSocketService wsService) {
        this.camunda = camunda;
        this.wsService = wsService;
    }

    @PostConstruct
    /**
     * Defines the Camunda workflow for the ticket order process. It uses the {@link ModelBuilderHelper} to create the
     * workflow.
     */
    void defineCamundaWorkflow() {
        BpmnModelInstance workflow = ModelBuilderHelper.newModel(ORDER_WORKFLOW_NAME)
                .start()
                .activity("Create booking", BookingCreateBooking.class)
                .compensationActivity("Compensate booking creation", BookingCompensateCreateBooking.class)
                .activity("Reserve seats", SeatBookingBlockSeats.class)
                .compensationActivity("Cancel reserved seats", SeatBookingCancelSeats.class)
                .activity("Create ticket for booking", TicketCreateTicket.class)
                .compensationActivity("Compensate ticket creation", TicketCompensateCreateTicket.class)
                .activity("Issue payment", PaymentIssuePayment.class)
                .compensationActivity("Compensate payment", PaymentCompensateIssuePayment.class)
                .receiveTask("Wait for payment", "ChildProcessCompleted")
                .activity("Check payment status", PaymentCheckPaymentStatus.class)
                .activity("Confirm booked seats", SeatBookingConfirmSeats.class)
                .activity("Send ticket via email", EmailSendTicket.class)
                .endSuccess()
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .triggerCompensationOnError(DO_NOT_RETRY)
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .build();
        camunda.getRepositoryService().createDeployment()
                .addModelInstance(ORDER_WORKFLOW_NAME + ".bpmn", workflow)
                .deploy();
    }

    /**
     * Starts the ticket order workflow for the given user and route.
     * @param userId The ID of the user who is ordering the ticket
     * @param route The {@link Route} for which the ticket is being ordered
     */
    public void startTicketOrderWorkflow(long userId, Route route) {
        camunda.getProcessEngineConfiguration().setDefaultNumberOfRetries(3);
        camunda.getProcessEngineConfiguration().setCreateIncidentOnFailedJobEnabled(true);
        UUID businessKey = UUID.randomUUID();

        camunda.getRuntimeService()
                .startProcessInstanceByKey(ORDER_WORKFLOW_NAME,
                        businessKey.toString(),
                            Map.of(VARIABLE_ROUTE, route,
                            VARIABLE_USER_ID, userId,
                            VARIABLE_PAYMENT_METHOD, "DEBIT"));
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        Notification notification;
        if (execution.hasVariable(FAILURE_REASON)) {
            log.error("The {} job instance with ID {} failed at activity ID {}: {}", getClass().getTypeName(),
                    execution.getProcessInstanceId(),
                    execution.getCurrentActivityId(),
                    execution.getVariable(FAILURE_REASON));
            notification = new Notification("Ticket Order Workflow Failure",
                    "The ticket order workflow failed at activity ID " + execution.getCurrentActivityId() +
                            ". Reason: " + execution.getVariable(FAILURE_REASON) +
                            ". Booking ID: " + execution.getVariable(VARIABLE_BOOKING_ID) +
                            ". Please contact support.");
        } else {
            log.info("The {} job was successfully executed. Booking ID: {}",
                   getClass().getTypeName(), execution.getVariable(VARIABLE_BOOKING_ID));
            notification = new Notification("Confirmation", "Your ticket was sent to your email address!");
        }
        wsService.sendNotification("/topic/notifications", notification);
    }
}
