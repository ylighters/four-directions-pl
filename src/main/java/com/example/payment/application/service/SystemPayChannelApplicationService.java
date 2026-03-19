package com.example.payment.application.service;

import com.example.payment.infrastructure.persistence.mapper.PayChannelMapper;
import com.example.payment.infrastructure.persistence.po.PayChannelPO;
import com.example.payment.interfaces.rest.system.dto.PayChannelUpsertRequest;
import com.example.payment.interfaces.rest.system.dto.PayChannelView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemPayChannelApplicationService {

    private final PayChannelMapper payChannelMapper;

    public SystemPayChannelApplicationService(PayChannelMapper payChannelMapper) {
        this.payChannelMapper = payChannelMapper;
    }

    public List<PayChannelView> list() {
        return payChannelMapper.selectAll().stream().map(this::toView).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public PayChannelView create(PayChannelUpsertRequest request) {
        PayChannelPO po = new PayChannelPO();
        po.setChannelCode(request.getChannelCode());
        po.setChannelName(request.getChannelName());
        po.setChannelType(request.getChannelType());
        po.setMchId(request.getMchId());
        po.setApiConfig(request.getApiConfig());
        po.setFeeRate(request.getFeeRate());
        po.setStatus(request.getStatus());
        payChannelMapper.insert(po);
        return toView(payChannelMapper.selectById(po.getId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public PayChannelView update(Long id, PayChannelUpsertRequest request) {
        PayChannelPO po = new PayChannelPO();
        po.setId(id);
        po.setChannelCode(request.getChannelCode());
        po.setChannelName(request.getChannelName());
        po.setChannelType(request.getChannelType());
        po.setMchId(request.getMchId());
        po.setApiConfig(request.getApiConfig());
        po.setFeeRate(request.getFeeRate());
        po.setStatus(request.getStatus());
        payChannelMapper.update(po);
        return toView(payChannelMapper.selectById(id));
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        payChannelMapper.deleteById(id);
    }

    private PayChannelView toView(PayChannelPO po) {
        return PayChannelView.of(
                po.getId(),
                po.getChannelCode(),
                po.getChannelName(),
                po.getChannelType(),
                po.getMchId(),
                po.getApiConfig(),
                po.getFeeRate(),
                po.getStatus()
        );
    }
}