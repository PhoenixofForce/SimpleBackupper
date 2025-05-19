# Backupper
A simple Application to back up folders. The Application supports `tar.bz2`, `tar.xz`, `tar`, `tgz` and `zip` files.

## Installation
Download the latest jar and yml Files from the release tab.

## Configuration
In the yml File you've downloaded, you can configure how you want your folders to be backed up.

Here are some example configurations:

```yml
backup:
  location: ./defaultConfig
  folders:
    - path: ./src
      cron: "0 0 0 * * *" # each day at 00:00:00
      fileFormat: tgz
      overwriteExisting: true # each daily/manual backup will overwrite the weekly/monthly/yearly backups
      retention:    # 0 - disable deleting, -1 - disable creating backups
        days: 7     # keep the last 7 daily backups
        weeks: 4    # last 4 weekly backups
        months: 6   # last 6 monthly backups
        years: 1    # last yearly backup
        
    - path: ./onlyDailies
      cron: "0 0 0 * * *"
      fileFormat: zip
      retention:
        days: 0     # keep all the daily backups
        weeks: -1   # disable weekly backups
        months: -1  # disable monthly backups
        years: -1   # disable yearly backups
```

## Running
Then with the following command you can instantly perform a backup. If you have cron jobs configured they will be started automatically, and you can keep the task running. If you dont have a cron job configured, backup will run once and you can manage the continuity yourself.

```bash
java -jar .\backupper-0.0.1-SNAPSHOT.jar --spring-config.location=file:./applcation.yml
```