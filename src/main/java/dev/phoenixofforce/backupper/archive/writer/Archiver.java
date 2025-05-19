package dev.phoenixofforce.backupper.archive.writer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public interface Archiver extends Closeable {
    void addFile(File rootDir, File file) throws IOException;
}