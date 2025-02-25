package com.kusa;

import com.kusa.playlist.Playlist;
import com.kusa.service.LocalService;

import java.util.TimerTask;
import java.util.Set;

public class PlaylistUpdaterTask extends TimerTask
{
  private Playlist playlist;

  public PlaylistUpdaterTask(Playlist playlist)
  {
    this.playlist = playlist;
  }

  /*
   * merges downloaded list with playlist.
   */
  @Override
  public void run()
  {
    Set<String> downloadedVideos = LocalService.getVideoMRLS();
    for(String video : downloadedVideos)
      if(!playlist.contains(video))
        playlist.add(video);
  }
}
