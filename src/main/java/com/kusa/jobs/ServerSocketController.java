package com.kusa.jobs;

import com.kusa.JinzoApp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * An attempt to control a JinzoApp by accepting commands
 * from a server socket.
 *
 * jinzo app is the engagement player with 2 side panels
 * and the video panel in the middle.
 */
public class ServerSocketController implements Runnable {

  private int port;
  private JinzoApp jinzoApp;

  //should be appframe instead of the panel.
  public ServerSocketController(JinzoApp jinzoApp) {
    this.jinzoApp = jinzoApp;
    this.port = 9999; //hard coded for now.
  }

  @Override
  public void run() {
    try (ServerSocket server = new ServerSocket(this.port)) {
      server.setSoTimeout(1000); //1 sec timeout.
      log(
        String.format("starting local ServerSocket on port %d...\n", this.port)
      );
      while (!Thread.currentThread().isInterrupted()) {
        try {
          Socket client = server.accept();
          BufferedReader br = new BufferedReader(
            new InputStreamReader(client.getInputStream())
          );
          handleCommand(br.readLine(), client);
          client.close();
        } catch (SocketTimeoutException ste) {
          //nothing
        } catch (IOException ioEx) {
          log("failed to communicate with client via buffered writier");
          ioEx.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //add new commands here!
  //
  //  when i say returns i basically mean that
  //  the whatever is being returned is actually
  //  being written to the socket's outputstream.
  //
  //  - status ()
  //  - pause (toggles pause on player.)
  //  - stop (closes player.)
  //  - playing? (returns a string with status on the current playing media.)
  //  - tracklist? (returns the complete video playlist inside jinzo app.)
  //  - left? (returns status about jinzo app left panel.)
  //  - right? (returns status about jinzo app right panel.)
  private void handleCommand(String command, Socket client) throws IOException {
    BufferedWriter writer = new BufferedWriter(
      new OutputStreamWriter(client.getOutputStream())
    );

    log("recieved command (raw): " + command);
    switch (command) {
      case "pause":
      case "PAUSE":
        log("pause command triggered");
        jinzoApp.pause();
        break;
      case "stop":
      case "STOP":
        log("stop command triggered");
        //this eventually stops this runnable also.
        jinzoApp.stop();
        break;
      case "playing?":
      case "PLAYING?":
        log("playing? command triggered");
        writer.write(getPlayingMessage("video"));
        writer.flush();
        break;
      case "tracklist?":
      case "TRACKLIST?":
        log("tracklist? command triggered");
        writer.write(getTracklistMessage());
        writer.flush();
        break;
      case "left?":
        log("left? command triggered");
        writer.write(getPlayingMessage("left"));
        writer.flush();
        break;
      case "right?":
        log("right? command triggered");
        writer.write(getPlayingMessage("right"));
        writer.flush();
        break;
      default:
        log("ignoring command: " + command);
        break;
    }
  }

  private void log(String message) {
    System.out.println("[ServerSocketController] " + message);
  }

  //calls current video on jinzo app and returns
  //something we can send to the buffered writer.
  private String getPlayingMessage(String panel) {
    String media = "n/a";
    if (panel.equals("left")) media = jinzoApp.currentLeftPanelMedia();
    if (panel.equals("right")) media = jinzoApp.currentRightPanelMedia();
    if (panel.equals("video"))
      media = jinzoApp.currentVideoPanelMedia();
    return String.format(
        "Jinzo App's %s panel is currently playing %s\n",
        panel,
        media
    );
  }

  //calls tracklist on jinzo app and returns something
  //we can send to the BufferedWriter.
  private String getTracklistMessage() {
    if (!jinzoApp.isRunning()) return "Jinzo App is not currently running.\n";

    List<String> tracks = jinzoApp.videoPanelTrackList();
    StringBuilder sb = new StringBuilder();
    sb.append("Loading Jinzo App's TrackList...\n");
    sb.append(String.format("Found %d tracks...\n", tracks.size()));
    for (int i = 0; i < tracks.size(); i++) {
      String track = tracks.get(i);
      if (track.equals(jinzoApp.currentVideoPanelMedia())) sb.append("*Playing Now* ");
      sb.append(String.format("[%d] %s\n", i, track));
    }
    return sb.toString();
  }
}
