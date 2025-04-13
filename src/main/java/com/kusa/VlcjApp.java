package com.kusa;

import com.kusa.jobs.DownloadFromDrive;
import com.kusa.jobs.UpdateSidePanel;
import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.SingleVideoPanel;
import com.kusa.player.VideoListPanel;
import com.kusa.player.VideoPanel;
import com.kusa.playlist.CircularQueuePlaylist;
import com.kusa.playlist.Playlist;
import com.kusa.service.GDriveService;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 * vlcj app class.
 *
 * this class builds and runs an engagment frame
 * based on vlcj.
 */
public class VlcjApp {

  private static VideoPanel middle;
  public static boolean running;

  public static void exec(GDriveService gds) {
    //screen dimensions
    final int screenWidth = AppFrame.device.getDisplayMode().getWidth();
    final int screenHeight = AppFrame.device.getDisplayMode().getHeight();

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(
      3,
      Executors.defaultThreadFactory()
    );

    //initial playlists.
    CircularQueuePlaylist leftPanelPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(SidePanel.photoMRLS(true))
    );
    CircularQueuePlaylist rightPanelPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(SidePanel.photoMRLS(false))
    );
    CircularQueuePlaylist videoPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(VideoPanel.videoMRLS())
    );

    //panels.
    SidePanel left = new SidePanel(
      leftPanelPlaylist.current(),
      screenWidth / 3,
      screenHeight
    );
    SidePanel right = new SidePanel(
      rightPanelPlaylist.current(),
      screenWidth / 3,
      screenHeight
    );
    middle = new SingleVideoPanel(videoPlaylist, gds);

    //schedule tasks.
    executor.scheduleAtFixedRate(
      new DownloadFromDrive(gds, 12, 8),
      0L,
      15L,
      TimeUnit.MINUTES //download from drive every 15 min
    );
    executor.scheduleAtFixedRate(
      new UpdateSidePanel(left, leftPanelPlaylist, true),
      0L,
      5L,
      TimeUnit.MINUTES //(call next) every 5 minutes
    );
    executor.scheduleAtFixedRate(
      new UpdateSidePanel(right, rightPanelPlaylist, false),
      0L,
      5L,
      TimeUnit.MINUTES //(call next) every 5 minutes
    );

    //create engagement frame + add shutdown.
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            running = false;
            executor.shutdownNow();
            SwingUtilities.invokeLater(() ->
              engagementFrame.dispatchEvent(
                new WindowEvent(engagementFrame, WindowEvent.WINDOW_CLOSING)
              )
            );
          }
        }
      }
    );
    engagementFrame.setFocusable(true);

    //set fullscreen and play the video panel
    engagementFrame.fullscreen();
    middle.start();
    running = true;
  }

  public static void start() {
    if (middle == null) return;
    if (middle.isPlaying()) return;
    middle.start();
  }

  public static void pause() {
    if (middle == null) return;
    middle.pause();
  }
}
