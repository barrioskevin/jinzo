package com.kusa.playlist

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

object ScalaPlaylistFactory {
  def foreverPlaylist(list : java.util.List[String]) =
    ForeverPlaylist(ArrayBuffer.from(list.asScala))
}
