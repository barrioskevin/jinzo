package com.kusa;


import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;

import java.util.TimerTask;
import java.util.List;
import java.util.Set;


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
    Set<String> localVideos = LocalService.getVideoMRLS();
    for(File video : videos)
    {
      String p = LocalService.videosPath + video.getName();
      if(!localVideos.contains(p))
        gds.downloadFile(video.getId(), "videos/");
    }
  }
}
