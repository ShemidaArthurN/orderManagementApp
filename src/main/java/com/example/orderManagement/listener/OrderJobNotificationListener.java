package com.example.orderManagement.listener;

import com.example.orderManagement.controller.TransactionIdFilter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderJobNotificationListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        MDC.put(TransactionIdFilter.MDC_CORRELATION_ID, String.format("batch-%s", jobExecution.getId()));
    }


    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        } else {
            log.error("!!! JOB FAILED! with the Errors : {}", jobExecution.getExitStatus().getExitDescription(), jobExecution.getAllFailureExceptions());
        }
        MDC.remove(TransactionIdFilter.MDC_CORRELATION_ID);
    }
}
