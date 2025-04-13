package com.example.orderManagement.job;

import com.example.orderManagement.configuration.OrdersConfigurationProps;
import com.example.orderManagement.tasklet.FileArchiveTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Component
public class OrderManagementJobLauncher {

    public static final String BATCH_FILE_JOB_NAME = "processOrderFiles";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderManagementJobLauncher.class);

    @Autowired
    @Qualifier(BATCH_FILE_JOB_NAME)
    private Job processOrderFiles;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private OrdersConfigurationProps props;

    public JobExecution launchXmlFileToDatabaseJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException,
            IOException {
        LOGGER.info("Checking if job is already running");
        Set<JobExecution> runningJobs = jobExplorer.findRunningJobExecutions(BATCH_FILE_JOB_NAME);
        if (CollectionUtils.isEmpty(runningJobs)) {
            LOGGER.info("No existing jobs, starting {}} job", BATCH_FILE_JOB_NAME);
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("file:" + props.getInputFolder() + "/*.csv");
            String resourcesString = FileArchiveTasklet.resourcesToFiles(resources);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .addString("jobName", BATCH_FILE_JOB_NAME)
                    .addString("files", resourcesString)
                    .toJobParameters();
            JobExecution jobExecution = jobLauncher.run(processOrderFiles, jobParameters);

            LOGGER.info("Stopped {}} job", BATCH_FILE_JOB_NAME);
            return jobExecution;
        } else {
            List runningJobIds = runningJobs.stream().map(j -> j.getJobId()).collect(toList());
            LOGGER.error("Jobs are already running {}", runningJobs.stream().map(j -> j.getJobId()).collect(toList()));
            throw new IllegalStateException(String.format("There are already running jobs %s", runningJobIds));
        }
    }
}
