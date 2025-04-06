package com.kusa.player;

//panel reference
import javax.swing.JPanel;

//retreiving videos
import com.kusa.Config;
import com.kusa.service.LocalService;
import com.kusa.util.PlaylistFile;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

//for calculatinng scale
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;

//exceptions ?!
import java.io.IOException;

public interface VideoPanel {

  /**
   * Will return the latest videos based on the configured playlist file.
   *
   * the playlist file is what makes up where we grab the videos from.
   *  see PlaylistFile.latest()
   *
   * the file seperates content into sections, there are special sections
   * corresponding to the days of the week which will handle playing
   * specific videos on specific days.
   *
   * @return set of video paths from latest playlist or empty if playlist failed to read.
   */
  public static Set<String> videoMRLS()
  {
    try {
      //Config.playlistFiles()[0]
      // could be a property to have panel choose a playlist
      PlaylistFile myPlaylist = new PlaylistFile(Config.playlistFiles()[0]);
      return new HashSet<String>(myPlaylist.latest().trackList());
    } catch (IOException ioEx) {
      ioEx.printStackTrace();
    } catch (Exception x) {
      //out of bounds execption.
      //maybe default to other existing playlists?
      x.printStackTrace();
    }
    return new HashSet<String>();
  }

  /**
   * Attempts to find a correct scale to ensure the container will
   * be filled with video content.
   *
   * either stretches until ends touch the horizontal border
   * or until the ends touch the vertical border
   *
   * !! a high scale is likely to crop and zoom the video. !!
   *
   * @param videoDimension the dimensions of video that needs to be scaled.
   * @param containerDimension the dimensions of container we want to fit video in.
   *
   * @return float - scale needed to zoom video to fit container's dimensions.
   */
  public static float calcScale(Dimension videoDimension, Dimension containerDimension)
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


  void start();
  void stop();
  void shutdown();
  JPanel panel();
}
