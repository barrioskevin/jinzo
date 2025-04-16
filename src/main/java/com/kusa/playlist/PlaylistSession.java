package com.kusa.playlist;

import com.kusa.util.PlaylistFile;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for managing a playlist session.
 *
 * playlist sessions are started by reading from a playlist
 * file. and then managing a session from there.
 * the sessions reloads and updates to reflect the latest config.
 * tracks can be added and removed from sessions with the option
 * of writing changes back to a config.
 *
 * instead of a class that uses a playlist tracking playlist information,
 * they should be using an instance of a session.
 */
public class PlaylistSession {

  class IndexedTrack {

    private String track;
    private int index;

    public IndexedTrack(String t, int i) {
      track = t;
      index = i;
    }

    public int getIndex() {
      return index;
    }

    public String getTrack() {
      return track;
    }
  }

  //the (.playlist) config file.
  private PlaylistFile source;

  //active playlist.
  private Playlist playlist;

  private String section;
  private int index;
  private List<IndexedTrack> addedTracks;
  private List<IndexedTrack> removedTracks;

  public PlaylistSession(PlaylistFile source) {
    this.addedTracks = new ArrayList<>();
    this.removedTracks = new ArrayList<>();
    this.source = source;
    //read section of the day...
    //could be an option for something else later
    //but this is how it behaves now.
    this.section = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    this.playlist = this.source.playlistFromSection(this.section);
    this.index = 0;
  }

  public String current() {
    return playlist.trackAt(index);
  }

  public void next() {
    index += 1;
    //here we can check if we should
    //loop or just stop advancing.
    if (index == playlist.size()) onPlaylistEnded();
  }

  public List<String> sessionTrackList() {
    return playlist.trackList();
  }

  public List<String> configTrackList() {
    return source.playlistFromSection(this.section).trackList();
  }

  public boolean isEmpty() {
    return this.playlist.isEmpty();
  }

  public int size() {
    return this.playlist.size();
  }

  private void onPlaylistEnded() {
    source.reload();
    this.section = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    List<String> updatedTracks = new ArrayList<>(
      source.playlistFromSection(section).trackList()
    );
    //merge changes list after reload.
    for (IndexedTrack it : addedTracks) {
      int addIndex = Math.max(
        0,
        Math.min(it.getIndex(), updatedTracks.size() - 1)
      );
      updatedTracks.add(addIndex, it.getTrack());
    }
    for (IndexedTrack it : removedTracks) {
      if (
        it.getIndex() >= 0 &&
        it.getIndex() < updatedTracks.size() &&
        updatedTracks.get(it.getIndex()).equals(it.getTrack())
      ) updatedTracks.remove(it.getIndex());
      else if (updatedTracks.contains(it.getTrack())) updatedTracks.remove(
        it.getTrack()
      );
    }
    //shuffle on end.
    this.playlist = Playlist.fromList(updatedTracks).shuffle();
    index = 0;
  }

  public void back() {
    index -= 1;
    if (index < 0) index = 0;
  }

  public int getIndex() {
    return index;
  }

  public void add(String media) {
    add(media, playlist.size());
  }

  public void add(String media, int index) {
    List<String> updatedTracks = new ArrayList<>(playlist.trackList());
    updatedTracks.add(index, media);
    playlist = Playlist.fromList(updatedTracks);
    addedTracks.add(new IndexedTrack(media, index));
    //updated session index if needed.
    if (index < this.index) this.index += 1; //TODO TEST THIS
  }

  public void remove(int idx) {
    if (idx < 0 || idx >= playlist.size()) return;
    String toRemove = playlist.trackAt(idx);
    List<String> updatedTracks = new ArrayList<>(playlist.trackList());
    updatedTracks.remove(idx);
    playlist = Playlist.fromList(updatedTracks);
    removedTracks.add(new IndexedTrack(toRemove, idx));

    if (idx < this.index) this.index -= 1; //TODO TEST THIS
  }

  public void remove(String media) {
    int idxToRemove = playlist.indexOf(media);
    remove(idxToRemove);
  }
}
