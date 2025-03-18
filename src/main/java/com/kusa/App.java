package com.kusa;
import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
//import com.kusa.player.CustomSidePanel;
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

import com.kusa.util.PathedFile;
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
    CircularQueuePlaylist leftPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(SidePanel.photoMRLS(true)));
    CircularQueuePlaylist rightPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(SidePanel.photoMRLS(false)));
    CircularQueuePlaylist videoPlaylist = new CircularQueuePlaylist(new ArrayList<String>(VideoPanel.videoMRLS()));

    System.out.println(AppFrame.device.getDisplayMode().getWidth());
    System.out.println(AppFrame.device.getDisplayMode().getHeight());
    
    final int screenWidth = AppFrame.device.getDisplayMode().getWidth();
    final int screenHeight = AppFrame.device.getDisplayMode().getWidth();

    //panels.
    SidePanel left = new SidePanel(leftPanelPlaylist.current(), screenWidth/3, screenHeight);
    SidePanel right = new SidePanel(rightPanelPlaylist.current(), screenWidth/3, screenHeight);
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
        Set<String> newLeft = SidePanel.photoMRLS(true);
        Set<String> newRight = SidePanel.photoMRLS(false);
        if(!newLeft.isEmpty())
        {
          leftPanelPlaylist.clear();
          for(String mrl : newLeft)
          {
            System.out.println("ADDING PICTURE " + mrl + " TO LEFT PANEL PLAYLIST.");
            leftPanelPlaylist.add(mrl);
          }
        }
        if(!newRight.isEmpty())
        {
          rightPanelPlaylist.clear();
          for(String mrl : newRight)
          {
            System.out.println("ADDING PICTURE " + mrl + " TO LEFT PANEL PLAYLIST.");
            rightPanelPlaylist.add(mrl);
          }
        }
        left.setImage(leftPanelPlaylist.next());
        right.setImage(rightPanelPlaylist.next());
      }
    }, 60000, 300000); //every 5 min

    //create and play engagement frame.
    //CustomSidePanel csp = new CustomSidePanel();
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.fullscreen();
    middle.play();

  }
}
