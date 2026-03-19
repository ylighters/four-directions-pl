package com.example.payment.application.service;

import com.example.payment.infrastructure.persistence.mapper.SysMenuMapper;
import com.example.payment.infrastructure.persistence.mapper.SysRoleMenuRelMapper;
import com.example.payment.infrastructure.persistence.po.SysMenuPO;
import com.example.payment.interfaces.rest.system.dto.MenuUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.MenuView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemMenuApplicationService {

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleMenuRelMapper sysRoleMenuRelMapper;

    public SystemMenuApplicationService(SysMenuMapper sysMenuMapper, SysRoleMenuRelMapper sysRoleMenuRelMapper) {
        this.sysMenuMapper = sysMenuMapper;
        this.sysRoleMenuRelMapper = sysRoleMenuRelMapper;
    }

    public List<MenuView> list() {
        return sysMenuMapper.selectAll().stream().map(this::toView).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public MenuView create(MenuUpsertRequest request) {
        SysMenuPO menu = new SysMenuPO();
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuPath(request.getMenuPath());
        menu.setIcon(request.getIcon());
        menu.setSortNo(request.getSortNo());
        menu.setStatus(request.getStatus());
        sysMenuMapper.insert(menu);
        return toView(sysMenuMapper.selectById(menu.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public MenuView update(Long id, MenuUpsertRequest request) {
        SysMenuPO menu = new SysMenuPO();
        menu.setId(id);
        menu.setParentId(request.getParentId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuPath(request.getMenuPath());
        menu.setIcon(request.getIcon());
        menu.setSortNo(request.getSortNo());
        menu.setStatus(request.getStatus());
        sysMenuMapper.update(menu);
        return toView(sysMenuMapper.selectById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysRoleMenuRelMapper.deleteByMenuId(id);
        sysMenuMapper.deleteById(id);
    }

    private MenuView toView(SysMenuPO menu) {
        return MenuView.of(menu.getId(), menu.getParentId(), menu.getMenuName(), menu.getMenuPath(), menu.getIcon(), menu.getSortNo(), menu.getStatus());
    }
}