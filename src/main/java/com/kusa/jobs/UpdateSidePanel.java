package com.kusa.jobs;

import com.kusa.player.SidePanel;
import com.kusa.playlist.Playlist;
import com.kusa.util.PlaylistFile;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Updates a side panels images based
 * on a playlist (file).
 */
public class UpdateSidePanel implements Runnable {

  private String panelName;
  private SidePanel panel;
  private PlaylistFile playlistFile;
  private Playlist playlist;
  private int index;

  public UpdateSidePanel(SidePanel panel, PlaylistFile playlistFile) {
    this(panel, playlistFile, "Panel", 0);
  }

  public UpdateSidePanel(
    SidePanel panel,
    PlaylistFile playlistFile,
    String name,
    int startingIndex
  ) {
    this.panelName = name;
    this.panel = panel;
    this.playlistFile = playlistFile;
    this.playlist = Playlist.dailyPlaylist(playlistFile);
    this.index = startingIndex;
  }

  //reloads the playlist file whenever playlist ends.
  @Override
  public void run() {
    if (index == playlist.size()) {
      log("clearing playlist.");
      index = 0;

      //the photos come from side panel static method.
      this.playlistFile.reload();
      this.playlist = Playlist.dailyPlaylist(playlistFile);

      log(
        String.format(
          "new side panel playlist loaded for %s\nin queue...",
          panelName
        )
      );
      for (int i = 0; i < playlist.size(); i++) {
        String pic = playlist.trackList().get(i);
        log(String.format(" [%d] %s", i, pic));
      }
    }
    //get next image (increments index)
    try {
      BufferedImage nextImage = ImageIO.read(
        new File(playlist.trackAt(index++))
      );
      panel.setImage(nextImage);
      log(String.format("set new image %s on %s panel", nextImage, panelName));
    } catch (Exception ex) {
      log(
        String.format(
          "failed to set the next image in %s side panel",
          panelName
        )
      );
    }
  }

  private void log(String message) {
    System.out.println("[UpdateSidePanel] " + message);
  }
}
