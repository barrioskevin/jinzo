package com.kusa;

import com.kusa.jobs.DownloadFromDrive;
import com.kusa.jobs.ServerSocketController;
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
  private SidePanel left;
  private SidePanel right;
  private VideoPanel middle;

  private Playlist leftPanelPlaylist;
  private Playlist rightPanelPlaylist;
  private Playlist videoPlaylist;

  public JinzoApp(GDriveService gds) {
    this.gds = gds;
    this.middle = middle;
    this.running = false;
    this.executor = Executors.newScheduledThreadPool(
      3,
      Executors.defaultThreadFactory()
    );

    //init playlists.
    leftPanelPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(SidePanel.photoMRLS(true))
    );
    rightPanelPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(SidePanel.photoMRLS(false))
    );
    videoPlaylist = new CircularQueuePlaylist(
      new ArrayList<String>(VideoPanel.videoMRLS())
    );

    //init panels.
    left = new SidePanel(
      leftPanelPlaylist.current(),
      screenWidth() / 3,
      screenHeight()
    );
    right = new SidePanel(
      rightPanelPlaylist.current(),
      screenWidth() / 3,
      screenHeight()
    );
    middle = new SingleVideoPanel(videoPlaylist, gds);

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
    //  -- maybe pass reference to **this** --
    //        instead of middle.
    executor.submit(new ServerSocketController(this));
  }

  private int screenWidth() {
    return AppFrame.device.getDisplayMode().getWidth();
  }

  private int screenHeight() {
    return AppFrame.device.getDisplayMode().getHeight();
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
      new UpdateSidePanel(left, leftPanelPlaylist, true),
      0L,
      5L,
      TimeUnit.MINUTES //(call playlist.next) every 5 minutes
    );
    executor.scheduleAtFixedRate(
      new UpdateSidePanel(right, rightPanelPlaylist, false),
      0L,
      5L,
      TimeUnit.MINUTES //(call playlist.next) every 5 minutes
    );
  }

  /**
   * starts the player in full screen mode.
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
    if (!running) return "";
    return videoPlaylist.current();
  }

  public int currentVideoIndex() {
    if (!running) return -1;
    if (videoPlaylist.index() == 0) return videoPlaylist.size() - 1;
    return videoPlaylist.index() - 1;
  }

  /**
   * If the player is running this will return the tracklist
   * that is loaded into 'videoPlaylist'.
   *
   * @return video playlist's tracklist or empty.
   */
  public List<String> videoTrackList() {
    if (!running) return Collections.emptyList();
    return videoPlaylist.trackList();
  }

  /**
   * Returns the current image on the left panel.
   * @return string representing mrl of left panel's image or empty.
   */
  public String currentLeftPanelMedia() {
    if (!running) return "";
    return leftPanelPlaylist.current();
  }

  /**
   * Returns the current image on the right panel.
   * @return string representing mrl of right panel's image or empty.
   */
  public String currentRightPanelMedia() {
    if (!running) return "";
    return rightPanelPlaylist.current();
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
