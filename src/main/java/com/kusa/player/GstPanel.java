package com.kusa.player;

import com.kusa.service.LocalService;
import com.kusa.service.GDriveService;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import java.net.URI;

import com.kusa.playlist.Playlist;
import com.kusa.util.PathedFile;
import com.kusa.Config;

import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.ElementFactory;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Structure;
import org.freedesktop.gstreamer.Version;
import org.freedesktop.gstreamer.elements.PlayBin;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.event.SeekFlags;
import org.freedesktop.gstreamer.message.Message;
import org.freedesktop.gstreamer.swing.GstVideoComponent;

/**
 * Class representing the video content in an engagment frame.
 *
 */
public class GstPanel extends GstVideoComponent
{
  private Playlist playlist;
  private PlayBin playbin;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;
  
  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * @param playlist_ the playlist dictating the media panel will play.
   * @param gds_ the apps google drive service.
   */
  public GstPanel(Playlist playlist_, GDriveService gds_)
  {
    super();

    this.playlist = playlist_;
    this.gds = gds_;
    this.playbin = new PlayBin("playbin");
    playbin.setVideoSink(getElement());

    setOpaque(true); //maybe remove?
  }

  public void begin()
  {
    Gst.invokeLater(() -> {
      playbin.stop();
      playlist.skipTo(0);
      playbin.setURI(URI.create("file:///" + playlist.next()));
      playbin.play();
    });
  }

  /**
   *
   * stops current playback, sets media to next uri in playlist,
   * plays the next media.
   *
   */
  public void playNext()
  {
    Gst.invokeLater(() -> {
      playbin.stop();
      playbin.setURI(URI.create("file:///" + playlist.next()));
      playbin.play();
    });
  }

  public PlayBin getPlayBin() { return this.playbin; }


  //VideoPanel.getVideoMrls();

}
