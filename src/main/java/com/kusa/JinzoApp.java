package com.kusa;

import com.kusa.jobs.DownloadFromDrive;
import com.kusa.jobs.ServerSocketController;
import com.kusa.jobs.UpdateSidePanel;
import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.SingleVideoPanel;
import com.kusa.player.VideoPanel;
import com.kusa.playlist.Playlist;
import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;
import com.kusa.util.PlaylistFile;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

/**
 * Builds the fundamental components for a AppFrame
 * and starts + controls the execution.
 * (when exec() is called)
 *
 * this class builds and runs an engagment frame.
 *
 * in the constructor you we create the engagment
 * frame window along with the inner panels.
 *
 * when run is called we start
 * the playback in fullscreen.
 *
 * jinzo app = engagment player O_o
 * (the complete thing! panels and video player setup)
 */
public class JinzoApp {

  private boolean running;

  private final GDriveService gds;
  private final ScheduledExecutorService executor;
  private final AppFrame engagementFrame;

  private final SidePanel left;
  private final SidePanel right;
  private final VideoPanel middle;
  /*
   *      (left)            (middle)         (right)
   *   |----------------                ________________|
   *   |________________                ________________|
   *   |________________                ________________|   /\
   *   |________________                ________________|   ||
   *   |________________                ________________|
   *   |________________                ________________| screenHeight
   *   |________________                ________________|
   *   |________________                ________________|   ||
   *   |________________                ________________|   \/
   *   |________________                ________________|
   *   |________________                ________________|
   *   |________________                ________________|
   *   |________________                ________________|
   *   |________________                ________________|
   *   |________________________________________________|
   *                <--- screenWidth --->
   */

  private PlaylistFile leftPanelPlaylistFile;
  private PlaylistFile rightPanelPlaylistFile;
  private PlaylistFile videoPanelPlaylistFile;

  private UpdateSidePanel leftPanelUpdater;
  private UpdateSidePanel rightPanelUpdater;

  public JinzoApp(GDriveService gds) {
    this.gds = gds;
    this.running = false;
    this.executor = Executors.newScheduledThreadPool(
      3,
      Executors.defaultThreadFactory()
    );

    //app crashes if can't read playlist configs.
    try {
      videoPanelPlaylistFile = new PlaylistFile(Config.playlistFiles()[0]);
      leftPanelPlaylistFile = new PlaylistFile(Config.playlistFiles()[1]);
      rightPanelPlaylistFile = new PlaylistFile(Config.playlistFiles()[2]);
    } catch (IOException ioexception) {
      ioexception.printStackTrace();
      System.exit(1);
    }

    //init panels.
    //we dont pass image to side panels becuause
    //the executor will handle setting images.
    left = new SidePanel(
      LocalService.screenWidth / 3,
      LocalService.screenHeight
    );
    right = new SidePanel(
      LocalService.screenWidth / 3,
      LocalService.screenHeight
    );
    middle = new SingleVideoPanel(videoPanelPlaylistFile, gds);

    leftPanelUpdater = new UpdateSidePanel(
      left,
      leftPanelPlaylistFile,
      "LEFT-PANEL",
      0
    );
    rightPanelUpdater = new UpdateSidePanel(
      right,
      rightPanelPlaylistFile,
      "RIGHT-PANEL",
      0
    );

    //sumbit repeating fixed rate tasks to executor.
    scheduleTasks();

    /*
     * init engagement frame + add listeners.
     *
     * what the frame window will look like.
     *  - foucused.
     *  - fullscreen.
     *  - playing videos in middle
     */
    engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.addKeyListener(
      new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            stop();
          }
        }
      }
    );

    //setup command controller.
    executor.submit(new ServerSocketController(this));
  }

  //playlists and panels should be initialized before calling this.
  private void scheduleTasks() {
    executor.scheduleAtFixedRate(
      new DownloadFromDrive(gds, 12, 8),
      0L,
      15L,
      TimeUnit.MINUTES //download from drive every 15 min
    );
    executor.scheduleAtFixedRate(
      leftPanelUpdater,
      0L,
      15L,
      TimeUnit.SECONDS //(set next playlist item) every 5 minutes
    );
    executor.scheduleAtFixedRate(
      rightPanelUpdater,
      0L,
      15L,
      TimeUnit.SECONDS //(set next playlist item) every 5 minutes
    );
  }

  /**
   * starts the player in full screen mode.
   * not to be confused with a runnable.
   *
   * does nothing if already running.
   * makes sure to set focus.
   * calls start on middle panel.
   */
  public void run() {
    if (running) return;
    engagementFrame.setFocusable(true);
    engagementFrame.fullscreen();
    middle.start();
    running = true;
  }

  /**
   * stops and destroys the player.
   *
   * after calling stop you should get rid of the instance.
   */
  public void stop() {
    running = false;

    if (executor != null) executor.shutdownNow();

    /*
     * Invoking close on the AppFrame will
     * handle shutting down the video panel!
     *
     * side panels dont need a shutdown their updates
     * are controlled by the executor.
     */
    if (engagementFrame != null) {
      SwingUtilities.invokeLater(() ->
        engagementFrame.dispatchEvent(
          new WindowEvent(engagementFrame, WindowEvent.WINDOW_CLOSING)
        )
      );
    }
  }

  /**
   * Restarts the video player.
   * We are restarting the playback. a new instance of the
   * player will not be created.
   *
   * we still need to implement restarting the side panels also.
   */
  public void restart() {
    if (!running) return;
    middle.restart();
    leftPanelUpdater.restart();
    rightPanelUpdater.restart();
  }

  /**
   * pauses the player's playback.
   */
  public void pause() {
    if (!running) return;

    //video panel implementation takes care of pause logic
    middle.pause();
  }

  /**
   * Returns the current video playing/loaded
   * according to the video playlist.
   *
   * @return current media from playlist or empty.
   */
  public String currentVideoPanelMedia() {
    return middle.currentMedia();
  }

  /**
   * Returns the current image on the left panel.
   * @return string representing mrl of left panel's image or empty.
   */
  public String currentLeftPanelMedia() {
    return left.currentMedia();
  }

  /**
   * Returns the current image on the right panel.
   * @return string representing mrl of right panel's image or empty.
   */
  public String currentRightPanelMedia() {
    return right.currentMedia();
  }

  public List<String> videoPanelTrackList() {
    return middle.tracks();
  }

  /**
   * True if app frame is open.
   *
   * the executor will be executing tasks when running.
   */
  public boolean isRunning() {
    return this.running;
  }
}
