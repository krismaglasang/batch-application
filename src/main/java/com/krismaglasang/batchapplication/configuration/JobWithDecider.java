package com.krismaglasang.batchapplication.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobWithDecider {

    @Autowired
    private final BatchConfiguration batchConfiguration;

    public JobWithDecider(BatchConfiguration batchConfiguration) {
        this.batchConfiguration = batchConfiguration;
    }

    @Bean
    public JobExecutionDecider decider() {
        return new JobExecutionDecider() {
            private int counter = 1;

            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                if (counter % 2 == 0) {
                    return new FlowExecutionStatus("EVEN");
                }
                counter++;
                return new FlowExecutionStatus("ODD");
            }
        };
    }

    /**
     * Execution flow is as follows: Execute step1 first, then call decider(). If counter%2 is even, execute evenStep,
     * otherwise, oddStep. We can manipulate the counter in the decider() method if we want it to run oddStep first.
     * @param jobRepository
     * @param transactionManager
     * @return
     */
    @Bean
    public Job deciderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("deciderJob", jobRepository)
                .start(batchConfiguration.step1(jobRepository, transactionManager))
                .next(decider())
                .from(decider()).on("EVEN").to(batchConfiguration.evenStep(jobRepository, transactionManager))
                .from(decider()).on("ODD").to(batchConfiguration.oddStep(jobRepository, transactionManager))
                .end()
                .build();
    }
}
