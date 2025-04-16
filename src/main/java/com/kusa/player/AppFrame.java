package com.kusa.player;

import com.kusa.service.LocalService;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Class representing the main frame.
 * aka: EngagmentFrame (will rename soon).
 *
 */
public class AppFrame extends JFrame {

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
  public AppFrame(
    SidePanel leftPanel,
    SidePanel rightPanel,
    VideoPanel middlePanel
  ) {
    this.leftPanel = leftPanel;
    this.rightPanel = rightPanel;
    this.middlePanel = middlePanel;

    contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());

    contentPane.add(leftPanel, BorderLayout.WEST);
    contentPane.add(middlePanel.panel(), BorderLayout.CENTER);
    contentPane.add(rightPanel, BorderLayout.EAST);

    setContentPane(contentPane);

    setUndecorated(true);
    setVisible(true);
    setFocusable(true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    requestFocusInWindow();

    addWindowListener(
      new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          shutdown();
        }

        @Override
        public void windowActivated(WindowEvent e) {
          requestFocusInWindow();
        }
      }
    );

    //might not need but added cus sometimes the key adapter wouldnt work.
    addFocusListener(
      new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
          requestFocusInWindow();
        }
      }
    );
  }

  /**
   * Calls shutdown implementation on frames video panel.
   *
   * we do this internally on a window closed event.
   *
   * made this private so i dont try to shutdown more than once
   * (will segfault due to video panel's shutdown method).
   *
   * could perform other shutdown related things here so long
   * as they are related to this frame in particular.
   *
   * other shutdown related things should be placed in the added
   * key adapter where we listen for the ESC key to quit.
   */
  private void shutdown() {
    middlePanel.shutdown();
  }

  /**
   * Toggles this frame to be full screen on systems main device.
   *
   * relies on local service for device reference.
   *
   * displays this frame on full screen or returns to windowed.
   */
  public void fullscreen() {
    LocalService.device.setFullScreenWindow(this);
  }
}
