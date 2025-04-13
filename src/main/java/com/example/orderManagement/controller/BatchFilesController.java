package com.example.orderManagement.controller;

import com.example.orderManagement.configuration.OrdersConfigurationProps;
import com.example.orderManagement.job.OrderManagementJobLauncher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(BaseController.BASE_PATH + "/files")
@Slf4j
public class BatchFilesController {

    @Autowired
    private OrdersConfigurationProps props;

    @Autowired
    private OrderManagementJobLauncher jobLauncher;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> fileUpload(@RequestParam("files") List<MultipartFile> files,
                                             @RequestParam(value = "triggerBatch", required = false) Boolean triggerBatch) throws Exception {
        if (files == null || CollectionUtils.isEmpty(files))
            return ResponseEntity.badRequest().body("No files provided");

        File targetDir = props.getInputFolder().toString().contains(":") ? new File(StringUtils.substringAfter(props.getInputFolder().toString(), ":")) : props.getInputFolder();

        for (MultipartFile file : files) {
            String targetName = file.getOriginalFilename();
            try {
                file.transferTo(new File(targetDir, targetName));
            } catch (IOException | RuntimeException rex) {
                log.error("Unable to save file {} to {}", targetName, targetDir);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        String.format("Unable to save file %s to %s", targetName, targetDir)
                );
            }
        }
        if (Boolean.TRUE.equals(triggerBatch)) {
            JobExecution jobExecution = jobLauncher.launchXmlFileToDatabaseJob();
            return ResponseEntity.ok(String.format("Started batch %s successfully.", jobExecution.getJobId()));
        }
        return ResponseEntity.accepted().build();
    }
}
