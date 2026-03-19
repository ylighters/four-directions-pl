package com.example.payment.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 支付订单索引表 Mapper。
 * 用于通过 order_no 快速获取 merchant_no（分表路由键）。
 */
@Mapper
public interface PayOrderIndexMapper {

    /**
     * 写入订单索引。
     */
    @Insert("""
            INSERT INTO pay_order_index (order_no, merchant_no, app_id)
            VALUES (#{orderNo}, #{merchantNo}, #{appId})
            """)
    int insert(@Param("orderNo") String orderNo,
               @Param("merchantNo") String merchantNo,
               @Param("appId") String appId);

    /**
     * 根据订单号反查商户号。
     */
    @Select("""
            SELECT merchant_no
            FROM pay_order_index
            WHERE order_no = #{orderNo}
            LIMIT 1
            """)
    String selectMerchantNoByOrderNo(@Param("orderNo") String orderNo);
}
