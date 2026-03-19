package com.example.payment.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface PayOrderStatusLogMapper {

    @Insert("""
            INSERT INTO pay_order_status_log (
              order_no, merchant_no, from_status, to_status, event_type, reason, operator, event_time
            ) VALUES (
              #{orderNo}, #{merchantNo}, #{fromStatus}, #{toStatus}, #{eventType}, #{reason}, #{operator}, #{eventTime}
            )
            """)
    int insert(@Param("orderNo") String orderNo,
               @Param("merchantNo") String merchantNo,
               @Param("fromStatus") String fromStatus,
               @Param("toStatus") String toStatus,
               @Param("eventType") String eventType,
               @Param("reason") String reason,
               @Param("operator") String operator,
               @Param("eventTime") LocalDateTime eventTime);
}

