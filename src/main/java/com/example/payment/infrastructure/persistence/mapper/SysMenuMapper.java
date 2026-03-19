package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.SysMenuPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysMenuMapper {

    @Insert("""
            INSERT INTO sys_menu (parent_id, menu_name, menu_path, icon, sort_no, status)
            VALUES (#{parentId}, #{menuName}, #{menuPath}, #{icon}, #{sortNo}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysMenuPO menu);

    @Update("""
            UPDATE sys_menu
            SET parent_id = #{parentId},
                menu_name = #{menuName},
                menu_path = #{menuPath},
                icon = #{icon},
                sort_no = #{sortNo},
                status = #{status}
            WHERE id = #{id}
            """)
    int update(SysMenuPO menu);

    @Delete("DELETE FROM sys_menu WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("""
            SELECT id, parent_id, menu_name, menu_path, icon, sort_no, status, created_at, updated_at
            FROM sys_menu
            WHERE id = #{id}
            LIMIT 1
            """)
    SysMenuPO selectById(@Param("id") Long id);

    @Select("""
            SELECT id, parent_id, menu_name, menu_path, icon, sort_no, status, created_at, updated_at
            FROM sys_menu
            ORDER BY sort_no ASC, id ASC
            """)
    List<SysMenuPO> selectAll();

    @Select("""
            <script>
            SELECT m.id, m.parent_id, m.menu_name, m.menu_path, m.icon, m.sort_no, m.status, m.created_at, m.updated_at
            FROM sys_menu m
            JOIN sys_role_menu_rel rm ON rm.menu_id = m.id
            WHERE rm.role_id IN
            <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
              #{roleId}
            </foreach>
            GROUP BY m.id, m.parent_id, m.menu_name, m.menu_path, m.icon, m.sort_no, m.status, m.created_at, m.updated_at
            ORDER BY m.sort_no ASC, m.id ASC
            </script>
            """)
    List<SysMenuPO> selectByRoleIds(@Param("roleIds") List<Long> roleIds);
}
