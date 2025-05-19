package dev.phoenixofforce.backupper.backup.task;

import lombok.Data;

@Data
public class Retention {
    private int days = 7;
    private int weeks = 4;
    private int months = 6;
    private int years = 1;
}
