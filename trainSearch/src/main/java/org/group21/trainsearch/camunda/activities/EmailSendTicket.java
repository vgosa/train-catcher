package org.group21.trainsearch.camunda.activities;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailSendTicket implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Does nothing, only happy flow implementation for now
        // TODO: Implement workflow sub-process to send email with ticket
        log.info(String.format("%s called with %s", getClass().getTypeName(), execution.getVariables()));
        log.info("Reached end of process. TODO: Send email with ticket.");
    }
}
