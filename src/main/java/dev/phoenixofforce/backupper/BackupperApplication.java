package dev.phoenixofforce.backupper;

import dev.phoenixofforce.backupper.backup.BackupService;
import dev.phoenixofforce.backupper.backup.task.BackupConfiguration;
import dev.phoenixofforce.backupper.backup.task.BackupTask;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class BackupperApplication {

	private final BackupConfiguration configuration;
	private final BackupService backupService;
	private final TaskScheduler scheduler;

	public static void main(String[] args) {
		new SpringApplicationBuilder(BackupperApplication.class)
				.headless(false)
				.run(args);
	}

	@PostConstruct
	public void run() {
		log.info("Found backup location: {}", configuration.getLocation());

		for(BackupTask task: configuration.getTasks()) {
			this.backupService.createBackups(task);

			if(task.getCron() != null) {
				Runnable runnable = () -> {
					log.info("Cron triggered {}", task.getCron());
					this.backupService.createBackups(task);
				};
				scheduler.schedule(runnable, new CronTrigger(task.getCron()));
			}
		}
	}



}
