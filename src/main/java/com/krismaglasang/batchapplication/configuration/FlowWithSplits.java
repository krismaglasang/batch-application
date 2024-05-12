package com.krismaglasang.batchapplication.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlowWithSplits {

    @Autowired
    private final BatchConfiguration batchConfiguration;

    public FlowWithSplits(BatchConfiguration batchConfiguration) {
        this.batchConfiguration = batchConfiguration;
    }

    /**
     * The split() allows for running multiple flows concurrently
     * @param jobRepository
     * @param transactionManager
     * @return
     */
    @Bean
    public Job customJob3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("customJob3", jobRepository)
                .start(batchConfiguration.flow1(jobRepository, transactionManager))
                .split(new SimpleAsyncTaskExecutor()).add(batchConfiguration.flow2(jobRepository, transactionManager))
                .end()
                .build();
    }
}
