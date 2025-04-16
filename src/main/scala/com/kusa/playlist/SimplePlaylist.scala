package com.kusa.playlist

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

class SimplePlaylist(val tracklist: ArrayBuffer[String] = ArrayBuffer()) extends Playlist:

  /**
   * Returns the track at given index.
   */
  def trackAt(idx : Int) : String = tracklist(idx)

  def isEmpty() : Boolean =
    tracklist.length == 0

  def shuffle() : Unit =
    scala.util.Random.shuffle(tracklist)

  def contains(title : String) : Boolean =
    tracklist.contains(title)

  def size() : Int = tracklist.length

  def trackList() : java.util.List[String] =
    tracklist.asJava
