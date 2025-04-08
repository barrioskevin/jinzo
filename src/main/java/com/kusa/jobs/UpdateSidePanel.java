package com.kusa.jobs;

import com.kusa.player.SidePanel;
import com.kusa.playlist.Playlist;
import java.util.Set;

public class UpdateSidePanel implements Runnable {

  private String panelName;
  private SidePanel panel;
  private Playlist playlist;
  private boolean isLeft;

  public UpdateSidePanel(SidePanel panel, Playlist playlist, boolean isLeft) {
    this.panel = panel;
    this.playlist = playlist;
    this.isLeft = isLeft;
    this.panelName = isLeft ? "left" : "right";
  }

  @Override
  public void run() {
    if (playlist.index() == 0) {
      log("clearing playlist.");

      //the photos come from side panel static method.
      Set<String> photos = SidePanel.photoMRLS(isLeft);
      if (!photos.isEmpty()) {
        playlist.clear();
        log(
          String.format("setting up new playlist for %s panel...", panelName)
        );
        for (String pic : photos) {
          log(String.format("adding %s to %s panel playlist.", pic, panelName));
          playlist.add(pic);
        }
      }
    }
    log(
      String.format(
        "set new image %s on %s panel",
        playlist.current(),
        panelName
      )
    );
    panel.setImage(playlist.next());
  }

  private void log(String message) {
    System.out.println("[UpdateSidePanel] " + message);
  }
}
