package com.kusa.jobs;

import com.kusa.player.SidePanel;
import com.kusa.playlist.Playlist;

import java.util.Set;

//60000, 300000); //every 5 min
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
    if(playlist.index() != playlist.size() - 1)
    {
      panel.setImage(playlist.next());
      return;
    }
    System.out.println("[end of panel playlist!]");

    Set<String> photos = SidePanel.photoMRLS(isLeft);
    if(!photos.isEmpty())
    {
      playlist.clear();
      for(String pic : photos)
        playlist.add(pic);
    }
    panel.setImage(playlist.next());
  }
}
