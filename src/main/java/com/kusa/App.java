package com.kusa;

import com.kusa.service.GDriveService;

/**
 * Main class.
 *
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 * 
 * In this class we just initialize drive service, build
 * the engagement frame and run it.
 */
public class App 
{
  public static void main(String args[])
  {
    //init deps
    GDriveService gds = new GDriveService();

    //initial downloads.
    if(gds.isValid())
      gds.downloadMedia();

    VlcjApp.exec(gds);
  }
}
