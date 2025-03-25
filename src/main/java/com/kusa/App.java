package com.kusa;

import com.kusa.service.GDriveService;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Version;

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
    Gst.init(Version.BASELINE, "JINZO", args);
    GDriveService gds = new GDriveService();

    //initial downloads.
    if(gds.isValid())
      gds.downloadMedia();

    VlcjApp.exec(gds);
    //GstApp.exec(gds);
  }
}
