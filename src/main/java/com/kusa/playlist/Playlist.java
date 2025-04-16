package com.kusa.playlist;

import java.util.List;

import com.kusa.util.PlaylistFile;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

public interface Playlist {
  public static Playlist dailyPlaylist(PlaylistFile pf)
  {
    String day = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    return pf.playlistFromSection(day);
  }

  List<String> trackList();
  String trackAt(int index);

  //in place implementation.
  //ideaully this shuffles 'tracklist'
  void shuffle();

  int size();
  boolean contains(String mrl);
  boolean isEmpty();
}
