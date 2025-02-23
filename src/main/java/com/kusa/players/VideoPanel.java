package com.kusa.players;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import com.kusa.service.LocalService;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import com.kusa.PlaylistUpdaterTask;


public class VideoPanel extends EmbeddedMediaPlayerComponent
{
  EmbeddedMediaPlayerComponent component;
  private Playlist playlist;
  
  public VideoPanel()
  {
    playlist = new Playlist();
    setOpaque(true);
    mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
      @Override
      public void finished(MediaPlayer mp)
      {
        mediaPlayer().submit(() -> mediaPlayer().media().play(playlist.next()));
      }
    });

    Timer t = new Timer();
    t.schedule(new PlaylistUpdaterTask(playlist), 1000, 60000);
  }

  public void play()
  {
    mediaPlayer().media().play(playlist.next());
  }

}
