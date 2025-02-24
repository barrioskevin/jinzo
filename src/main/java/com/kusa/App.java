package com.kusa;
import com.kusa.players.AppFrame;

import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.util.Collections;
import java.util.List;
import java.util.Timer;


/**
 * Main class.
 *
 * Responsible for launching the app and any services.
 *
 * we need to make sure that we do thing properly here.
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 * 
 * In this class we should just spin everything up.
 */
public class App 
{
  public static void main(String args[])
  {
    GDriveService gds = new GDriveService();

    new AppFrame();

    Timer downloadTimer = new Timer();
    downloadTimer.schedule(new VideoDownloaderTask(gds), 1000, 60000);

    System.out.println(Config.getProperty("downloadPath"));
    System.out.println(Config.getProperty("tokenStoragePath"));
    System.out.println(Config.getProperty("googleCredentialsPath"));
  }
}
