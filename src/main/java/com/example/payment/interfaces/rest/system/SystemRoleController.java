package com.example.payment.interfaces.rest.system;

import com.example.payment.application.service.SystemRoleApplicationService;
import com.example.payment.interfaces.rest.system.dto.RoleUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.RoleView;
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
@RequestMapping("/api/system/roles")
public class SystemRoleController {

    private final SystemRoleApplicationService systemRoleApplicationService;

    public SystemRoleController(SystemRoleApplicationService systemRoleApplicationService) {
        this.systemRoleApplicationService = systemRoleApplicationService;
    }

    @GetMapping
    public List<RoleView> list() {
        return systemRoleApplicationService.list();
    }

    @PostMapping
    public RoleView create(@Valid @RequestBody RoleUpsertRequest request) {
        return systemRoleApplicationService.create(request);
    }

    @PutMapping("/{id}")
    public RoleView update(@PathVariable Long id, @Valid @RequestBody RoleUpsertRequest request) {
        return systemRoleApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        systemRoleApplicationService.delete(id);
    }
}