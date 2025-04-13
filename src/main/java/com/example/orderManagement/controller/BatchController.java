package com.example.orderManagement.controller;

import com.example.orderManagement.configuration.OrdersConfigurationProps;
import com.example.orderManagement.job.OrderManagementJobLauncher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(BaseController.BASE_PATH + "/batches")
public class BatchController {

    @Autowired
    private OrderManagementJobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private OrdersConfigurationProps props;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String startNewBatch() throws Exception {
        JobExecution jobExecution = jobLauncher.launchXmlFileToDatabaseJob();
        final List<Throwable> failedExps = jobExecution.getAllFailureExceptions();
        String status = "Job Id - " + jobExecution.getJobId();
        if (CollectionUtils.isEmpty(failedExps)) {
            status += " failed with the errors -> " + failedExps;
            throw new Exception(status);
        } else {
            status += " started successfully.";
        }

        return status;
    }

    @RequestMapping(value = "/{batch_id}", method = RequestMethod.GET)
    public ResponseEntity<Object> retrieveBatchInfo(@PathVariable("batch_id") String batchId) throws Exception {
        JobExecution jobExecution = jobExplorer.getJobExecution(getJobBatchId(batchId));
        if (jobExecution != null) {
            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body(objectMapper.writeValueAsString(Arrays.asList(jobExecution.getJobInstance(), jobExecution.getJobParameters(), jobExecution.getStatus(), jobExecution.getCreateTime(), jobExecution.getEndTime())));

        } else {
            return ResponseEntity.status(404).body("Job not found");
        }
    }

    public Long getJobBatchId(String batchId) throws NoSuchJobException {
        return "latest".equals(batchId) ? jobExplorer.getJobInstanceCount(OrderManagementJobLauncher.BATCH_FILE_JOB_NAME) : Long.valueOf(batchId);
    }
}
