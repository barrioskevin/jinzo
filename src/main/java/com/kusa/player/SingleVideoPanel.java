package com.kusa.player;

import com.kusa.playlist.Playlist;
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

  private PlaylistFile playlistFile;
  private Playlist playlist;
  private int playlistIndex;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;

  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * @param playlist_ the playlist dictating the media panel will play.
   * @param gds_ the apps google drive service.
   */
  public SingleVideoPanel(PlaylistFile playlistFile_, GDriveService gds_) {
    super();
    this.playlistFile = playlistFile_;
    this.gds = gds_;
    this.playlist = Playlist.dailyPlaylist(playlistFile);
    this.playlistIndex = 0;

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
    final String track = playlist.trackAt(playlistIndex);
    mediaPlayer().media().play(track);
    log(
      String.format("Starting video panel.\n [index]:%d\n [vid]:%s", playlistIndex, track)
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
    log(String.format("Now playing:\n [index]%d\n [vid]:%s", playlistIndex, playlist.trackAt(playlistIndex)));
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
    if (playlist.isEmpty()) log("[WARNING] Video panel playlist is empty!!!");
    log("a video just finished.");

    //advance index and see if we are at end.
    if(playlistIndex++ == playlist.size())
    {
      log("playlist ended! reloading playlist...");
      playlistFile.reload();
      playlist = Playlist.dailyPlaylist(playlistFile);
      playlist.shuffle();
      playlistIndex = 0;

      log(String.format("new playlist loaded with %d tracks.", playlist.size()));
      List<String> tracks = playlist.trackList();
      for (int i = 0; i < tracks.size(); i++) log(
        String.format(" [%d] : %s", i, tracks.get(i))
      );
    }

    mediaPlayer()
      .submit(() ->
        mediaPlayer()
          .media()
          .play(
            playlist.trackAt(playlistIndex),
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
    return playlist.trackList();
  }

  private void log(String message) {
    System.out.println("[SingleVideoPanel] " + message);
  }
}
