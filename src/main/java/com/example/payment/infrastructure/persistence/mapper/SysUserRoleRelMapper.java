package com.example.payment.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserRoleRelMapper {

    @Delete("DELETE FROM sys_user_role_rel WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    @Insert("""
            <script>
            INSERT INTO sys_user_role_rel (user_id, role_id)
            VALUES
            <foreach collection="roleIds" item="roleId" separator=",">
              (#{userId}, #{roleId})
            </foreach>
            </script>
            """)
    int batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    @Select("""
            SELECT role_id
            FROM sys_user_role_rel
            WHERE user_id = #{userId}
            ORDER BY role_id ASC
            """)
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
