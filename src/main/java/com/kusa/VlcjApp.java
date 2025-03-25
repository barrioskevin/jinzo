package com.kusa;

import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.VideoPanel;

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
    executor.schedule(new DownloadFromDrive(gds), 3L, TimeUnit.MINUTES);
    executor.schedule(new UpdateSidePanel(left, leftPanelPlaylist, true), 5L, TimeUnit.MINUTES);
    executor.schedule(new UpdateSidePanel(right, rightPanelPlaylist, false), 5L, TimeUnit.MINUTES);

    //create and play engagement frame.
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.fullscreen();
    middle.play();
  }
}
