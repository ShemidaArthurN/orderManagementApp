package com.example.orderManagement.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@ConfigurationProperties(prefix = "orders")
@Data
@Slf4j
@Getter
@Setter
@Component
public class OrdersConfigurationProps {

    private File inputFolder;
    private File archiveFolder;
    private Boolean isCreateFolders = false;


    @PostConstruct
    public void dumpConfig() {
        if (Boolean.TRUE.equals(isCreateFolders)) {
            inputFolder.mkdirs();
            archiveFolder.mkdirs();
        }
        log.info("This bean {}: {}", toString());
    }

}
