package com.kusa;

import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.VideoPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.kusa.playlist.Playlist;
import com.kusa.playlist.CircularQueuePlaylist;

import com.kusa.service.GDriveService;

import com.kusa.jobs.DownloadFromDrive;
import com.kusa.jobs.UpdateSidePanel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

/**
 * vlcj app class.
 *
 * this class builds and runs an engagment frame
 * based on vlcj.
 */
public class VlcjApp 
{
  public static void exec(GDriveService gds)
  {
    //screen dimensions
    final int screenWidth = AppFrame.device.getDisplayMode().getWidth();
    final int screenHeight = AppFrame.device.getDisplayMode().getHeight();

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3, Executors.defaultThreadFactory());

    //initial playlists.
    CircularQueuePlaylist leftPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(SidePanel.photoMRLS(true)));
    CircularQueuePlaylist rightPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(SidePanel.photoMRLS(false)));
    CircularQueuePlaylist videoPlaylist = new CircularQueuePlaylist(new ArrayList<String>(VideoPanel.videoMRLS()));
    
    //panels.
    SidePanel left = new SidePanel(leftPanelPlaylist.current(), screenWidth/3, screenHeight);
    SidePanel right = new SidePanel(rightPanelPlaylist.current(), screenWidth/3, screenHeight);
    VideoPanel middle = new VideoPanel(videoPlaylist, gds);

    //schedule tasks.
    executor.scheduleAtFixedRate(new DownloadFromDrive(gds), 0L, 30L, TimeUnit.SECONDS);
    executor.scheduleAtFixedRate(new UpdateSidePanel(left, leftPanelPlaylist, true), 0L, 5L, TimeUnit.MINUTES);
    executor.scheduleAtFixedRate(new UpdateSidePanel(right, rightPanelPlaylist, false), 0L, 5L, TimeUnit.MINUTES);

    //create engagement frame + add shutdown.
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.addWindowListener(new WindowAdapter() {
      @Override 
      public void windowClosing(WindowEvent e)
      {
        System.out.println("JINZO QUITTING...");
        engagementFrame.shutdown();
        executor.shutdownNow();
        System.exit(0); //app quits when main frame is closed.
      }
    });
      
    //set fullscreen and play the video panel
    engagementFrame.fullscreen();
    middle.play();
  }
}
