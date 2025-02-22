package com.kusa.players;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class VideoPanel extends EmbeddedMediaPlayerComponent
{
  EmbeddedMediaPlayerComponent component;
  
  public VideoPanel()
  {
    setOpaque(true);
  }

  public void playVideo(String path)
  {
    mediaPlayer().media().play(path);
  }
}
