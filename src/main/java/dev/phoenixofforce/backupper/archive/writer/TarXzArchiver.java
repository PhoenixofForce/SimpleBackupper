package dev.phoenixofforce.backupper.archive.writer;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;

import java.io.*;
import java.util.Objects;

public class TarXzArchiver implements Archiver {
    private final TarArchiveOutputStream tarOut;

    public TarXzArchiver(OutputStream os) {
        try {
            XZCompressorOutputStream xzOut = new XZCompressorOutputStream(os);
            this.tarOut = new TarArchiveOutputStream(xzOut);
            this.tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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