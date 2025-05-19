package dev.phoenixofforce.backupper.archive;

import dev.phoenixofforce.backupper.archive.writer.Archiver;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.OutputStream;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class Archive {

    private final String filetype;
    @Getter(AccessLevel.NONE)
    private final Function<OutputStream, Archiver> archiver;

    public Archiver getWriter(OutputStream stream) {
        return archiver.apply(stream);
    }
}
