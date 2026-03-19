package com.example.payment.interfaces.rest.system;

import com.example.payment.application.service.SystemPayChannelApplicationService;
import com.example.payment.interfaces.rest.system.dto.PayChannelUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.PayChannelView;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/system/pay-channels")
public class SystemPayChannelController {

    private final SystemPayChannelApplicationService systemPayChannelApplicationService;

    public SystemPayChannelController(SystemPayChannelApplicationService systemPayChannelApplicationService) {
        this.systemPayChannelApplicationService = systemPayChannelApplicationService;
    }

    @GetMapping
    public List<PayChannelView> list() {
        return systemPayChannelApplicationService.list();
    }

    @PostMapping
    public PayChannelView create(@Valid @RequestBody PayChannelUpsertRequest request) {
        return systemPayChannelApplicationService.create(request);
    }

    @PutMapping("/{id}")
    public PayChannelView update(@PathVariable Long id, @Valid @RequestBody PayChannelUpsertRequest request) {
        return systemPayChannelApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        systemPayChannelApplicationService.delete(id);
    }
}