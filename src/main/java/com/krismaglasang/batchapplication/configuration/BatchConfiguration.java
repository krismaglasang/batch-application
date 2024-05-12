package com.krismaglasang.batchapplication.configuration;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfiguration {

    @Bean
    public Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.printf(
                        "%s has been executed on thread %s%n",
                        chunkContext.getStepContext().getStepName(),
                        Thread.currentThread().getName());
                return null;
            }
        };
    }

    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step step2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step2", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step step3(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step3", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step step4(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step4", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step oddStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("oddStep", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step evenStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("evenStep", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Flow flow1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow1");
        flowBuilder.start(step1(jobRepository, transactionManager))
                .next(step2(jobRepository, transactionManager))
                .end();
        return flowBuilder.build();
    }

    @Bean
    public Flow flow2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow2");
        flowBuilder.start(step3(jobRepository, transactionManager))
                .next(step4(jobRepository, transactionManager))
                .end();
        return flowBuilder.build();
    }
}
