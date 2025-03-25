package com.kusa.player;

import com.kusa.Config;
import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Class representing the main frame.
 * aka: EngagmentFrame (will rename soon).
 *
 */
public class AppFrame extends JFrame
{
  /**
   * GraphicsDevice of the screen that the device is running on.
   *
   * might not work for all enviornments. but we define the screen 
   * so that we can make our app go into full screen mode after launching.
   */
  public static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

  private SidePanel leftPanel;
  private VideoPanel middlePanel;
  private SidePanel rightPanel;
  private JPanel contentPane;

  /**
   * Constructs the engagment frame from two side panels and a video panel.
   * 
   * Operations:
   *  - sets the private variables.
   *  - Constructs the content pane where panels will be added.
   *  - add in all the panels to this engagment frames layout.
   *  - sets frame properties such as undecorated and visible to true.
   */
  public AppFrame(SidePanel leftPanel, SidePanel rightPanel, VideoPanel middlePanel)
  {
    this.leftPanel = leftPanel;
    this.rightPanel = rightPanel;
    this.middlePanel = middlePanel;

    setUndecorated(true);

    addWindowListener(new WindowAdapter() {
      @Override 
      public void windowClosing(WindowEvent e)
      {
        middlePanel.release();
        System.exit(0); //app quits when main frame is closed.
      }
    });

    contentPane = new JPanel();

    contentPane.setLayout(new BorderLayout());

    contentPane.add(leftPanel, BorderLayout.WEST);
    contentPane.add(middlePanel, BorderLayout.CENTER);
    contentPane.add(rightPanel, BorderLayout.EAST);

    setContentPane(contentPane);
    setVisible(true);
  }
  
  /** Toggles full screen on systems main device.
   *
   * displays this frame on full screen or returns to windowed.
   */
  public void fullscreen(){ device.setFullScreenWindow(this); }
}
