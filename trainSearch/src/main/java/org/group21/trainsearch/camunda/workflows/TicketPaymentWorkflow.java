package org.group21.trainsearch.camunda.workflows;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.group21.trainsearch.camunda.ModelBuilderHelper;
import org.group21.trainsearch.camunda.activities.PaymentCheckUserBalance;
import org.group21.trainsearch.camunda.activities.PaymentCompensateProcessPayment;
import org.group21.trainsearch.camunda.activities.PaymentProcessPayment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TicketPaymentWorkflow implements ExecutionListener {

    public static final String PAYMENT_WORKFLOW_NAME = "TicketPaymentWorkflow";
    public static final String VARIABLE_SUFFICIENT_BALANCE = "userHasSufficientBalance";
    private final ProcessEngine camunda;

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
                .compensationActivity("Compensate payment processing", PaymentCompensateProcessPayment.class)
                .endSuccess()
                .build();
        camunda.getRepositoryService().createDeployment()
                .addModelInstance(PAYMENT_WORKFLOW_NAME + ".bpmn", workflow)
                .deploy();
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("Notification listener was called because a job is complete {}", execution);
        //TODO: Handle the completion of the job
    }
}
