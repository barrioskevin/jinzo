package com.kusa;
import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.VideoPanel;

import com.kusa.playlist.Playlist;
import com.kusa.playlist.CircularQueuePlaylist;

import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import java.time.LocalDateTime;
import java.time.DayOfWeek;

/**
 * Main class.
 *
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 * 
 * In this class we just initialize drive service, build
 * the engagement frame and play it.
 */
public class App 
{
  public static void main(String args[])
  {
    //initial downloads.
    GDriveService gds = new GDriveService();
    if(gds.isValid())
      gds.downloadMedia();

    //initial playlists.
    CircularQueuePlaylist leftPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(LocalService.getLocalMRLS("photos/left/", true)));
    CircularQueuePlaylist rightPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(LocalService.getLocalMRLS("photos/right/", true)));
    CircularQueuePlaylist videoPlaylist = new CircularQueuePlaylist(new ArrayList<String>(VideoPanel.videoMRLS()));

    //panels.
    SidePanel left = new SidePanel(leftPanelPlaylist.current());
    SidePanel right = new SidePanel(rightPanelPlaylist.current());
    VideoPanel middle = new VideoPanel(videoPlaylist, gds);

    //schedule poll and download task.
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run()
      {
        //playlist or video player won't be bothered between 12am - 8am
        int hour = LocalDateTime.now().getHour(); 
        if(hour >= 0 && hour < 8)
        {
          System.out.println("IT IS CURRENTLY THE " + hour + " HOUR SO WE WILL NOT MODIFY PLAYLIST OR USE SERVICES.");
          return;
        }

        //calls to is valid will
        //attempt to revalidate the drive service.
        if(gds.isValid())
          gds.downloadMedia();
      }
    }, 10000, 60000); //every 1 min

    Timer timer2 = new Timer();
    timer2.schedule(new TimerTask() {
      @Override
      public void run()
      {
        Set<String> currLeft = new HashSet<>(leftPanelPlaylist.trackList());
        Set<String> currRight = new HashSet<>(rightPanelPlaylist.trackList());
        for(String pic : LocalService.getLocalMRLS("photos/left/", true))
        {
          if(!currLeft.contains(pic))
          {
            System.out.println("ADDING NEW PICTURE " + pic + " TO LEFT PANEL PLAYLIST.");
            leftPanelPlaylist.add(pic);
          }
        }
        for(String pic : LocalService.getLocalMRLS("photos/right/", true))
        {
          if(!currRight.contains(pic))
          {
            System.out.println("ADDING NEW PICTURE " + pic + " TO RIGHT PANEL PLAYLIST.");
            rightPanelPlaylist.add(pic);
          }
        }
        left.setImage(leftPanelPlaylist.next());
        right.setImage(rightPanelPlaylist.next());
      }
    }, 60000, 300000); //every 5 min

    //create and play engagement frame.
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.fullscreen();
    middle.play();
  }
}
