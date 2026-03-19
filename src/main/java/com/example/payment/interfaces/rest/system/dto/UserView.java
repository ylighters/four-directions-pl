package com.example.payment.interfaces.rest.system.dto;

import java.util.List;

public class UserView {

    private Long id;
    private String username;
    private String displayName;
    private Integer status;
    private List<Long> roleIds;

    public static UserView of(Long id, String username, String displayName, Integer status, List<Long> roleIds) {
        UserView view = new UserView();
        view.id = id;
        view.username = username;
        view.displayName = displayName;
        view.status = status;
        view.roleIds = roleIds;
        return view;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Integer getStatus() {
        return status;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }
}