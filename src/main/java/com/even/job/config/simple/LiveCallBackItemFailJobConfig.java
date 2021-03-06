package com.even.job.config.simple;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.even.job.job.simple.LiveCallBackItemFailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiveCallBackItemFailJobConfig {

    @Autowired
    private ZookeeperRegistryCenter regCenter;

    @Autowired
    private JobEventConfiguration jobEventConfiguration;

    @Bean
    public LiveCallBackItemFailJob liveCallBackItemFailJob() {
        return new LiveCallBackItemFailJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler liveCallBackItemFailScheduler(final LiveCallBackItemFailJob liveCallBackItemFailJob, @Value("${live.callbackfail.cron}") final String cron, @Value("${live.callbackfail.shardingTotalCount}") final int shardingTotalCount,
                                           @Value("${live.callbackfail.shardingItemParameters}") final String shardingItemParameters) {
        return new SpringJobScheduler(liveCallBackItemFailJob, regCenter, getLiteJobConfiguration(liveCallBackItemFailJob.getClass(), cron, shardingTotalCount, shardingItemParameters), jobEventConfiguration);
    }

    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron, final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(JobCoreConfiguration.newBuilder(
                jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName())).overwrite(true).build();
    }
}
