package com.kusa.playlist;

import scala.collection.JavaConverters._

object Playlist {
  //for use from java to easily create playlists.
  def fromList(list : java.util.List[String]) =
    Playlist(Vector.from(list.asScala))
}

class Playlist(val tracks : Vector[String] = Vector()):
  /**
   * Returns this playlist's tracklist.
   * 
   * the returned list is not intended to be modified.
   *
   * @return List<String> list of media tracks as MRLs 
   */
  def trackList() : java.util.List[String] = tracks.asJava

  /**
   * Returns the track of this playlist at a given index.
   *
   * @param index the index of the track to retrieve.
   *
   * @throws IndexOutOfBoundsException if index is invalid.
   * @return string of track at the given index.
   */
  def trackAt(index : Int) : String =
    tracks(index)

  /**
   * Returns a **new** playlist with the
   * same contents of this playlist in a random order.
   *
   * @return playlist with a random ordering.
   */
  def shuffle() : Playlist = 
    Playlist(scala.util.Random.shuffle(tracks))

  /**
   * Returns the length of this playlist's tracklist.
   * @return tracklist length.
   */
  def size() : Int = tracks.length

  /**
   * Returns true if 'title' is a track in this
   * playlist.
   *
   * @return true if 'title' exists in this playlist.
   */
  def contains(title : String) : Boolean = 
    tracks.contains(title)

  def indexOf(title : String) : Int =
    tracks.indexOf(title)

  /**
   * Returns true if this playlist has no tracks.
   *
   * @return ture if tracklist is empty. 
   */
  def isEmpty() : Boolean =
    tracks.length == 0
