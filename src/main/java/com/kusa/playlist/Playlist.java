package com.kusa.playlist;

import com.kusa.util.PlaylistFile;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public interface Playlist {
  public static Playlist dailyPlaylist(PlaylistFile pf) {
    String day = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    return pf.playlistFromSection(day);
  }

  List<String> trackList();
  String trackAt(int index);

  //in place implementation.
  //ideally this shuffles 'tracklist'
  void shuffle();

  int size();
  boolean contains(String mrl);
  boolean isEmpty();
}
