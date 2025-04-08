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

  private BufferedImage originalImage;
  private Image scaledImage;
  private ImageIcon icon;
  private JLabel imageLabel;

  /**
   * Constructs a side panel with the default dimensions.
   * default dimensions attempt to take up 1/3 of the 1080p display.
   * be careful if your display is not 1080p the images might not show up as expected.
   * @param path path of image content to be displayed.
   */
  public SidePanel(String path) {
    this(path, 640, 1080);
  }

  /**
   * Constucts a side panel with custom dimensions.
   * we can specify our own width and height but it's up to the engament frame to
   * determine how it will be displayed in the end.
   * (this might not be true. the size should probably be set HERE)
   * @param path path of image content to be displayed.
   * @param width int representing width of side panel.
   * @param height int representing height of side panel.
   */
  public SidePanel(String path, int width, int height) {
    //layout (border layout might not be best)
    setLayout(new BorderLayout());

    /*
     * LOADING IMAGE.
     *
     * if we fail to read the image this panel will just be black.
     * im trying to come up with better ways to scale the image so that the side panels image still
     * fits correctly on different resolutions. previously i had used get scaled instance, but it
     * didn't seem to work as nicely when displaying on TVs with high res. I think it could've been
     * a bug that made the images not look as nice, i was passing in the screens width for both width and height
     * so although the bug is gone our new solutions utilizes Scalr library to scale based on the width which
     * should be 1/3 of the display. Im currently testing out better methods to scale the image.
     *
     * previous scaling code: scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
     */
    try {
      originalImage = ImageIO.read(new File(path));
      scaledImage = Scalr.resize(
        originalImage,
        Scalr.Method.BALANCED,
        Scalr.Mode.FIT_EXACT,
        width,
        height
      );
    } catch (Exception e) {
      System.out.println("[SidePanel] IMAGE " + path + " FAILED TO LOAD");
      scaledImage = new BufferedImage(
        width,
        height,
        BufferedImage.TYPE_INT_ARGB
      );
      Graphics g = scaledImage.getGraphics();
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, width, height);
      g.dispose();
    }

    icon = new ImageIcon(scaledImage);
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
   * we have a Task for using this function i just havent
   * found the best place to use it.
   *
   * @param path path of new image to be displayed.
   */
  public void setImage(String path) {
    BufferedImage newImage;
    Image newScaledImage;
    int width = imageLabel.getIcon().getIconWidth();
    int height = imageLabel.getIcon().getIconHeight();
    try {
      newImage = ImageIO.read(new File(path));
      newScaledImage = Scalr.resize(
        newImage,
        Scalr.Method.BALANCED,
        Scalr.Mode.FIT_EXACT,
        width,
        height
      );
      //newScaledImage = Scalr.resize(newImage, Scalr.Method.BALANCED, Scalr.Mode.FIT_TO_WIDTH, width);
      ImageIcon newIcon = new ImageIcon(newScaledImage);
      imageLabel.setIcon(newIcon);
    } catch (Exception e) {
      System.out.println("[SidePanel] FAILED TO SET NEW IMAGE: " + path);
    }
  }

  /**
   * Returns set of latest photo mrls based on the configured playlist.
   *
   * @param leftPanel true if we are reading the left panels playlist.
   * @return set of mrls representing latest photos of playlist or empty if playlist file failed to read.
   */
  public static Set<String> photoMRLS(boolean leftPanel) {
    try {
      String filePath = (leftPanel)
        ? Config.playlistFiles()[1]
        : Config.playlistFiles()[2];
      PlaylistFile playlistFile = new PlaylistFile(filePath);
      return new HashSet<String>(playlistFile.latest().trackList());
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } catch (Exception ex) {
      //out of bounds maybe.
      ex.printStackTrace();
    }
    return new HashSet<String>();
  }
}
