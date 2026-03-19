package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.OutboxMessagePO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OutboxMapper {

    @Insert("""
            INSERT INTO mq_outbox (
              msg_id, topic, biz_key, msg_body, send_status, retry_count, next_retry_time
            ) VALUES (
              #{msgId}, #{topic}, #{bizKey}, #{msgBody}, #{sendStatus}, #{retryCount}, #{nextRetryTime}
            )
            """)
    int insert(OutboxMessagePO messageDO);

    @Select("""
            SELECT id, msg_id, topic, biz_key, msg_body, send_status, retry_count, next_retry_time, created_at, updated_at
            FROM mq_outbox
            WHERE send_status IN ('INIT', 'RETRY')
              AND next_retry_time <= #{now}
            ORDER BY id ASC
            LIMIT #{limit}
            """)
    List<OutboxMessagePO> scanDispatchable(@Param("now") LocalDateTime now, @Param("limit") int limit);

    @Update("""
            UPDATE mq_outbox
            SET send_status = 'SENDING', updated_at = NOW(3)
            WHERE id = #{id}
              AND send_status IN ('INIT', 'RETRY')
            """)
    int markSending(@Param("id") Long id);

    @Update("""
            UPDATE mq_outbox
            SET send_status = 'SENT', updated_at = NOW(3)
            WHERE id = #{id}
              AND send_status = 'SENDING'
            """)
    int markSent(@Param("id") Long id);

    @Update("""
            UPDATE mq_outbox
            SET send_status = 'RETRY',
                retry_count = retry_count + 1,
                next_retry_time = #{nextRetryTime},
                updated_at = NOW(3)
            WHERE id = #{id}
              AND send_status = 'SENDING'
            """)
    int markRetry(@Param("id") Long id, @Param("nextRetryTime") LocalDateTime nextRetryTime);
}

