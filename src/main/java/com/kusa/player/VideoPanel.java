package com.kusa.player;

//for calculatinng scale
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;
//util
import java.util.List;
//panel reference
import javax.swing.JPanel;

public interface VideoPanel {
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
  public static float calcScale(
    Dimension videoDimension,
    Dimension containerDimension
  ) {
    float ws = (float) containerDimension.width / (float) videoDimension.width;
    BigDecimal bd = new BigDecimal(Float.toString(ws));
    bd = bd.setScale(2, RoundingMode.HALF_UP);
    final float widthScale = bd.floatValue();

    float hs =
      (float) containerDimension.height / (float) videoDimension.height;
    BigDecimal bd2 = new BigDecimal(Float.toString(hs));
    bd2 = bd2.setScale(2, RoundingMode.HALF_UP);
    final float heightScale = bd2.floatValue();

    return Math.max(widthScale, heightScale);
  }

  //controls
  void start();
  void stop();
  void pause();
  void restart();
  void shutdown();

  //info
  boolean isPlaying();
  String currentMedia();
  List<String> tracks();

  //references
  JPanel panel();
}
