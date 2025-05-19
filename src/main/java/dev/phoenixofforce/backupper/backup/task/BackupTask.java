package dev.phoenixofforce.backupper.backup.task;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.file.Paths;

@Data
public class BackupTask {

    private String path;
    private String cron;
    private String fileFormat;
    private Retention retention;
    @Accessors(fluent = true)
    private boolean overwriteExisting = false;

    public String getBackupName() {
        String path = this.path;
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return Paths.get(path).getFileName().toString();
    }

}
