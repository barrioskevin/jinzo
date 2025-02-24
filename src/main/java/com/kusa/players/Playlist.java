package com.kusa.players;


import com.kusa.Config; 
import com.kusa.service.LocalService;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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

  public void addVideoMRL(String mrl)
  {
    videoTitles.add(mrl);
    videoMRLS.add(mrl);
  }

  public boolean hasTitle(String mrl)
  {
    return videoTitles.contains(mrl);
  }
  
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


  public String getCurrent() { return this.currentVideoPath; }
  public int getIndex() { return this.videoIndex; }
}
