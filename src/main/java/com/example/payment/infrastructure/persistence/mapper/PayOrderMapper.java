package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.PayOrderPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 支付订单主表 Mapper（逻辑表：pay_order，底层由 ShardingSphere 路由到分表）。
 */
@Mapper
public interface PayOrderMapper {

    /**
     * 新增订单。
     */
    @Insert("""
            INSERT INTO pay_order (
              order_no, merchant_no, app_id, merchant_order_no, pay_scene, pay_way,
              channel_code, channel_order_no, amount, currency, status, subject,
              notify_url, return_url, version
            ) VALUES (
              #{orderNo}, #{merchantNo}, #{appId}, #{merchantOrderNo}, #{payScene}, #{payWay},
              #{channelCode}, #{channelOrderNo}, #{amount}, #{currency}, #{status}, #{subject},
              #{notifyUrl}, #{returnUrl}, #{version}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PayOrderPO orderDO);

    /**
     * 按订单号 + 商户号查询订单。
     */
    @Select("""
            SELECT id, order_no, merchant_no, app_id, merchant_order_no, pay_scene, pay_way,
                   channel_code, channel_order_no, amount, currency, status, subject,
                   notify_url, return_url, version, created_at, updated_at
            FROM pay_order
            WHERE order_no = #{orderNo}
              AND merchant_no = #{merchantNo}
            LIMIT 1
            """)
    PayOrderPO selectByOrderNo(@Param("orderNo") String orderNo, @Param("merchantNo") String merchantNo);

    /**
     * 基于版本号的乐观锁更新。
     */
    @Update("""
            UPDATE pay_order
            SET status = #{status},
                channel_code = #{channelCode},
                channel_order_no = #{channelOrderNo},
                version = version + 1
            WHERE order_no = #{orderNo}
              AND merchant_no = #{merchantNo}
              AND version = #{version}
            """)
    int updateStatusWithVersion(PayOrderPO orderDO);

    /**
     * 按商户订单号查询（幂等校验使用）。
     */
    @Select("""
            SELECT id, order_no, merchant_no, app_id, merchant_order_no, pay_scene, pay_way,
                   channel_code, channel_order_no, amount, currency, status, subject,
                   notify_url, return_url, version, created_at, updated_at
            FROM pay_order
            WHERE merchant_no = #{merchantNo}
              AND app_id = #{appId}
              AND merchant_order_no = #{merchantOrderNo}
            LIMIT 1
            """)
    PayOrderPO selectByMerchantOrder(@Param("merchantNo") String merchantNo,
                                     @Param("appId") String appId,
                                     @Param("merchantOrderNo") String merchantOrderNo);

    /**
     * 按商户分页查询订单。
     */
    @Select("""
            SELECT id, order_no, merchant_no, app_id, merchant_order_no, pay_scene, pay_way,
                   channel_code, channel_order_no, amount, currency, status, subject,
                   notify_url, return_url, version, created_at, updated_at
            FROM pay_order
            WHERE merchant_no = #{merchantNo}
              AND app_id = #{appId}
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            """)
    java.util.List<PayOrderPO> selectPageByMerchant(@Param("merchantNo") String merchantNo,
                                                    @Param("appId") String appId,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);
}

