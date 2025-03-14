package com.kusa.player;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
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


/**
 * Class representing the video content in an engagment frame.
 *
 * this class extends vlcjs embedded media player component for
 * access to vlc bindings.
 */
public class VideoPanel extends EmbeddedMediaPlayerComponent 
{
  private Playlist playlist;

  //gds is injected to the video panel but currently we don't use it.
  private GDriveService gds;
  
  /**
   * Constructs a video panel for use in an engagment frame.
   *
   * @param playlist_ the playlist dictating the media panel will play.
   * @param gds_ the apps google drive service.
   */
  public VideoPanel(Playlist playlist_, GDriveService gds_)
  {
    super();
    playlist = playlist_;
    gds = gds_;

    setOpaque(true); //maybe remove?

    //we setup a event adapter to automatically play the next video in a playlist
    //once a video ends.
    mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
      @Override
      public void finished(MediaPlayer mp)
      {
        if(playlist.isEmpty())
          System.out.println("WARNING THE VIDEO PLAYER CURRENTLY HAS AN EMPTY PLAYLIST");

        System.out.println("A VIDEO FINISHED.");
        Set<String> driveMrls = new HashSet<>();

        boolean willShuffle = false;
        if(playlist.index() == 0)
        {
          System.out.println("PLAYLIST OVER CLEARING THE PLAYLIST");
          playlist.clear();
          willShuffle = true;
        }

        Set<String> mrls = videoMRLS();
        Set<String> currentTracks = new HashSet<>(playlist.trackList());
        for(String video : mrls)
        {
          if(!currentTracks.contains(video))
          {
            System.out.println("ADDING NEW " + video + " TO PLAYLIST");
            playlist.add(video);
          }
        }

        if(willShuffle)
        {
          System.out.println("SHUFFLING PLAYLIST");
          playlist.shuffle();

          System.out.println("PLAYING NEW PLAYLIST! INDEX: " + playlist.index());
          List<String> tracks = playlist.trackList();
          for(int i = 0; i < tracks.size(); i++)
            System.out.println(" " + i + tracks.get(i));
        }

        final int idx = playlist.index();
        final String mrl = playlist.next();

        mediaPlayer().submit(() -> mediaPlayer().media().play(mrl, "--avcodec-hw=mmal", "--no-xlib", "--no-osd", "--no-interact", "--no-video-filter", "--quiet"));
        System.out.printf("NOW PLAYING %d : %s \n", idx, mrl);
        System.out.printf("NEXT INDEX %d\n", playlist.index());
      }
    });
  }

  /**
   * Advances the playlist and plays the next media.
   * expects the media player to be <strong>Stopped.</strong>
   *
   * called by the main app to start the video panel. 
   *
   * because the video panel takes care of starting new videos 
   * after one ends this method should only be called once 
   * after the engagment frame has been created.
   */
  public void play()
  {
    final int idx = playlist.index();
    final String mrl = playlist.next();
    mediaPlayer().media().play(mrl);
    System.out.printf("NOW STARTING %d : %s \n", idx, mrl);
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

}
