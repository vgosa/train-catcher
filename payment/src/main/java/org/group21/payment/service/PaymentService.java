package org.group21.payment.service;

import jakarta.persistence.EntityNotFoundException;
import org.group21.payment.model.Payment;
import org.group21.payment.model.PaymentMethod;
import org.group21.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.MissingResourceException;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Optional<Payment> getPaymentById(long paymentId) {
       return paymentRepository.findById(paymentId);
    }

    public Payment addPayment(long userId, long bookingId, PaymentMethod paymentMethod, boolean isSuccess) {
        Payment payment = new Payment(userId, bookingId, paymentMethod, isSuccess);
        return paymentRepository.save(payment);
    }

    public Payment setPaymentStatus(long paymentId, boolean isSuccess) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setSuccess(isSuccess);
            return paymentRepository.save(payment);
        }
        throw new EntityNotFoundException("Payment with paymentId " + paymentId + " not found");
    }
}
