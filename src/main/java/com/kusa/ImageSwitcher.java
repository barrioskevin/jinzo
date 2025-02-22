package com.kusa;

import java.util.TimerTask;
import com.kusa.players.SidePanel;

public class ImageSwitcher extends TimerTask {
  SidePanel panel;
  public ImageSwitcher(SidePanel panel){this.panel = panel;}

  @Override
  public void run()
  {
    panel.setImage("/home/kusa/UwU/jinzo/src/main/resources/wallpaper.jpg");
  }
}
