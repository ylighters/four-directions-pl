package com.example.payment.interfaces.rest;

import com.example.payment.application.dto.CreatePaymentOrderRequest;
import com.example.payment.application.dto.PaymentOrderResponse;
import com.example.payment.application.service.PaymentApplicationService;
import com.example.payment.domain.enums.PaymentEventType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @PostMapping("/orders")
    public PaymentOrderResponse createOrder(@Valid @RequestBody CreatePaymentOrderRequest request) {
        return paymentApplicationService.createOrder(request);
    }

    @PostMapping("/orders/{orderNo}/events")
    public PaymentOrderResponse handleEvent(@PathVariable String orderNo,
                                            @RequestParam PaymentEventType eventType,
                                            @RequestParam(defaultValue = "system") String operator) {
        return paymentApplicationService.applyEvent(orderNo, eventType, operator);
    }
}

