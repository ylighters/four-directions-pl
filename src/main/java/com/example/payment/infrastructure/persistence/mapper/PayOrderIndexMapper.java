package com.example.payment.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PayOrderIndexMapper {

    @Insert("""
            INSERT INTO pay_order_index (order_no, merchant_no, app_id)
            VALUES (#{orderNo}, #{merchantNo}, #{appId})
            """)
    int insert(@Param("orderNo") String orderNo,
               @Param("merchantNo") String merchantNo,
               @Param("appId") String appId);

    @Select("""
            SELECT merchant_no
            FROM pay_order_index
            WHERE order_no = #{orderNo}
            LIMIT 1
            """)
    String selectMerchantNoByOrderNo(@Param("orderNo") String orderNo);
}
