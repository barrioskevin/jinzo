package com.kusa.player;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import com.kusa.service.LocalService;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import com.kusa.playlist.Playlist;


/**
 * Class representing the video content in an engagment frame.
 *
 * this class extends vlcjs embedded media player component for
 * access to vlc bindings.
 */
public class VideoPanel extends EmbeddedMediaPlayerComponent 
{
  private Playlist playlist;
  
  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * we should proably add the playlist as a parameter to this 
   * class eventually so we don't have to manage the playlist 
   * within a video panel.
   */
  public VideoPanel(Playlist playlist_)
  {
    super();
    playlist = playlist_;
    setOpaque(true);

    //we setup a event adapter to automatically play the next video in a playlist
    //once a video ends.
    mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
      @Override
      public void finished(MediaPlayer mp)
      {
        final int idx = playlist.index();
        final String mrl = playlist.next();

        mediaPlayer().submit(() -> mediaPlayer().media().play(mrl, "--avcodec-hw=mmal", "--no-xlib", "--no-osd", "--no-interact", "--no-video-filter", "--quiet"));
        System.out.printf("NOW PLAYING %d : %s \n", idx, mrl);
      }
    });
  }

  /**
   * Advances the playlist and plays the next media.
   *
   * called by the main app to start the video panel. 
   *
   * because the video panel takes care of starting new videos 
   * after one ends this method should only be called once 
   * after the engagment frame has been created.
   */
  public void play()
  {
    final int idx = playlist.index();
    final String mrl = playlist.next();
    mediaPlayer().media().play(mrl);
    System.out.printf("NOW STARTING %d : %s \n", idx, mrl);
  }

}
