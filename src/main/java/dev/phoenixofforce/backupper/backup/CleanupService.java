package dev.phoenixofforce.backupper.backup;

import dev.phoenixofforce.backupper.backup.task.BackupConfiguration;
import dev.phoenixofforce.backupper.backup.task.BackupTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CleanupService {

    private final BackupConfiguration configuration;

    public void runCleanup(BackupTask task) {
        for(TimeFrame timeFrame: TimeFrame.values()) {
            this.runGenericCleanup(task, timeFrame.getFolderName(), timeFrame.getRetention(task.getRetention()));
        }
    }

    private void runGenericCleanup(BackupTask task, String subFolder, int maxFiles) {
        Path backupStorage = Path.of(configuration.getLocation()).resolve(task.getBackupName()).resolve(subFolder);
        List<File> backups = getBackups(backupStorage);

        boolean isRetentionFilterEnabled = maxFiles > 0;
        while(backups.size() > 1 && (backups.size() > maxFiles && isRetentionFilterEnabled)) {
            File fileToRemove = backups.removeFirst();
            boolean wasRemoved = fileToRemove.delete();
            if(wasRemoved) log.info(" - deleted {}", fileToRemove.getName());
            else log.error(" - could not delete {}", fileToRemove.getName());
        }
    }

    private List<File> getBackups(Path backupStorage) {
        File[] backups = backupStorage.toFile().listFiles();
        if(backups == null) return List.of();
        return new ArrayList<>(Arrays.stream(backups)
                .sorted(Comparator.comparing(File::getName))
                .toList());
    }

}
