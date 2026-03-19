package com.example.payment.interfaces.rest.system.dto;

import java.util.List;

public class RoleView {

    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
    private List<Long> menuIds;

    public static RoleView of(Long id, String roleCode, String roleName, Integer status, List<Long> menuIds) {
        RoleView view = new RoleView();
        view.id = id;
        view.roleCode = roleCode;
        view.roleName = roleName;
        view.status = status;
        view.menuIds = menuIds;
        return view;
    }

    public Long getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }
}