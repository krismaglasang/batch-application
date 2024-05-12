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
public class FlowSecondConfiguration {

    @Autowired
    private final BatchConfiguration batchConfiguration;

    public FlowSecondConfiguration(BatchConfiguration batchConfiguration) {
        this.batchConfiguration = batchConfiguration;
    }

    @Bean
    public Step startingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("startingStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("This is the starting step");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job customJob2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return new JobBuilder("job1", jobRepository)
//                .start(step1(jobRepository, transactionManager))
//                .next(step2(jobRepository, transactionManager))
//                .next(step3(jobRepository, transactionManager))
//                .build();

        //Alternative to above
//        return new JobBuilder("job1", jobRepository)
//                .start(step1(jobRepository, transactionManager))
//                .on("COMPLETED").to(step2(jobRepository, transactionManager))
//                .from(step2(jobRepository, transactionManager)).on("COMPLETED").to(step3(jobRepository,transactionManager))
//                .from(step3(jobRepository, transactionManager))
//                .end()
//                .build();

        // instead of the jobbuilder calling the steps directly, wen ca pass the steps to a flow and then
        // the jobbuilder calls the flow
        return new JobBuilder("customJob2", jobRepository)
                .start(startingStep(jobRepository, transactionManager))
                .on("COMPLETED")
                .to(batchConfiguration.flow1(jobRepository, transactionManager))
                .end()
                .build();
    }
}
