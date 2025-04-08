package com.kusa.jobs;

import com.kusa.service.GDriveService;
import java.time.LocalDateTime;

/**
 * Calls the Drive Service and downloads media to
 * local storage.
 *
 * 1:1 mapping between drive file path and local drive path in config.
 */
public class DownloadFromDrive implements Runnable {

  private GDriveService gds;
  private int min;
  private int max;

  public DownloadFromDrive(GDriveService gds) {
    this(gds, 0, 0);
  }

  public DownloadFromDrive(
    GDriveService gds,
    int hourRangeMin,
    int hourRangeMax
  ) {
    this.gds = gds;
    this.min = hourRangeMin;
    this.max = hourRangeMax;
  }

  @Override
  public void run() {
    //wont download any files if called between min(inclusive) - max(exclusive)
    int hour = LocalDateTime.now().getHour();
    if (hour >= min && hour < max) {
      log(
        String.format(
          "it is currently the %d hour, so we will not use drive service.",
          hour
        )
      );
      return;
    }
    //calls to is valid will
    //attempt to revalidate the drive service.
    if (gds.isValid()) gds.downloadMedia();
    else log("gds is invalid.");
  }

  private void log(String message) {
    System.out.println("[DownloadFromDrive] " + message);
  }
}
