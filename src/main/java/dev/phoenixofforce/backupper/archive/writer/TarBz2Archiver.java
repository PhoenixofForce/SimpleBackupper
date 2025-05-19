package dev.phoenixofforce.backupper.archive.writer;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.*;
import java.util.Objects;

public class TarBz2Archiver implements Archiver {

    private final TarArchiveOutputStream tarOut;

    public TarBz2Archiver(OutputStream os){
        try {
            BZip2CompressorOutputStream bzip2Out = new BZip2CompressorOutputStream(os);
            this.tarOut = new TarArchiveOutputStream(bzip2Out);
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