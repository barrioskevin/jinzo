package com.kusa.player;

import com.kusa.Config;
import com.kusa.playlist.Playlist;
import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;
import com.kusa.util.PathedFile;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
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

  private Playlist playlist;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;

  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * @param playlist_ the playlist dictating the media panel will play.
   * @param gds_ the apps google drive service.
   */
  public SingleVideoPanel(Playlist playlist_, GDriveService gds_) {
    super();
    playlist = playlist_;
    gds = gds_;

    setOpaque(true); //maybe remove?
  }

  /**
   * Advances the playlist and plays the next media.
   * expects the media player to be <strong>Stopped.</strong>
   *
   * called by the main app to start the video panel.
   *
   * because this video panel takes care of starting new videos
   * after one ends this method should only be called once
   * after the engagment frame has been created.
   */
  @Override
  public void start() {
    final int idx = playlist.index();
    final String mrl = playlist.next();
    mediaPlayer().media().play(mrl);
    log(
      String.format("Starting video panel.\n [index]:%d\n [vid]:%s", idx, mrl)
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

    boolean willShuffle = false;
    if (playlist.index() == 0) {
      log("playlist ended! calling clear...");
      playlist.clear();
      willShuffle = true;
    }

    Set<String> mrls = VideoPanel.videoMRLS();
    Set<String> currentTracks = new HashSet<>(playlist.trackList());
    for (String video : mrls) {
      if (!currentTracks.contains(video)) {
        log(String.format("found new video! adding %s to playlist.", video));
        playlist.add(video);
      }
    }

    if (willShuffle) {
      log("Shuffling Playlist!");
      playlist.shuffle();

      log(String.format("Start of new playlist! Index = ", playlist.index()));
      List<String> tracks = playlist.trackList();
      for (int i = 0; i < tracks.size(); i++) log(
        String.format(" [%d] : %s", i, tracks.get(i))
      );
    }

    final int idx = playlist.index();
    final String mrl = playlist.next();

    mediaPlayer()
      .submit(() ->
        mediaPlayer()
          .media()
          .play(
            mrl,
            "--avcodec-hw=mmal",
            "--no-xlib",
            "--no-osd",
            "--no-interact",
            "--no-video-filter",
            "--quiet"
          )
      );

    log(String.format("Now playing:\n [index]%d\n [vid]:%s", idx, mrl));
    log(String.format("Next index should be %d", playlist.index()));
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

  private void log(String message) {
    System.out.println("[SingleVideoPanel] " + message);
  }
}
