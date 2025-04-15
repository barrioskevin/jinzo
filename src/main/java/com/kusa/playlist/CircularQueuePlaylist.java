package com.kusa.playlist;

import com.kusa.Config;
import com.kusa.service.LocalService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class respresenting a circular queue playlist.
 *
 */
public class CircularQueuePlaylist implements Playlist {

  List<String> mrls;
  Set<String> mrlSet;
  private int index;
  private String currentTrack;

  public CircularQueuePlaylist() {
    this(new ArrayList<String>());
    currentTrack = "Nothing. (empty)";
  }

  /**
   * Constructs a new circular queue playlist.
   *
   * this playlist defaults to loading in all downloaded videos.
   */
  public CircularQueuePlaylist(List<String> initialMRLS) {
    mrls = new ArrayList<>(initialMRLS);
    mrlSet = new HashSet<>(mrls);
    index = 0;
    if (mrls.size() > 0) currentTrack = mrls.get(0);
  }

  @Override
  public boolean clear() {
    if (mrls == null || mrls.isEmpty()) return false;
    if (mrlSet == null || mrlSet.isEmpty()) return false;

    mrls.clear();
    mrlSet.clear();
    index = 0;
    currentTrack = "Nothing. (empty)";
    return true;
  }

  /**
   * Adds the mrl to the end of playlist.
   *
   * we make sure the file actually exists in order
   * to prevent any potential issues ahead of time.
   *
   * @param mrl Media Resource Link to be added.
   * @return true if mrl was sucessfully added to playlist.
   */
  @Override
  public boolean add(String mrl) {
    if (!LocalService.fileExists(mrl)) return false;

    mrls.add(mrl);
    mrlSet.add(mrl);
    if(currentTrack.equals("Nothing. (empty)"))
      currentTrack = mrls.get(0);
    return true;
  }

  @Override
  public boolean isEmpty() {
    return mrls.isEmpty();
  }

  @Override
  public boolean remove(String mrl) {
    int index_ = mrls.indexOf(mrl);
    if (index_ < 0) {
      System.out.println(
        "Cant remove file from playlist. " +
        mrl +
        " does not exist in this playlist"
      );
      return false;
    }

    return remove(index_);
  }

  @Override
  public boolean remove(int pIndex) {
    try {
      String mrl = mrls.get(pIndex);
      mrls.remove(pIndex);
      mrlSet.remove(mrl);
      if (pIndex <= this.index) this.index--;
      System.out.println("REMOVED FILE FROM PLAYLIST. " + mrl);
    } catch (Exception e) {
      System.out.println("Couldnt remove file from playlist!");
      e.printStackTrace();
      return false;
    }

    return true;
  }

  /**
   * Returns true if the MRL is included in the playlist.
   *
   * @param mrl Media Resource Link to check if in playlist.
   * @return true if mrl exists in this playlist.
   */
  @Override
  public boolean contains(String mrl) {
    return mrlSet.contains(mrl);
  }

  /**
   * Updates the playlist queue and returns the new current title.
   *
   * this will loop back to 0 if we reached the end.
   * if the playlist is empty an empty string will be returned.
   *
   * @return String of MRL for next media in playlist or empty.
   */
  @Override
  public String next() {
    if (mrls.isEmpty()) {
      System.out.println("CALLING NEXT ON EMPTY PLAYLIST.");
      return "";
    }

    if (index >= mrls.size() || index < 0) index = 0;

    final String next = mrls.get(index++);
    if (!LocalService.fileExists(next)) {
      remove(next);
      return next(); // TODO LOOOK OUT RIGHT HERE COULD BE TROUBLE!!!
    }
    currentTrack = next;
    return next;
  }

  /**
   * Skips to the index specified if possible.
   *
   * because its a circular queue. if you pass an index
   * greater than the acttual playlists length, you will
   * still get updated to a valid position in the playlist it
   * would just keep cycling from the beg.
   *
   * @return the mrl of new current title. or empty if playlist empty.
   */
  @Override
  public String skipTo(int pIndex) {
    if (mrls.isEmpty()) return "";

    index = pIndex % mrls.size();
    currentTrack = mrls.get(index++);
    return currentTrack;
  }

  /**
   * Gets the MRL of current media playing.
   *
   * returns 'Nothing. (empty)' if playlist empty.
   *
   * @return String of MRL at index of playlist.
   */
  @Override
  public String current() {
    return currentTrack;
  }

  /**
   * Gets the index of queued track in playlist.
   *
   * @return int representing playlist's index.
   */
  @Override
  public int index() {
    return ((this.index >= mrls.size()) ? 0 : this.index);
  }

  @Override
  public void shuffle() {
    Collections.shuffle(mrls);
  }

  /**
   * Returns the track list in the order matching playlist.
   *
   * @return UNMODIFIABLE list of mrls in the playlist.
   */
  @Override
  public List<String> trackList() {
    //maybe just return mrls?
    return List.copyOf(mrls);
  }

  /**
   * Returns the number of tracks queued in the playlist.
   *
   * @return int representing number of tracks in the playlists queue.
   */
  @Override
  public int size() {
    return mrls.size();
  }
}
