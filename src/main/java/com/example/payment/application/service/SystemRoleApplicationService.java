package com.example.payment.application.service;

import com.example.payment.infrastructure.persistence.mapper.SysRoleMapper;
import com.example.payment.infrastructure.persistence.mapper.SysRoleMenuRelMapper;
import com.example.payment.infrastructure.persistence.po.SysRolePO;
import com.example.payment.interfaces.rest.system.dto.RoleUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.RoleView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SystemRoleApplicationService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuRelMapper sysRoleMenuRelMapper;

    public SystemRoleApplicationService(SysRoleMapper sysRoleMapper, SysRoleMenuRelMapper sysRoleMenuRelMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysRoleMenuRelMapper = sysRoleMenuRelMapper;
    }

    public List<RoleView> list() {
        return sysRoleMapper.selectAll().stream()
                .map(role -> RoleView.of(role.getId(), role.getRoleCode(), role.getRoleName(), role.getStatus(),
                        sysRoleMenuRelMapper.selectMenuIdsByRoleId(role.getId())))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleView create(RoleUpsertRequest request) {
        SysRolePO role = new SysRolePO();
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setStatus(request.getStatus());
        sysRoleMapper.insert(role);
        bindMenus(role.getId(), request.getMenuIds());
        return RoleView.of(role.getId(), role.getRoleCode(), role.getRoleName(), role.getStatus(),
                sysRoleMenuRelMapper.selectMenuIdsByRoleId(role.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public RoleView update(Long id, RoleUpsertRequest request) {
        SysRolePO role = new SysRolePO();
        role.setId(id);
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setStatus(request.getStatus());
        sysRoleMapper.update(role);
        bindMenus(id, request.getMenuIds());
        SysRolePO db = sysRoleMapper.selectById(id);
        return RoleView.of(db.getId(), db.getRoleCode(), db.getRoleName(), db.getStatus(),
                sysRoleMenuRelMapper.selectMenuIdsByRoleId(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysRoleMenuRelMapper.deleteByRoleId(id);
        sysRoleMapper.deleteById(id);
    }

    private void bindMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuRelMapper.deleteByRoleId(roleId);
        List<Long> finalMenuIds = menuIds == null ? Collections.emptyList() : menuIds;
        if (!finalMenuIds.isEmpty()) {
            sysRoleMenuRelMapper.batchInsert(roleId, finalMenuIds);
        }
    }
}