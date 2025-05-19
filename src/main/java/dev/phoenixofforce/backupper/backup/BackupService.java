package dev.phoenixofforce.backupper.backup;

import dev.phoenixofforce.backupper.backup.task.BackupConfiguration;
import dev.phoenixofforce.backupper.backup.task.BackupTask;
import dev.phoenixofforce.backupper.archive.ArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupConfiguration configuration;
    private final CleanupService cleanupService;
    private final ArchiveService archiveService;

    public void createBackups(BackupTask task) {
        log.info("Creating backups for {}", task.getPath());

        Path backupStorage = Path.of(configuration.getLocation()).resolve(task.getBackupName());
        Path sourceFile = archiveService.archiveDirectory(task.getPath(), backupStorage, "latest", task.getFileFormat(), true);

        for(TimeFrame timeFrame: TimeFrame.values()) {
            if(timeFrame.getRetention(task.getRetention()) < 0) {
                continue;
            }
            moveFile(sourceFile, timeFrame.getDateFormat(), timeFrame.getFolderName(), task.overwriteExisting());
        }

        this.cleanupService.runCleanup(task);
    }

    private void moveFile(Path sourceFile, String dateFormat, String subFolder, boolean overwrite) {
        if(sourceFile == null) return;

        String filename = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateFormat));
        Path target = Path.of(sourceFile.toString().replace("latest", subFolder + File.separator + filename));

        try {
            if(target.getParent() != null) {
                Files.createDirectories(target.getParent());
            }

            if(overwrite) {
                Files.copy(sourceFile, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.copy(sourceFile, target);
            }
            log.info(" + successfully moved to {}", target);
        } catch (IOException e) {
            if(!(e instanceof FileAlreadyExistsException)) e.printStackTrace();
        }
    }



}
