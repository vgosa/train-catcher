package org.group21.trainsearch.listeners;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.model.HealthService;
import org.group21.trainsearch.model.Operator;
import org.group21.trainsearch.service.OperatorService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsulServiceListener {

    private final ConsulClient consulClient;

    private final OperatorService operatorService;

    public ConsulServiceListener(ConsulClient consulClient, OperatorService operatorService) {
        this.consulClient = consulClient;
        this.operatorService = operatorService;
    }

    /**
     * Checks the health status of all registered operators every 2.5 seconds. If
     * an operator is down, it updates the operator's status in the system and the
     * aggregator will not include it in the search results. When it comes back,
     * it will be included again.
     */
    @Scheduled(fixedRate = 2500)
    protected void operatorHealthCheck() {
        operatorService.getOperators().forEach(this::checkOperatorStatus);
    }

    /**
     * Checks the health status of a specific operator by querying the Consul API through
     * it's {@link ConsulClient}.
     * @param operator the operator to check
     */
    private void checkOperatorStatus(Operator operator) {
        // Ignore the deprecated method warning, it's fine for this use case
        Response<List<HealthService>> response = consulClient.getHealthServices(getOperatorServiceName(operator.getUrl()), true, null);
        if (response != null && response.getValue() != null) {
            List<HealthService> healthyServices = response.getValue();
            operatorService.changeOperatorActiveStatus(operator.getName(), !healthyServices.isEmpty());
        }
    }

    /**
     * Extracts the operator service name from the URL.
     * @param url the URL of the operator service from the service mesh
     * @return the operator service name as registered in Consul
     */
    private String getOperatorServiceName(String url) {
        return url.split(":")[1].replace("//", "");
    }
}
