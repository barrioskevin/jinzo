package com.kusa.player;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.BorderLayout;

import java.io.File;
import javax.imageio.ImageIO;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ImageIcon;

//might be able to remove these.
import javax.swing.BorderFactory;
import java.awt.GridLayout;

/**
 * Class for managing a side panel of an engagment frame.
 *
 * right now this class only works by displaying an image from
 * a path specified in the constructor. in the future i want to extend
 * functionality to allow for creating panels with custom text as well.
 */
public class SidePanel extends JPanel
{
  private BufferedImage originalImage;
  private Image scaledImage;
  private ImageIcon icon;
  private JLabel imageLabel;

  /**
   * Constructs a side panel with the default dimensions.
   * default dimensions attempt to take up 1/3 of the 1080p display.
   * @param path path of image content to be displayed.
   */ 
  public SidePanel(String path) { this(path, 640, 1080); }
  /**
   * Constucts a side panel with custom dimensions.
   * we can specify our own width and height but it's up to the engament frame to 
   * determine how it will be displayed in the end.
   * @param path path of image content to be displayed.
   * @param width int representing width of side panel.
   * @param height int representing height of side panel.
   */
  public SidePanel(String path, int width, int height)
  {
    //TODO Handle failure properly!!!
      
    setLayout(new BorderLayout());

    try{
      originalImage = ImageIO.read(new File(path));
      scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    }catch(Exception e)
    {
      System.out.println("IMAGE " + path + " FAILED TO LOAD");
      scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics g = scaledImage.getGraphics();
      g.setColor(Color.BLUE);
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
  public void setImage(String path)
  {
    BufferedImage newImage;
    Image newScaledImage;
    int width = imageLabel.getIcon().getIconWidth(); 
    int height = imageLabel.getIcon().getIconHeight(); 
    try{
      newImage = ImageIO.read(new File(path));
      newScaledImage = newImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
      ImageIcon newIcon = new ImageIcon(newScaledImage);
      imageLabel.setIcon(newIcon);
    }catch(Exception e)
    {
      System.out.println("FAILED TO SET A NEW IMAGE");
    }
    return;
  }

}
