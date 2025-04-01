package org.group21.payment.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.group21.payment.annotation.EnumValue;
import org.group21.payment.model.Payment;
import org.group21.payment.model.PaymentMethod;
import org.group21.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Spring also allows defining enums return values instead of pure strings, but I find this validation
    // method of String -> Enum more versatile, and easier to debug (less abstraction by Springboot...)
    @PostMapping("/{method}")
    public ResponseEntity<Payment> issuePayment(@PathVariable("method") @EnumValue(enumClass = PaymentMethod.class) String method,
                                                @RequestParam("userId") @NotNull @Min(0) Long userId,
                                                @RequestParam("bookingId") @NotNull @Min(0) Long bookingId) {
        Payment newPayment = paymentService.addPayment(userId, bookingId, PaymentMethod.valueOf(method.toUpperCase()), true);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPayment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("paymentId") @Min(0) Long paymentId) {
        Optional<Payment> paymentOpt = paymentService.getPaymentById(paymentId);
        return paymentOpt.map(ResponseEntity::ok).orElseThrow(() -> new EntityNotFoundException("Payment with id " + paymentId + " not found"));
    }
}
