package dev.phoenixofforce.backupper.backup;

import dev.phoenixofforce.backupper.backup.task.Retention;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public enum TimeFrame {
    DAILY("yyyy-MM-dd", Retention::getDays),
    WEEKLY("yyyy-ww", Retention::getWeeks),
    MONTHLY("yyyy-MM", Retention::getMonths),
    YEARLY("yyyy", Retention::getYears);

    @Getter
    private final String dateFormat;
    private final Function<Retention, Integer> taskToRetention;

    public int getRetention(Retention retention) {
        return this.taskToRetention.apply(retention);
    }

    public String getFolderName() {
        return this.name().toLowerCase();
    }
}
