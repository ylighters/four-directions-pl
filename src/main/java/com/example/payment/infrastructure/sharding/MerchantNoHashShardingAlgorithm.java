package com.example.payment.infrastructure.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;
import java.util.Objects;
import java.util.Properties;

public class MerchantNoHashShardingAlgorithm implements StandardShardingAlgorithm<String> {

    private Properties props = new Properties();

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        String merchantNo = shardingValue.getValue();
        int suffix = Math.floorMod(Objects.requireNonNull(merchantNo).hashCode(), 64);
        String targetSuffix = String.format("%02d", suffix);

        return availableTargetNames.stream()
                .filter(each -> each.endsWith("_" + targetSuffix))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No shard table found for merchantNo=" + merchantNo));
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
                                         RangeShardingValue<String> shardingValue) {
        return availableTargetNames;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }

    @Override
    public String getType() {
        return "CLASS_BASED";
    }

    @Override
    public Properties getProps() {
        return props;
    }
}

