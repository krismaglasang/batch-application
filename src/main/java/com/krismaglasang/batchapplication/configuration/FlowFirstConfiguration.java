package com.krismaglasang.batchapplication.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FlowFirstConfiguration {

    @Autowired
    private final BatchConfiguration batchConfiguration;

    public FlowFirstConfiguration(BatchConfiguration batchConfiguration) {
        this.batchConfiguration = batchConfiguration;
    }

    @Bean
    public Step endingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("endingStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("This is the ending step");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job flowFirstJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("flowFirstJob", jobRepository)
                .start(batchConfiguration.flow1(jobRepository, transactionManager))
                .next(endingStep(jobRepository, transactionManager))
                .end()
                .build();
    }
}
