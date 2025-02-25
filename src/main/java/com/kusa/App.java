package com.kusa;
import com.kusa.player.AppFrame;
import com.kusa.playlist.CircularQueuePlaylist;

import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;


/**
 * Main class.
 *
 * Responsible for launching the app and any services.
 *
 * we need to make sure that we do thing properly here.
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 * 
 * In this class we should just spin everything up.
 */
public class App 
{
  public static void main(String args[])
  {
    GDriveService gds = new GDriveService();

    //download photos.
    List<File> photos = gds.getPhotoFiles();
    Set<String> localPhotos = LocalService.getPhotoMRLS();
    Set<String> drivePhotos = new HashSet<>();
    for(File photo : photos)
    {
      String p = LocalService.photosPath + photo.getName();
      drivePhotos.add(p);
      if(!localPhotos.contains(p))
        gds.downloadFile(photo.getId(), "photos/");
    }
    //download videos.
    List<File> videos = gds.getVideoFiles();
    Set<String> localVideos = LocalService.getVideoMRLS();
    Set<String> driveVideos = new HashSet<>();
    for(File video : videos)
    {
      String p = LocalService.videosPath + video.getName();
      driveVideos.add(p);
      if(!localVideos.contains(p))
        gds.downloadFile(video.getId(), "videos/");
    }

    //create playlist.
    Set<String> intersectingVideos = new HashSet<>(localVideos);
    intersectingVideos.retainAll(driveVideos);
    CircularQueuePlaylist cqp = new CircularQueuePlaylist(new ArrayList<String>(intersectingVideos));

    //set up playlist sync task.
    Timer playlistTimer = new Timer();
    playlistTimer.schedule(new PlaylistDriveSyncTask(cqp, gds), 1000, 60000);

    //set up drive video downloader
    Timer downloadTimer = new Timer();
    downloadTimer.schedule(new VideoDownloaderTask(gds), 1000, 60000);

    new AppFrame(cqp);

    System.out.println(Config.getProperty("downloadPath"));
    System.out.println(Config.getProperty("tokenStoragePath"));
    System.out.println(Config.getProperty("googleCredentialsPath"));
  }
}
