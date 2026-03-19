package com.example.payment.interfaces.rest.auth;

public class LoginMenuItem {

    private Long id;
    private Long parentId;
    private String menuName;
    private String menuPath;
    private String icon;
    private Integer sortNo;

    public static LoginMenuItem of(Long id, Long parentId, String menuName, String menuPath, String icon, Integer sortNo) {
        LoginMenuItem item = new LoginMenuItem();
        item.id = id;
        item.parentId = parentId;
        item.menuName = menuName;
        item.menuPath = menuPath;
        item.icon = icon;
        item.sortNo = sortNo;
        return item;
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
}
