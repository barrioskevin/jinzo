package com.kusa;
import com.kusa.player.AppFrame;
import com.kusa.player.SidePanel;
import com.kusa.player.VideoPanel;

import com.kusa.playlist.Playlist;
import com.kusa.playlist.CircularQueuePlaylist;

import com.kusa.service.GDriveService;
import com.kusa.service.LocalService;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import java.time.LocalDateTime;
import java.time.DayOfWeek;

/**
 * Main class.
 *
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 * 
 * In this class we just initialize drive service, build
 * the engagement frame and play it.
 */
public class App 
{
  public static void main(String args[])
  {
    //initial sync.
    GDriveService gds = new GDriveService();
    if(gds.isValid())
      gds.sync();

    //initial playlists.
    CircularQueuePlaylist leftPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(LocalService.getLocalMRLS("photos/left/", true)));
    CircularQueuePlaylist rightPanelPlaylist = new CircularQueuePlaylist(new ArrayList<String>(LocalService.getLocalMRLS("photos/right/", true)));
    CircularQueuePlaylist videoPlaylist = new CircularQueuePlaylist(new ArrayList<String>(videoMRLS()));

    //panels.
    SidePanel left = new SidePanel(leftPanelPlaylist.current());
    SidePanel right = new SidePanel(rightPanelPlaylist.current());
    VideoPanel middle = new VideoPanel(videoPlaylist);

    //schedule poll and sync task.
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run()
      {
        //calls to is valid will
        //attempt to revalidate the drive service.
        if(gds.isValid())
          if(!gds.sync())
            return;

        List<Playlist> playlists = List.of(leftPanelPlaylist, rightPanelPlaylist, videoPlaylist);
        for(int i = 0; i < playlists.size(); i++)
        {
          Playlist playlist = playlists.get(i);
          Set<String> mrls = new HashSet<>();
          if(i == 0)
            mrls.addAll(LocalService.getLocalMRLS("photos/left/", true));
          if(i == 1)
            mrls.addAll(LocalService.getLocalMRLS("photos/right/", true));
          if(i == 2)
            mrls.addAll(videoMRLS());

          if(mrls.isEmpty())
            continue;

          //save index
          final int idx = playlist.index();

          //rebuild to account for new changes.
          playlist.clear();
          for(String mrl : mrls)
            playlist.add(mrl);

          playlist.skipTo(idx % playlist.size());
        }
      }
    }, 10000, 60000);

    Timer timer2 = new Timer();
    timer2.schedule(new TimerTask() {
      @Override
      public void run()
      {
        left.setImage(leftPanelPlaylist.next());
        right.setImage(rightPanelPlaylist.next());
      }
    }, 60000, 60000);

    //create and play engagement frame.
    AppFrame engagementFrame = new AppFrame(left, right, middle);
    engagementFrame.fullscreen();
    middle.play();
  }

  //returns mrls for all files in the videos directory
  //and files in the corresponding day of week folder.
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
