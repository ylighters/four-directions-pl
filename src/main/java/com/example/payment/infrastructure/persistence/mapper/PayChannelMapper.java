package com.example.payment.infrastructure.persistence.mapper;

import com.example.payment.infrastructure.persistence.po.PayChannelPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PayChannelMapper {

    @Insert("""
            INSERT INTO pay_channel (channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status)
            VALUES (#{channelCode}, #{channelName}, #{channelType}, #{mchId}, CAST(#{apiConfig} AS JSON), #{feeRate}, #{status})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PayChannelPO channel);

    @Update("""
            UPDATE pay_channel
            SET channel_code = #{channelCode},
                channel_name = #{channelName},
                channel_type = #{channelType},
                mch_id = #{mchId},
                api_config = CAST(#{apiConfig} AS JSON),
                fee_rate = #{feeRate},
                status = #{status}
            WHERE id = #{id}
            """)
    int update(PayChannelPO channel);

    @Delete("DELETE FROM pay_channel WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("""
            SELECT id, channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status, created_at, updated_at
            FROM pay_channel
            WHERE id = #{id}
            LIMIT 1
            """)
    PayChannelPO selectById(@Param("id") Long id);

    @Select("""
            SELECT id, channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status, created_at, updated_at
            FROM pay_channel
            ORDER BY id DESC
            """)
    java.util.List<PayChannelPO> selectAll();

    @Select("""
            SELECT id, channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status, created_at, updated_at
            FROM pay_channel
            WHERE channel_code = #{channelCode}
              AND status = 1
            LIMIT 1
            """)
    PayChannelPO selectActiveByChannelCode(@Param("channelCode") String channelCode);

    @Select("""
            SELECT id, channel_code, channel_name, channel_type, mch_id, api_config, fee_rate, status, created_at, updated_at
            FROM pay_channel
            WHERE channel_type = #{channelType}
              AND status = 1
            ORDER BY id ASC
            """)
    java.util.List<PayChannelPO> selectActiveByChannelType(@Param("channelType") String channelType);
}
