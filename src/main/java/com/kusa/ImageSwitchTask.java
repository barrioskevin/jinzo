package com.kusa;

import java.util.TimerTask;
import com.kusa.players.SidePanel;

public class ImageSwitchTask extends TimerTask {
  private SidePanel panel;
  private String imagePath;
  public ImageSwitchTask(SidePanel panel, String path)
  {
    this.panel = panel;
    this.imagePath = path;
  }
//"/home/kusa/UwU/jinzo/src/main/resources/wallpaper.jpg"

  @Override
  public void run()
  {
    panel.setImage(imagePath);
  }

  public void setPath(String path) { this.imagePath = path; }
}
