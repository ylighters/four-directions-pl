package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.PayOrderPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PayOrderMapper {

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
}

