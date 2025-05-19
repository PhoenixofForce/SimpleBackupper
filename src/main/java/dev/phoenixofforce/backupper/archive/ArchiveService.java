package dev.phoenixofforce.backupper.archive;

import dev.phoenixofforce.backupper.archive.writer.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {

    public Path archiveDirectory(String directoryToBackup, Path backupStorageFolder, String filename, String fileExtension, boolean overwrite) {
        try {
            Archive archive = getArchive(fileExtension);
            Path backupFile = backupStorageFolder.resolve(filename + "." + archive.getFiletype());

            if(backupFile.toFile().exists() && !overwrite) {
                log.debug(" / backup already exists for {}", backupFile);
                return backupFile;
            }

            if(backupFile.getParent() != null) {
                Files.createDirectories(backupFile.getParent());
            }

            FileOutputStream fos = new FileOutputStream(backupFile.toString());
            Archiver writer = archive.getWriter(fos);
            File source = new File(directoryToBackup);
            writer.addFile(source, source);
            writer.close();

            log.info(" + successfully created backup for {}", backupFile);
            return  backupFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Archive getArchive(String fileformat) {
        return switch (fileformat.toLowerCase()) {
            case "tar.bz2" -> new Archive("tar.bz2", TarBz2Archiver::new);
            case "tar.xz" -> new Archive("tar.xt", TarXzArchiver::new);
            case "tar" -> new Archive("tar", TarArchiver::new);
            case "tgz", "tar.gz" -> new Archive("tgz", TarGzArchiver::new);
            default -> new Archive("zip", ZipArchiver::new);
        };
    }
}
