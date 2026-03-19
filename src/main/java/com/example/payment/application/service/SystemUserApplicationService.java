package com.example.payment.application.service;

import com.example.payment.infrastructure.persistence.mapper.SysUserMapper;
import com.example.payment.infrastructure.persistence.mapper.SysUserRoleRelMapper;
import com.example.payment.infrastructure.persistence.po.SysUserPO;
import com.example.payment.interfaces.rest.system.dto.UserUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.UserView;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class SystemUserApplicationService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleRelMapper sysUserRoleRelMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SystemUserApplicationService(SysUserMapper sysUserMapper, SysUserRoleRelMapper sysUserRoleRelMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleRelMapper = sysUserRoleRelMapper;
    }

    public List<UserView> list() {
        return sysUserMapper.selectAll().stream()
                .map(user -> UserView.of(user.getId(), user.getUsername(), user.getDisplayName(), user.getStatus(),
                        sysUserRoleRelMapper.selectRoleIdsByUserId(user.getId())))
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserView create(UserUpsertRequest request) {
        SysUserPO user = new SysUserPO();
        user.setUsername(request.getUsername());
        user.setDisplayName(request.getDisplayName());
        user.setStatus(request.getStatus());
        user.setPassword(resolvePasswordForCreate(request.getPassword()));
        sysUserMapper.insert(user);
        bindRoles(user.getId(), request.getRoleIds());
        return UserView.of(user.getId(), user.getUsername(), user.getDisplayName(), user.getStatus(),
                sysUserRoleRelMapper.selectRoleIdsByUserId(user.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public UserView update(Long id, UserUpsertRequest request) {
        SysUserPO user = new SysUserPO();
        user.setId(id);
        user.setDisplayName(request.getDisplayName());
        user.setStatus(request.getStatus());
        sysUserMapper.update(user);

        if (StringUtils.hasText(request.getPassword())) {
            sysUserMapper.updatePassword(id, encodePassword(request.getPassword()));
        }
        bindRoles(id, request.getRoleIds());
        SysUserPO db = sysUserMapper.selectById(id);
        return UserView.of(db.getId(), db.getUsername(), db.getDisplayName(), db.getStatus(),
                sysUserRoleRelMapper.selectRoleIdsByUserId(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        sysUserRoleRelMapper.deleteByUserId(id);
        sysUserMapper.deleteById(id);
    }

    private void bindRoles(Long userId, List<Long> roleIds) {
        sysUserRoleRelMapper.deleteByUserId(userId);
        List<Long> finalRoleIds = roleIds == null ? Collections.emptyList() : roleIds;
        if (!finalRoleIds.isEmpty()) {
            sysUserRoleRelMapper.batchInsert(userId, finalRoleIds);
        }
    }

    private String resolvePasswordForCreate(String password) {
        if (!StringUtils.hasText(password)) {
            return encodePassword("123456");
        }
        return encodePassword(password);
    }

    private String encodePassword(String raw) {
        return passwordEncoder.encode(raw);
    }
}