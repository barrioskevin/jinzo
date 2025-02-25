package com.kusa;

import java.util.TimerTask;
import java.util.List;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.kusa.service.GDriveService;


public class VideoDownloaderTask extends TimerTask {
  private GDriveService gds;
  public VideoDownloaderTask(GDriveService gds){ this.gds = gds; }

  /*
   * pull videos and download them.
   */
  @Override
  public void run()
  {
    System.out.println("VideoDownloader downloading vids...");
    List<File> videos = gds.getVideoFiles();
    for(File video : videos)
    {
      gds.downloadFile(video.getId(), "videos/");
    }
  }
}
