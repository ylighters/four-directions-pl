package com.example.payment.interfaces.rest;

import com.example.payment.application.dto.CreatePaymentOrderRequest;
import com.example.payment.application.dto.CashierOrderResponse;
import com.example.payment.application.dto.CreateCashierOrderRequest;
import com.example.payment.application.dto.PaymentOrderListItem;
import com.example.payment.application.dto.PaymentOrderResponse;
import com.example.payment.application.service.PaymentApplicationService;
import com.example.payment.domain.enums.PaymentEventType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 支付订单对外REST接口。
 * 提供下单、收银台下单、订单查询与状态事件流转能力。
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    /**
     * 通用下单接口（商户服务端调用）。
     */
    @PostMapping("/orders")
    public PaymentOrderResponse createOrder(@Valid @RequestBody CreatePaymentOrderRequest request) {
        return paymentApplicationService.createOrder(request);
    }

    /**
     * 收银设备下单接口（POS/收银机调用）。
     */
    @PostMapping("/cashier/orders")
    public CashierOrderResponse createCashierOrder(@Valid @RequestBody CreateCashierOrderRequest request) {
        return paymentApplicationService.createCashierOrder(request);
    }

    /**
     * 按商户和应用分页查询订单列表。
     */
    @GetMapping("/orders")
    public List<PaymentOrderListItem> listOrders(@RequestParam String merchantNo,
                                                 @RequestParam String appId,
                                                 @RequestParam(defaultValue = "1") int pageNo,
                                                 @RequestParam(defaultValue = "20") int pageSize) {
        return paymentApplicationService.listOrders(merchantNo, appId, pageNo, pageSize);
    }

    /**
     * 手动推动订单状态机事件（用于补单、对账、测试）。
     */
    @PostMapping("/orders/{orderNo}/events")
    public PaymentOrderResponse handleEvent(@PathVariable String orderNo,
                                            @RequestParam PaymentEventType eventType,
                                            @RequestParam(defaultValue = "system") String operator) {
        return paymentApplicationService.applyEvent(orderNo, eventType, operator);
    }
}

