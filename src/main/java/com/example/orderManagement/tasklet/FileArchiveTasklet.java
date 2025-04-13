package com.example.orderManagement.tasklet;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.stream.Collectors;

@Setter
public class FileArchiveTasklet implements Tasklet, InitializingBean {

    private File inputFolder;
    private File archiveFolder;

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Resource[] resources = filesToResources(inputFolder, chunkContext.getStepContext().getJobParameters().get("files").toString());
        String prefix = "batch-" + chunkContext.getStepContext().getStepExecution().getJobExecution().getJobId() + "-";
        for (Resource r : resources) {
            File file = r.getFile();
            File target = new File(archiveFolder, prefix + file.getName());
            try {
                Path path = Files.move(file.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new UnexpectedJobExecutionException("Could not archive file " + file.getPath(), e);
            }
        }
        return RepeatStatus.FINISHED;
    }


    public void afterPropertiesSet() throws Exception {
        Assert.notNull(inputFolder != null && (!inputFolder.exists() || inputFolder.isDirectory()), "input folder must be set and exist");
        Assert.isTrue(archiveFolder != null && (!archiveFolder.exists() || archiveFolder.isDirectory()), "archive folder must be set and exist");
    }

    public static final Resource[] filesToResources(File folder, String files) {
        String[] filesArray = StringUtils.split(files, "\n");
        return Arrays.stream(filesArray).map(fileName -> new FileSystemResource(new File(StringUtils.removeStart(folder.getPath(), "file:"), fileName))).toArray(Resource[]::new);
    }

    public static final Resource fileToResource(File folder, String fileName) {
        return new FileSystemResource(new File(StringUtils.removeStart(folder.getPath(), "file:"), fileName));
    }

    public static final String resourcesToFiles(Resource[] resources) {
        return Arrays.stream(resources).map(r -> {
            try {
                return r.getFile().getName();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.joining("\n"));
    }
}
