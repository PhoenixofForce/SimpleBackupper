package dev.phoenixofforce.backupper.archive.writer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class TarGzArchiver implements Archiver {

    private final TarArchiveOutputStream tarOut;

    public TarGzArchiver(OutputStream os) {
        try {
            GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(os);
            this.tarOut = new TarArchiveOutputStream(gzipOut);
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
            if(children == null) children = new File[]{};

            for (File child : children) {
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