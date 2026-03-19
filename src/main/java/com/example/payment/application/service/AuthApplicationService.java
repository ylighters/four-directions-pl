package com.example.payment.application.service;

import com.example.payment.infrastructure.auth.LoginSession;
import com.example.payment.infrastructure.auth.TokenSessionStore;
import com.example.payment.infrastructure.persistence.mapper.SysMenuMapper;
import com.example.payment.infrastructure.persistence.mapper.SysRoleMapper;
import com.example.payment.infrastructure.persistence.mapper.SysUserMapper;
import com.example.payment.infrastructure.persistence.po.SysMenuPO;
import com.example.payment.infrastructure.persistence.po.SysRolePO;
import com.example.payment.infrastructure.persistence.po.SysUserPO;
import com.example.payment.interfaces.rest.auth.LoginMenuItem;
import com.example.payment.interfaces.rest.auth.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AuthApplicationService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final TokenSessionStore tokenSessionStore;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthApplicationService(SysUserMapper sysUserMapper,
                                  SysRoleMapper sysRoleMapper,
                                  SysMenuMapper sysMenuMapper,
                                  TokenSessionStore tokenSessionStore) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysMenuMapper = sysMenuMapper;
        this.tokenSessionStore = tokenSessionStore;
    }

    public LoginResponse login(String username, String password) {
        SysUserPO user = sysUserMapper.selectByUsername(username);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        if (!passwordMatches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        List<SysRolePO> roles = sysRoleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(SysRolePO::getRoleCode).toList();
        boolean admin = "admin".equalsIgnoreCase(user.getUsername()) || roleCodes.contains("ADMIN");
        List<Long> roleIds = roles.stream().map(SysRolePO::getId).toList();
        List<SysMenuPO> menus = admin
                ? sysMenuMapper.selectAll().stream().filter(m -> m.getStatus() != null && m.getStatus() == 1).toList()
                : roleIds.isEmpty()
                ? List.of()
                : sysMenuMapper.selectByRoleIds(roleIds).stream().filter(m -> m.getStatus() != null && m.getStatus() == 1).toList();

        LoginSession session = LoginSession.of(user.getId(), user.getUsername(), user.getDisplayName(), admin, roleCodes);
        String token = tokenSessionStore.create(session);

        return LoginResponse.of(token,
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                admin,
                roleCodes,
                menus.stream().map(this::toLoginMenu).toList());
    }

    public LoginResponse me(LoginSession session) {
        SysUserPO user = sysUserMapper.selectById(session.getUserId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录态已失效");
        }
        List<SysRolePO> roles = sysRoleMapper.selectByUserId(user.getId());
        List<Long> roleIds = roles.stream().map(SysRolePO::getId).toList();
        List<SysMenuPO> menus = session.isAdmin()
                ? sysMenuMapper.selectAll().stream().filter(m -> m.getStatus() != null && m.getStatus() == 1).toList()
                : roleIds.isEmpty()
                ? List.of()
                : sysMenuMapper.selectByRoleIds(roleIds).stream().filter(m -> m.getStatus() != null && m.getStatus() == 1).toList();

        return LoginResponse.of(
                "",
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                session.isAdmin(),
                session.getRoleCodes(),
                menus.stream().map(this::toLoginMenu).toList()
        );
    }

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }

    private LoginMenuItem toLoginMenu(SysMenuPO menuPO) {
        return LoginMenuItem.of(menuPO.getId(), menuPO.getParentId(), menuPO.getMenuName(), menuPO.getMenuPath(), menuPO.getIcon(), menuPO.getSortNo());
    }
}
