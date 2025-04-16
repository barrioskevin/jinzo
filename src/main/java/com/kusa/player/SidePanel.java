package com.kusa.player;

import com.kusa.Config;
import com.kusa.service.LocalService;
import com.kusa.util.PlaylistFile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
//might be able to remove these.
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.imgscalr.Scalr;

/**
 * Class for managing a side panel of an engagment frame.
 *
 * right now this class only works by displaying an image from
 * a path specified in the constructor. in the future i want to extend
 * functionality to allow for creating panels with custom text as well.
 */
public class SidePanel extends JPanel {

  private final int width;
  private final int height;

  private Image image;
  private ImageIcon icon;
  private JLabel imageLabel;

  /**
   * Returns an all black image with the width
   * and height specified.
   * @param width width of all black image.
   * @param height height of all black image.
   */
  public static Image blackImage(int width, int height) {
    Image fallback = new BufferedImage(
      width,
      height,
      BufferedImage.TYPE_INT_ARGB
    );
    Graphics g = fallback.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, width, height);
    g.dispose();
    return fallback;
  }

  public SidePanel(int width, int height) {
    this(blackImage(width, height), width, height);
  }

  /**
   * Constucts a side panel with custom dimensions.
   * we can specify our own width and height any images
   * we set will be scaled to this width and height.
   *
   * @param image image content to be displayed.
   * @param width int representing width of side panel.
   * @param height int representing height of side panel.
   */
  public SidePanel(Image image, int width, int height) {
    this.width = width;
    this.height = height;
    setLayout(new BorderLayout());
    icon = new ImageIcon(image);
    imageLabel = new JLabel(icon);
    imageLabel.setOpaque(true);
    imageLabel.setBackground(Color.BLACK);
    add(imageLabel, BorderLayout.CENTER);
  }

  /**
   * Sets the content displayed on the side panel.
   *
   * if it fails to load the new image it will
   * just stick to the image it currently has.
   *
   * !! any images passed in are scaled to this
   *    panels width and height !!
   *
   * @param newImage image to be set.
   */
  public void setImage(BufferedImage newImage) {
    Image newScaledImage = Scalr.resize(
      newImage,
      Scalr.Method.BALANCED,
      Scalr.Mode.FIT_EXACT,
      width,
      height
    );
    imageLabel.setIcon(new ImageIcon(newScaledImage));
  }

  /**
   * Returns name of current image.
   *
   * TODO fix this!
   *
   * @return this panel's current mrl or empty string (if no image set).
   */
  public String currentMedia() {
    return "WIP";
  }
}
