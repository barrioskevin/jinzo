package com.kusa.playlist;

import java.util.List;

public interface Playlist {

  //controls
  String next();
  String skipTo(int pIndex);

  //true if playlist changed after call.
  boolean add(String mrl);
  boolean remove(String mrl);
  boolean remove(int pIndex);
  boolean clear();
  boolean isEmpty();

  //true if mrl is in playlist.
  boolean contains(String mrl);

  //getters
  String current();
  int index();
  int size();
  List<String> trackList();
}
