package com.kusa.playlist

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

object ScalaPlaylistFactory {
  def simplePlaylist(list : java.util.List[String]) =
    SimplePlaylist(ArrayBuffer.from(list.asScala))
}
