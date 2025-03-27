package org.group21.trainsearch.camunda.listeners;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;

public class PaymentProcessListener implements ExecutionListener {

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();

        String parentProcessInstanceId = (String) execution.getVariable("parentProcessInstanceId");

        if (parentProcessInstanceId != null) {
            // Signal the parent process using a message event
            runtimeService.createMessageCorrelation("ChildProcessCompleted")
                    .processInstanceId(parentProcessInstanceId)
                    .correlate();
        }
    }
}
