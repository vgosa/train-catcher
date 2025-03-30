package org.group21.trainsearch.camunda.workflows;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.group21.trainsearch.camunda.ModelBuilderHelper;
import org.group21.trainsearch.camunda.activities.PaymentCheckUserBalance;
import org.group21.trainsearch.camunda.activities.PaymentCompensateProcessPayment;
import org.group21.trainsearch.camunda.activities.PaymentProcessPayment;
import org.group21.trainsearch.model.Route;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class TicketPaymentWorkflow implements ExecutionListener {

    public static final String PAYMENT_WORKFLOW_NAME = "TicketPaymentWorkflow";
    public static final String FAILURE_REASON = "failureReason";
    public static final String DO_NOT_RETRY = "DoNotRetry";
    public static final String VARIABLE_SUFFICIENT_BALANCE = "userHasSufficientBalance";
    public static final String VARIABLE_ROUTE = "route";
    public static final String VARIABLE_USER_ID = "userId";
    public static final String USER_SERVICE_URL = "http://user/user";
    public static final String OPERATOR_CONTEXT_PATH = "/operator";
    private final ProcessEngine camunda;

    @Getter
    public AtomicBoolean wasUserCredited = new AtomicBoolean(false);
    @Getter
    private final Deque<PaymentJob> paymentJobs = new ConcurrentLinkedDeque<>();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TicketPaymentWorkflow(ProcessEngine camunda) {
        this.camunda = camunda;
    }

    @PostConstruct
    void defineCamundaWorkflow() {
        BpmnModelInstance workflow = ModelBuilderHelper.newModel(PAYMENT_WORKFLOW_NAME)
                .start()
                .activity("Check user balance", PaymentCheckUserBalance.class)
                .exclusiveGateway("Sufficient balance")
                .conditionExpression("No", String.format("${!execution.getVariable('%s')}", VARIABLE_SUFFICIENT_BALANCE))
                .endFail()
                .moveToLastGateway()
                .conditionExpression("Yes", String.format("${execution.getVariable('%s')}", VARIABLE_SUFFICIENT_BALANCE))
                .activity("Process payment", PaymentProcessPayment.class)
                .endSuccess()
                .addListener(ExecutionListener.EVENTNAME_START, this.getClass())
                .triggerCompensationOnErrorWithoutEnd(DO_NOT_RETRY)
                .activity("Compensate payment processing", PaymentCompensateProcessPayment.class)
                .end("Compensation")
                .addListener(ExecutionListener.EVENTNAME_END, this.getClass())
                .build();
        camunda.getRepositoryService().createDeployment()
                .addModelInstance(PAYMENT_WORKFLOW_NAME + ".bpmn", workflow)
                .deploy();
    }

    public void startTicketPaymentWorkflow(long userId, Route route) {
        camunda.getProcessEngineConfiguration().setCreateIncidentOnFailedJobEnabled(true);
        camunda.getProcessEngineConfiguration().setDefaultNumberOfRetries(10);

        camunda.getRuntimeService()
                .startProcessInstanceByKey(PAYMENT_WORKFLOW_NAME, Map.of(
                        VARIABLE_USER_ID, userId,
                        VARIABLE_ROUTE, route
                ));
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("Notification listener was called because a job is complete {}", execution);
        if (execution.getCurrentActivityName().contains(DO_NOT_RETRY)) {
            log.error("The job failed to execute. Reason: {}", execution.getVariable(FAILURE_REASON));
        } else {
            log.info("The job was successfully executed.");
            execution.getProcessEngine().getRuntimeService().correlateMessage("ChildProcessCompleted");
        }
    }

    public void addPaymentJob(PaymentJob job) {
        synchronized (paymentJobs) {
            if (paymentJobs.stream()
                    .anyMatch(e -> e.getOperatorName().equals(job.getOperatorName()))) {
                return;
            }
            paymentJobs.add(job);
        }
    }

    public void setWasUserCredited(boolean wasUserCredited) {
        this.wasUserCredited.set(wasUserCredited);
    }

    @Data
    @AllArgsConstructor
    public static class PaymentJob {
        private String operatorName;
        private double amount;
        private String url;
    }
}
