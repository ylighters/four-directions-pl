package com.example.payment.interfaces.rest.system.dto;

public class MenuView {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuPath;
    private String icon;
    private Integer sortNo;
    private Integer status;

    public static MenuView of(Long id, Long parentId, String menuName, String menuPath, String icon, Integer sortNo, Integer status) {
        MenuView view = new MenuView();
        view.id = id;
        view.parentId = parentId;
        view.menuName = menuName;
        view.menuPath = menuPath;
        view.icon = icon;
        view.sortNo = sortNo;
        view.status = status;
        return view;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuPath() {
        return menuPath;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public Integer getStatus() {
        return status;
    }
}