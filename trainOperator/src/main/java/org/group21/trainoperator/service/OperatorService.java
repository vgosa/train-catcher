package org.group21.trainoperator.service;

import org.group21.trainoperator.exception.OperatorNotFoundException;
import org.group21.trainoperator.model.Operator;
import org.group21.trainoperator.repository.OperatorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class OperatorService {

    private final OperatorRepository operatorRepository;

    public OperatorService(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public List<Operator> getAllOperators() {
        return operatorRepository.findAll();
    }

    public Optional<Operator> getOperatorById(Long id) {
        return operatorRepository.findById(id);
    }

    public Operator addOperator(Operator operator) {
        operator.setId(null); // Auto-generated ID
        return operatorRepository.save(operator);
    }

    public Optional<Operator> topUpBalance(Long id, double amount) {
        Optional<Operator> operator = operatorRepository.findById(id);
        if (operator.isPresent()) {
            Operator updatedOperator = operator.get();
            updatedOperator.setBalance(updatedOperator.getBalance() + amount);
            operatorRepository.save(updatedOperator);
            return Optional.of(updatedOperator);
        }
        return Optional.empty();
    }

    public Optional<Operator> deductBalance(Long id, double amount) {
        Optional<Operator> operator = operatorRepository.findById(id);
        if (operator.isPresent()) {
            Operator updatedOperator = operator.get();
            updatedOperator.setBalance(updatedOperator.getBalance() - amount);
            operatorRepository.save(updatedOperator);
            return Optional.of(updatedOperator);
        }
        return Optional.empty();
    }

    public Operator updateOperator(Long id, Operator operator) throws OperatorNotFoundException {
        return operatorRepository.findById(id)
                .map(existingOperator -> {
                    existingOperator.setName(operator.getName());
                    existingOperator.setBalance(operator.getBalance());
                    return operatorRepository.save(existingOperator);
                })
                .orElseThrow(() -> new OperatorNotFoundException("Operator not found during update operation", id));
    }

    public void deleteOperator(Long id) {
        operatorRepository.deleteById(id);
    }
}
