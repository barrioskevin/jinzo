package com.kusa.players;

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

public class SidePanel extends JPanel
{
  private BufferedImage originalImage;
  private Image scaledImage;
  private ImageIcon icon;
  private JLabel imageLabel;

  //default to 660x1080
  public SidePanel(String path) { this(path, 640, 1080); }
  public SidePanel(String path, int width, int height)
  {
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
