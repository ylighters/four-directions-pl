package com.example.payment.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMenuRelMapper {

    @Delete("DELETE FROM sys_role_menu_rel WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_role_menu_rel WHERE menu_id = #{menuId}")
    int deleteByMenuId(@Param("menuId") Long menuId);

    @Insert("""
            <script>
            INSERT INTO sys_role_menu_rel (role_id, menu_id)
            VALUES
            <foreach collection="menuIds" item="menuId" separator=",">
              (#{roleId}, #{menuId})
            </foreach>
            </script>
            """)
    int batchInsert(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    @Select("""
            SELECT menu_id
            FROM sys_role_menu_rel
            WHERE role_id = #{roleId}
            ORDER BY menu_id ASC
            """)
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);
}
