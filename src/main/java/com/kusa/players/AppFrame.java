package com.kusa.players;

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
 * This class is the heart of the application it will be what is running on the applications
 * window.
 */
public class AppFrame extends JFrame
{

  /**
   * GraphicsDevice of the screen that the device is running on.
   *
   * might not work for all enviornments. but we define the screen 
   * so that we can make our app go into full screen mode after launching.
   */
  static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

  private SidePanel leftPanel;
  private VideoPanel middlePanel;
  private SidePanel rightPanel;
  private JPanel contentPane;

  /**
   * Constructs the engagment frame.
   * 
   * While right now this engagment frame is handling alot I eventually want to add
   * paramters for this constructor insttead of internally creating the panels.
   *
   * Operations:
   *  - Constructs needed panels. 2 side panels and 1 video panel. 
   *  - Constructs the content pane where panels will be added.
   *  - sets frame properties such as underorated and visible to true.
   *  - uses the GraphicsDevice to enable full screen on this frame.
   *  - starts the video panel.
   */
  public AppFrame()
  {
    leftPanel = new SidePanel(Config.getProperty("downloadPath") + "photos/logo-stack.png");
    rightPanel = new SidePanel(Config.getProperty("downloadPath") + "photos/logo-stack.png");
    middlePanel = new VideoPanel();

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

    //contentPane.setLayout(new GridLayout(1, 3, 0, 0));
    contentPane.setLayout(new BorderLayout());

    contentPane.add(leftPanel, BorderLayout.WEST);
    contentPane.add(middlePanel, BorderLayout.CENTER);
    contentPane.add(rightPanel, BorderLayout.EAST);

    setContentPane(contentPane);
    setVisible(true);
      
    device.setFullScreenWindow(this);
    middlePanel.play();

    /*
    System.out.println("VIDEO FRAME CREATED, SHOWING CONTENT");
    System.out.println("Left Panel" + leftPanel.getSize());
    System.out.println("video " + middlePanel.getSize());
    System.out.println("Right Panel" + rightPanel.getSize());
    */
  }
}
