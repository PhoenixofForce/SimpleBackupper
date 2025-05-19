package dev.phoenixofforce.backupper.backup.task;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "backup")
public class BackupConfiguration {

    private String location;

    @Name("folders")
    private List<BackupTask> tasks;

}
