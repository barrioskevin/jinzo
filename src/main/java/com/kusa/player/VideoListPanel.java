package com.kusa.player;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType; 
import uk.co.caprica.vlcj.medialist.MediaList;
import uk.co.caprica.vlcj.player.base.events.MediaPlayerEvent;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import com.kusa.service.LocalService;
import com.kusa.service.GDriveService;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import com.kusa.playlist.Playlist;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import com.kusa.util.PathedFile;
import com.kusa.Config;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class representing the video content in an engagment frame.
 *
 * this class extends vlcjs embedded media list player component for
 * access to vlc bindings.
 */
public class VideoListPanel extends EmbeddedMediaListPlayerComponent 
{
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
  public VideoListPanel(Playlist playlist_, GDriveService gds_)
  {
    super();
    playlist = playlist_;
    gds = gds_;

    setOpaque(true); //maybe remove?
    setCursorEnabled(false); //kind of works. (ONLY OVER VID PANEL)
                     
    //add all videos from playlist to media list.
    for(String video : playlist.trackList())
      mediaListPlayer().list().media().add(video);
  }

  @Override
  public void playing(MediaPlayer mp)
  {
    //ensure correct scaling for frame based on video and container size.
    mp.video().setScale(calcScale(mp.video().videoDimension(), getSize()));

    log(String.format("Now playing...\n [vid] %s\n [idx] %d", playlist.current(), playlist.index())); 
    playlist.next();
  }

  @Override
  public void mediaListPlayerFinished(MediaListPlayer mlp)
  {
    playlist.clear();
    mediaListPlayer().submit(() -> mediaListPlayer().list().media().clear());
    for(String video : videoMRLS())
      playlist.add(video);

    playlist.shuffle();
    for(String video : playlist.trackList())
      mediaListPlayer().submit(() -> mediaListPlayer().list().media().add(video));

    mediaListPlayer().submit(() -> mediaListPlayer().controls().play(0));

    log(String.format("Starting new playlist of %s videos.", playlist.size()));
  }

  @Override
  public void nextItem(MediaListPlayer mlp, MediaRef ref)
  {
    //add any new found tracks to playlist and media list.
    //TODO could be inefficient to perform this after each item.
    Set<String> currentTracks = new HashSet<>(playlist.trackList());
    for(String video : videoMRLS())
    {
      if(!currentTracks.contains(video))
      {
        log(String.format("found new video! adding %s to playlist.", video));
        playlist.add(video);
        mediaListPlayer().submit(() -> mediaListPlayer().list().media().add(video));
      }
    }
  }

  /**
   * Calls play on the internal media list player.
   *
   * expects the media list player to be <strong>Stopped.</strong>
   *
   * called by the main app to start the video panel. 
   */
  public void play()
  {
    log(String.format("Starting new playlist of %s videos.", playlist.size()));
    log("wrapper play() called.");
    mediaListPlayer().controls().play();
  }

  //attempts to find a correct scale to ensure the container
  //will be filled with video content.
  //
  //this is likely to crop and zoom the video.
  private float calcScale(Dimension videoDimension, Dimension containerDimension)
  {
    float ws = (float)containerDimension.width / (float)videoDimension.width;
    BigDecimal bd = new BigDecimal(Float.toString(ws));
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    final float widthScale = bd.floatValue();

    float hs = (float)containerDimension.height / (float)videoDimension.height;
    BigDecimal bd2 = new BigDecimal(Float.toString(hs));
    bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
    final float heightScale = bd2.floatValue();

    return Math.max(widthScale, heightScale);
  }
  
  /** Returns mrls for all files in the videos directory
   *  and files in the corresponding day of week folder.
   *
   *  this function is kinda specific. it's basically the videos 
   *  you want to consider for the playlist. the playlist will call
   *  this method and add any mrls it doesn't have.
   */
  public static Set<String> videoMRLS()
  {
    Set<String> mrls = LocalService.getLocalMRLS("videos/", false);
    switch(LocalDateTime.now().getDayOfWeek())
    {
      case MONDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/monday/", true));
        break;
      case TUESDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/tuesday/", true));
        break;
      case WEDNESDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/wednesday/", true));
        break;
      case THURSDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/thursday/", true));
        break;
      case FRIDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/friday/", true));
        break;
      case SATURDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/saturday/", true));
        break;
      case SUNDAY:
        mrls.addAll(LocalService.getLocalMRLS("videos/sunday/", true));
        break;
      default:
        break;
    }
    return mrls;
  }

  private void log(String message)
  {
    System.out.println("[VideoListPanel] " + message);
  }

}
