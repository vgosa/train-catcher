package org.group21.trainsearch.camunda;

import jakarta.annotation.PostConstruct;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.stereotype.Service;

@Service
public class TicketOrderWorkflow implements ExecutionListener {

    public static final String ORDER_WORKFLOW_NAME = "TicketOrderWorkflow";

    private final ProcessEngine camunda;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TicketOrderWorkflow(ProcessEngine camunda) {
        this.camunda = camunda;
    }

    @PostConstruct
    void defineCamundaWorkflow() {
        BpmnModelInstance workflow = null;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        //TODO: This is triggered when a job is finally executed
    }
}
