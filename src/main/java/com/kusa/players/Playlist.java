package com.kusa.players;


import com.kusa.Config; 
import com.kusa.service.LocalService;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Class respresenting a circular queue playlist.
 *
 * We use the term MRL for playlists. but when we are passing around mrls we assume that it's relative
 * to the main media directory represented by the variable "vidDir". This means that when adding videos
 * to playlists, you should only pass the name of the file since it will only look in this specific directory.
 *
 * example:
 *  - video exists in $HOME/.jinzo/drive/videos/somevideo.mp4
 *  - adding to playlist: playlist.addVideoMRL("somevideo.mp4");
 *  - playlist will play the video by using "vidDir + videoMRLS.get(index)".
 *  - ultimately resolving to $HOME/.jinzo/drive/videos/somevideo.mp4
 */
public class Playlist
{
  public static final String vidDir = Config.getProperty("downloadPath") + "videos/";
  List<String> videoMRLS;
  Set<String> videoTitles;
  private int videoIndex;
  private String currentVideoPath;

  public Playlist()
  {
    videoMRLS = new ArrayList<>(LocalService.getDownloadedVideoNames());
    videoTitles = new HashSet<>(videoMRLS);
    videoIndex = 0;
    currentVideoPath = vidDir + (videoMRLS.isEmpty() ? "" : videoMRLS.get(0));
  }

  /**
   * Adds the mrl to the next position in playlist.
   * @param mrl Media Resource Link to be added.
   */
  public void addVideoMRL(String mrl)
  {
    videoTitles.add(mrl);
    videoMRLS.add(mrl);
  }

  /**
   * Returns true if the MRL is included in the playlist.
   * @param mrl Media Resource Link to check if in playlist.
   * @return true if mrl exists in this playlist.
   */
  public boolean hasTitle(String mrl)
  {
    return videoTitles.contains(mrl);
  }
  
  /**
   * Expected to return the next MRL in the playlist.
   *
   * should always return a valid link to play.
   *
   * @return String of MRL for next media in playlist. 
   */
  public String next()
  {
    if(videoMRLS.isEmpty())
      return currentVideoPath;

    if(videoIndex >= videoMRLS.size())
      videoIndex = 0;

    if(videoMRLS.get(videoIndex) != null)
      currentVideoPath = vidDir + videoMRLS.get(videoIndex++);

    return currentVideoPath;
  }


  /**
   * Gets the MRL of current media playing.
   *
   * @return String of MRL of current media in playlist. 
   */
  public String getCurrent() { return this.currentVideoPath; }

  /**
   * Gets the index of current track in playlist.
   *
   * @return int representing playlist's index.
   */
  public int getIndex() { return this.videoIndex; }
}
