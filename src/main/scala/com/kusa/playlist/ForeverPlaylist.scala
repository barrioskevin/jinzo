package com.kusa.playlist

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

class ForeverPlaylist(val tracklist: ArrayBuffer[String] = ArrayBuffer()) extends Playlist:
  private var index_ : Int = -1

  /**
   * Increments index_ and returns the current track.
   */
  def next() : String =
    index_ = index_ + 1
    if (index_ == tracklist.length) then
      index_ = 0
      tracklist(tracklist.length-1)
    else
      tracklist(index_ - 1)

  /**
   * Updates the index_ to index_ of new value and returns
   * the track at that index_.
   *
   * --- our index_ will be one ahead of what we just
   *      skipped to ---
   */
  def skipTo(idx : Int) : String = 
    index_ = idx + 1
    tracklist(idx)

  /*
   * it looks like the list methods used in java
   * return booleans while that is not the case here.
   * maybe i need to wrap this in a try catch to properly
   * return a value. for now we just kind of return true.
   */

  def add(title : String) : Boolean =
    tracklist += title
    true

  //removes all cases of this title in the playlist.
  def remove(title : String) : Boolean =
    tracklist.filterInPlace(_ != title)
    true

  def remove(idx : Int) : Boolean = 
    tracklist.remove(idx)
    true

  def clear() : Boolean =
    tracklist.clear()
    true

  def isEmpty() : Boolean =
    tracklist.length == 0

  def shuffle() : Unit =
    scala.util.Random.shuffle(tracklist)

  def contains(title : String) : Boolean =
    tracklist.contains(title)

  def current() : String =
    index_ match
      case 0 => tracklist(tracklist.length-1)
      case _ => tracklist(index_ - 1)

  def index() : Int = index_

  def size() : Int = tracklist.length

  def trackList() : java.util.List[String] =
    tracklist.asJava
