package com.kusa.jobs;

import com.kusa.service.GDriveService;
import java.time.LocalDateTime;

/**
 * Calls the Drive Service and downloads media to
 * local storage. 
 *
 * 1:1 mapping between drive file path and local drive path in config.
 */
public class DownloadFromDrive implements Runnable
{
  private GDriveService gds;
  public DownloadFromDrive(GDriveService gds) { this.gds = gds; }
  @Override
  public void run()
  {
    //playlist or video player won't be bothered between 12am - 8am
    int hour = LocalDateTime.now().getHour(); 
    if(hour >= 0 && hour < 8)
    {
      System.out.println("IT IS CURRENTLY THE " + hour + " HOUR SO WE WILL NOT MODIFY PLAYLIST OR USE SERVICES.");
      return;
    }
    //calls to is valid will
    //attempt to revalidate the drive service.
    if(gds.isValid())
      gds.downloadMedia();
  }
}
