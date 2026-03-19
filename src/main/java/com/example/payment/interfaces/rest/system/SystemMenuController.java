package com.example.payment.interfaces.rest.system;

import com.example.payment.application.service.SystemMenuApplicationService;
import com.example.payment.interfaces.rest.system.dto.MenuUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.MenuView;
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
@RequestMapping("/api/system/menus")
public class SystemMenuController {

    private final SystemMenuApplicationService systemMenuApplicationService;

    public SystemMenuController(SystemMenuApplicationService systemMenuApplicationService) {
        this.systemMenuApplicationService = systemMenuApplicationService;
    }

    @GetMapping
    public List<MenuView> list() {
        return systemMenuApplicationService.list();
    }

    @PostMapping
    public MenuView create(@Valid @RequestBody MenuUpsertRequest request) {
        return systemMenuApplicationService.create(request);
    }

    @PutMapping("/{id}")
    public MenuView update(@PathVariable Long id, @Valid @RequestBody MenuUpsertRequest request) {
        return systemMenuApplicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        systemMenuApplicationService.delete(id);
    }
}