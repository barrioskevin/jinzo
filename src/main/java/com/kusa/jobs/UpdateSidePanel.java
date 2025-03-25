package com.kusa.jobs;

import com.kusa.player.SidePanel;
import com.kusa.playlist.Playlist;

import java.util.Set;

public class UpdateSidePanel implements Runnable
{
  private SidePanel panel;
  private Playlist playlist;
  private boolean isLeft;
  public UpdateSidePanel(SidePanel panel, Playlist playlist, boolean isLeft)
  {
    this.panel = panel;
    this.playlist = playlist;
    this.isLeft = isLeft;
  }

  @Override
  public void run()
  {
    if(playlist.index() == 0)
    {
      System.out.println("[clearing playlist!]");

      //the photos come from side panel static method.
      Set<String> photos = SidePanel.photoMRLS(isLeft);
      if(!photos.isEmpty())
      {
        playlist.clear();
        for(String pic : photos)
          playlist.add(pic);
      }
    }
    panel.setImage(playlist.next());
  }
}
