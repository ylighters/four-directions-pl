package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.SysUserPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Insert("""
            INSERT INTO sys_user (username, password, display_name, status)
            VALUES (#{username}, #{password}, #{displayName}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUserPO user);

    @Update("""
            UPDATE sys_user
            SET display_name = #{displayName},
                status = #{status}
            WHERE id = #{id}
            """)
    int update(SysUserPO user);

    @Update("""
            UPDATE sys_user
            SET password = #{password}
            WHERE id = #{id}
            """)
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("""
            SELECT id, username, password, display_name, status, created_at, updated_at
            FROM sys_user
            WHERE id = #{id}
            LIMIT 1
            """)
    SysUserPO selectById(@Param("id") Long id);

    @Select("""
            SELECT id, username, password, display_name, status, created_at, updated_at
            FROM sys_user
            WHERE username = #{username}
            LIMIT 1
            """)
    SysUserPO selectByUsername(@Param("username") String username);

    @Select("""
            SELECT id, username, password, display_name, status, created_at, updated_at
            FROM sys_user
            ORDER BY id DESC
            """)
    List<SysUserPO> selectAll();
}
