package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.SysRolePO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Insert("""
            INSERT INTO sys_role (role_code, role_name, status)
            VALUES (#{roleCode}, #{roleName}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysRolePO role);

    @Update("""
            UPDATE sys_role
            SET role_code = #{roleCode},
                role_name = #{roleName},
                status = #{status}
            WHERE id = #{id}
            """)
    int update(SysRolePO role);

    @Delete("DELETE FROM sys_role WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("""
            SELECT id, role_code, role_name, status, created_at, updated_at
            FROM sys_role
            WHERE id = #{id}
            LIMIT 1
            """)
    SysRolePO selectById(@Param("id") Long id);

    @Select("""
            SELECT id, role_code, role_name, status, created_at, updated_at
            FROM sys_role
            WHERE role_code = #{roleCode}
            LIMIT 1
            """)
    SysRolePO selectByRoleCode(@Param("roleCode") String roleCode);

    @Select("""
            SELECT id, role_code, role_name, status, created_at, updated_at
            FROM sys_role
            ORDER BY id DESC
            """)
    List<SysRolePO> selectAll();

    @Select("""
            SELECT r.id, r.role_code, r.role_name, r.status, r.created_at, r.updated_at
            FROM sys_role r
            JOIN sys_user_role_rel ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            ORDER BY r.id DESC
            """)
    List<SysRolePO> selectByUserId(@Param("userId") Long userId);
}
