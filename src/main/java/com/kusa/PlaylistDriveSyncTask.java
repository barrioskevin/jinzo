package com.kusa;

import com.kusa.playlist.Playlist;
import com.kusa.service.LocalService;
import com.kusa.service.GDriveService;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.List;
import java.util.TimerTask;
import java.util.Set;
import java.util.HashSet;

/**
 * Class that defines a task for syncing a playlist with google drive.
 *
 * This class operates on a playlist!
 *
 * we inject a gds from main app.
 *
 * plan on making this class work for any specified directory.
 *
 * right now we have this only working for videos.
 */
public class PlaylistDriveSyncTask extends TimerTask
{
  private Playlist playlist;
  private GDriveService gds;

  public PlaylistDriveSyncTask(Playlist playlist, GDriveService gds)
  {
    this.playlist = playlist;
    this.gds = gds;
  }

  /*
   * syncs files in drive with playlist.
   * WIP
   */
  @Override
  public void run()
  {
    final int idx = playlist.index();
    List<File> videos = gds.getVideoFiles();
    Set<String> localVideos = LocalService.getVideoMRLS();
    Set<String> driveVideos = new HashSet<>();
    for(File video : videos)
      driveVideos.add(LocalService.videosPath + video.getName());

    playlist.clear();
    Set<String> intersection = new HashSet<>(localVideos);
    intersection.retainAll(driveVideos);
    for(String mrl : intersection)
      playlist.add(mrl);
    playlist.skipTo(idx);
    printPlaylist();
  }

  private void printPlaylist()
  {
    System.out.println("PLAYLIST TRACKLIST:");
    List<String> tracks = playlist.trackList(); 
    for(int i = 0; i < tracks.size(); i++)
    {
      String title = tracks.get(i);
      System.out.println(i + " : " + title);
    }
  }
}
