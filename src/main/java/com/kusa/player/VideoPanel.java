package com.kusa.player;

//panel reference
import javax.swing.JPanel;

//retreiving videos
import com.kusa.service.LocalService;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

//for calculatinng scale
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;

public interface VideoPanel {

  /** 
   *  Returns paths of all videos to play.
   *
   *  returs all files in videos and checks day of week.
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

  //attempts to find a correct scale to ensure the container
  //will be filled with video content.
  //
  //this is likely to crop and zoom the video.
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
