package dev.phoenixofforce.backupper.archive.writer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.*;
import java.util.Objects;

public class TarArchiver implements Archiver {

    private final TarArchiveOutputStream tarOut;

    public TarArchiver(OutputStream os) {
        this.tarOut = new TarArchiveOutputStream(os);
        this.tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
    }

    @Override
    public void addFile(File rootDir, File file) throws IOException {
        String entryName = rootDir.toPath()
                .relativize(file.toPath())
                .toString()
                .replace(File.separatorChar, '/');

        TarArchiveEntry entry = new TarArchiveEntry(file, entryName);
        tarOut.putArchiveEntry(entry);

        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                fis.transferTo(tarOut);
            }
        }

        tarOut.closeArchiveEntry();

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : Objects.requireNonNullElse(children, new File[0])) {
                addFile(rootDir, child);
            }
        }
    }

    @Override
    public void close() throws IOException {
        tarOut.finish();
        tarOut.close();
    }
}