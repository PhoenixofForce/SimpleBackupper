package dev.phoenixofforce.backupper.archive.writer;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipArchiver implements Archiver {
    private final ZipOutputStream zipOut;

    public ZipArchiver(OutputStream outputStream) {
        this.zipOut = new ZipOutputStream(outputStream);
    }

    @Override
    public void addFile(File rootDir, File file) throws IOException {
        String entryName = rootDir.toPath().relativize(file.toPath()).toString().replace(File.separatorChar, '/');

        if (file.isDirectory()) {
            if (!entryName.endsWith("/")) entryName += "/";
            zipOut.putNextEntry(new ZipEntry(entryName));
            zipOut.closeEntry();

            File[] children = file.listFiles();
            if(children == null) children = new File[]{};

            for (File child : children) {
                addFile(rootDir, child);
            }
            return;
        }

        zipOut.putNextEntry(new ZipEntry(entryName));
        try(FileInputStream fis = new FileInputStream(file)) {
            fis.transferTo(zipOut);
        } catch (FileNotFoundException ignored) {}
        zipOut.closeEntry();
    }

    @Override
    public void close() throws IOException {
        zipOut.close();
    }
}