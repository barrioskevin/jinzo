package com.kusa.player;

import com.kusa.playlist.PlaylistSession;
import com.kusa.service.GDriveService;
import com.kusa.util.PlaylistFile;
import java.util.List;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

/**
 * Class representing the video content in an engagment frame.
 *
 * this class extends vlcjs embedded media player component for
 * access to vlc bindings.
 */
public class SingleVideoPanel
  extends EmbeddedMediaPlayerComponent
  implements VideoPanel {

  private PlaylistSession playlistSession;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;

  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * the session we use attempts to stay up to date with the config
   * passed in (playlistFile_) .
   *
   * @param playlistfile_ the player builds a session based off the file passed in.
   * @param gds_ the apps google drive service.
   */
  public SingleVideoPanel(PlaylistFile playlistFile_, GDriveService gds_) {
    super();
    this.playlistSession = new PlaylistSession(playlistFile_);
    this.gds = gds_;
    setOpaque(true); //maybe remove?
    setCursorEnabled(false); //kind of works. (ONLY OVER VID PANEL)
  }

  /**
   * Plays the media (no submit).
   * expects the media player to be <strong>Stopped.</strong>
   *
   * called by the main app to start the video panel.
   *
   * because this video panel takes care of starting new videos
   * after one ends this method should only be called once
   * after the engagment frame has been created.
   *
   * NOTE - we don't advance the index here.
   */
  @Override
  public void start() {
    final String track = playlistSession.current();
    final int idx = playlistSession.getIndex();
    mediaPlayer().media().play(track);
    log(
      String.format("Starting video panel.\n [index]:%d\n [vid]:%s", idx, track)
    );
  }

  /**
   * Overriding playing event in media player.
   *
   * this will get triggered when a video starts to play.
   *
   * will make a call to scale the video to fit fully in container.
   */
  @Override
  public void playing(MediaPlayer mp) {
    log(
      String.format(
        "Now playing:\n [index]%d\n [vid]:%s",
        playlistSession.getIndex(),
        playlistSession.current()
      )
    );
    mp
      .video()
      .setScale(VideoPanel.calcScale(mp.video().videoDimension(), getSize()));
  }

  /**
   * Overriding finished event in media player.
   *
   * this will get triggered when a video ends.
   *
   * shuffle if playlist is over.
   *
   * checks for new videos after every video ends.
   *
   * calls play to continue playing the next video.
   */
  @Override
  public void finished(MediaPlayer mp) {
    if (playlistSession.isEmpty()) log(
      "[WARNING] Video panel playlist is empty!!!"
    );
    log("a video just finished.");

    playlistSession.next();

    //track end for logging only... (maybe log in session instead?)
    if (playlistSession.getIndex() == 0) {
      log(
        String.format(
          "new playlist loaded with %d tracks.",
          playlistSession.size()
        )
      );
      List<String> tracks = playlistSession.sessionTrackList();
      for (int i = 0; i < tracks.size(); i++) log(
        String.format(" [%d] : %s", i, tracks.get(i))
      );
    }

    mediaPlayer()
      .submit(() ->
        mediaPlayer()
          .media()
          .play(
            playlistSession.current(),
            "--avcodec-hw=mmal",
            "--no-xlib",
            "--no-osd",
            "--no-interact",
            "--no-video-filter",
            "--quiet"
          )
      );
  }

  @Override
  public void error(MediaPlayer mp) {
    log("an error occured!!!");
  }

  @Override
  /**
   * sends a stop signal to player.
   *
   * should stop the video.
   */
  public void stop() {
    mediaPlayer().controls().stop();
  }

  /**
   * sends a pause signal to player using submit.
   *
   * should pause the video.
   */
  @Override
  public void pause() {
    mediaPlayer().controls().pause();
  }

  @Override
  public void restart() {
    stop();
    playlistSession.restart();
    start();
  }

  @Override
  public void paused(MediaPlayer mediaPlayer) {
    log("playback has been paused or resumed.");
  }

  @Override
  public JPanel panel() {
    return (JPanel) this;
  }

  @Override
  public void shutdown() {
    this.release();
  }

  @Override
  public boolean isPlaying() {
    return mediaPlayer().status().isPlaying();
  }

  @Override
  public String currentMedia() {
    return mediaPlayer().media().info().mrl();
  }

  @Override
  public List<String> tracks() {
    return playlistSession.sessionTrackList();
  }

  private void log(String message) {
    System.out.println("[SingleVideoPanel] " + message);
  }
}
