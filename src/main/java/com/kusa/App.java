package com.kusa;
import com.kusa.players.AppFrame;

import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.Collections;
import java.util.List;
import java.util.Timer;


public class App 
{
  public static void main(String args[])
  {
    GDriveService gds = new GDriveService();
    new AppFrame();

    Timer downloadTimer = new Timer();
    downloadTimer.schedule(new VideoDownloaderTask(gds), 1000, 60000);
  }
}
