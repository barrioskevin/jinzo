package com.kusa.player;

import com.kusa.playlist.Playlist;
import com.kusa.service.GDriveService;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

/**
 * Class representing the video content in an engagment frame.
 *
 * this class extends vlcjs embedded media list player component for
 * access to vlc bindings.
 */
public class VideoListPanel
  extends EmbeddedMediaListPlayerComponent
  implements VideoPanel {

  //tracks local files to play.
  private Playlist playlist;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;

  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * @param playlist_ the playlist dictating what the media panel will play.
   * @param gds_ the apps google drive service.
   */
  public VideoListPanel(Playlist playlist_, GDriveService gds_) {
    super();
    playlist = playlist_;
    gds = gds_;

    setOpaque(true); //maybe remove?
    setCursorEnabled(false); //kind of works. (ONLY OVER VID PANEL)

    //add all videos from playlist to media list.
    for (String video : playlist.trackList()) mediaListPlayer()
      .list()
      .media()
      .add(video);
  }

  /**
   * This event gets triggered when any new media starts to play.
   *
   * We make sure set the scale accordingly, log the current video,
   * and advance the playlist.
   */
  @Override
  public void playing(MediaPlayer mp) {
    //ensure correct scaling for frame based on video and container size.
    //  ** we can probably move this to elementaryStreamAdded event **
    mp
      .video()
      .setScale(VideoPanel.calcScale(mp.video().videoDimension(), getSize()));

    log(
      String.format(
        "Now playing...\n [vid] %s\n [idx] %d",
        playlist.current(),
        playlist.index()
      )
    );
    playlist.next();
  }

  /**
   * This event gets triggered when the player reaches the end of playlist.
   *
   * We clear the playlist and the players playlist.
   *
   * add new videos from 'videoMRLS' to local playlist
   *
   * shuffle playlist
   * add all the new media to the player.
   * call play on first index.
   */
  @Override
  public void mediaListPlayerFinished(MediaListPlayer mlp) {
    playlist.clear();
    //mediaListPlayer().submit(() -> mediaListPlayer().list().media().clear());
    mediaListPlayer().list().media().clear();
    for (String video : VideoPanel.videoMRLS()) playlist.add(video);

    playlist.shuffle();
    /*
    for (String video : playlist.trackList()) mediaListPlayer()
      .submit(() -> mediaListPlayer().list().media().add(video));
    */
    for (String video : playlist.trackList()) mediaListPlayer()
      .list()
      .media()
      .add(video);

    mediaListPlayer().submit(() -> mediaListPlayer().controls().play(0));
    log(String.format("Starting new playlist of %s videos.", playlist.size()));
  }

  /**
   * This event gets triggered after a new item in the media list is played.
   *
   * We make a call to videoMRLS after each video to see if we need to add anything.
   */
  @Override
  public void nextItem(MediaListPlayer mlp, MediaRef ref) {
    //add any new found tracks to playlist and media list.
    //TODO could be inefficient to perform this after each item.
    Set<String> currentTracks = new HashSet<>(playlist.trackList());
    for (String video : VideoPanel.videoMRLS()) {
      if (!currentTracks.contains(video)) {
        log(String.format("found new video! adding %s to playlist.", video));
        playlist.add(video);
        mediaListPlayer()
          .submit(() -> mediaListPlayer().list().media().add(video));
      }
    }
  }

  /**
   * Calls play on the internal media list player.
   *
   * expects the media list player to be <strong>Stopped.</strong>
   *
   * called by the main app to start the video panel.
   *
   * maybe we should force stop?
   */
  @Override
  public void start() {
    log(String.format("Starting new playlist of %s videos.", playlist.size()));
    log("START called.");
    mediaListPlayer().controls().play();
  }

  //stops the player.
  @Override
  public void stop() {
    mediaListPlayer().submit(() -> mediaListPlayer().controls().stop());
  }

  @Override
  public void shutdown() {
    this.release();
  }

  @Override
  public JPanel panel() {
    return (JPanel) this;
  }

  private void log(String message) {
    System.out.println("[VideoListPanel] " + message);
  }
}
