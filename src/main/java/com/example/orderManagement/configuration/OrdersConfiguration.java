package com.example.orderManagement.configuration;

import com.example.orderManagement.job.OrderManagementJobLauncher;
import com.example.orderManagement.listener.OrderJobNotificationListener;
import com.example.orderManagement.processor.OrderInputProcessor;
import com.example.orderManagement.request.OrderTransaction;
import com.example.orderManagement.tasklet.FileArchiveTasklet;
import com.example.orderManagement.writer.OrderDBWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
@Slf4j
public class OrdersConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    OrdersConfigurationProps props;

    @Autowired
    private OrderJobNotificationListener listener;

    private String files;

    @Bean
    @StepScope
    public MultiResourceItemReader<OrderTransaction> multiFileReader(@Value("#{jobParameters['files']}") String files) throws IOException {
        this.files = files;
        MultiResourceItemReader<OrderTransaction> reader = new MultiResourceItemReader<>();
        // Get files from the directory
        ResourcePatternResolver patternResolver =
                new PathMatchingResourcePatternResolver();
        Resource[] inputResources = FileArchiveTasklet.filesToResources(props.getInputFolder(), files);

        reader.setResources(inputResources);
        reader.setSaveState(true);
        reader.setDelegate(csvItemReader()); // Delegate the internal reading to FlatFileResource
        return reader;
    }

    @Bean
    public FlatFileItemReader<OrderTransaction> csvItemReader() {
        FlatFileItemReader<OrderTransaction> reader = new FlatFileItemReader<>();
        //reader.setResource(new FileSystemResource("input-directory/sample.csv")); // Update with the file path
        reader.setLinesToSkip(1); // Skip the header row

        DefaultLineMapper<OrderTransaction> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("Transaction Time", "Customer ID", "Quatity", "Product Code"); // Map columns by name
        lineMapper.setLineTokenizer(tokenizer);

        BeanWrapperFieldSetMapper<OrderTransaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(OrderTransaction.class); // Map data to the POJO
        lineMapper.setFieldSetMapper(fieldSetMapper);

        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public OrderInputProcessor orderInputprocessor() {
        return new OrderInputProcessor();
    }

    @Bean
    public OrderDBWriter databaseWriter() {
        return new OrderDBWriter();
    }

    @Bean
    @Autowired
    public Step readOrderFiles(MultiResourceItemReader multiFileReader) throws Exception {
        return stepBuilderFactory.get("readOrderFiles")
                .<OrderTransaction, OrderTransaction>chunk(100)
                .reader(multiFileReader)
                .processor(orderInputprocessor())
                .writer(databaseWriter())
                .build();
    }

    @Bean
    Step archiveFiles() {
        FileArchiveTasklet task = new FileArchiveTasklet();
        task.setInputFolder(props.getInputFolder());
        task.setArchiveFolder(props.getArchiveFolder());
        return stepBuilderFactory.get("archiveFiles")
                .tasklet(task)
                .build();
    }

    // Job definition
    @Bean(name = OrderManagementJobLauncher.BATCH_FILE_JOB_NAME)
    Job processOrderFiles(JobBuilderFactory jobs, Step readOrderFiles, Step archiveFiles) throws Exception {
        return jobs.get(OrderManagementJobLauncher.BATCH_FILE_JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(readOrderFiles)
                .on("*").to(archiveFiles)
                .end()
                .build();
    }

}
