package com.example.payment.interfaces.rest.system;

import com.example.payment.application.service.SystemUserApplicationService;
import com.example.payment.interfaces.rest.system.dto.UserUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.UserView;
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
@RequestMapping("/api/system/users")
public class SystemUserController {

    private final SystemUserApplicationService systemUserApplicationService;

    public SystemUserController(SystemUserApplicationService systemUserApplicationService) {
        this.systemUserApplicationService = systemUserApplicationService;
    }

    @GetMapping
    public List<UserView> list() {
        return systemUserApplicationService.list();
    }

    @PostMapping
    public UserView create(@Valid @RequestBody UserUpsertRequest request) {
        return systemUserApplicationService.create(request);
    }

    @PutMapping("/{id}")
    public UserView update(@PathVariable Long id, @Valid @RequestBody UserUpsertRequest request) {
        return systemUserApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        systemUserApplicationService.delete(id);
    }
}