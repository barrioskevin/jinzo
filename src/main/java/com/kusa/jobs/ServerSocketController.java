package com.kusa.jobs;

import com.kusa.JinzoApp;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

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
          handleCommand(br.readLine());
          client.close();
        } catch (SocketTimeoutException ste) {}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //add new commands here!
  //
  //  - status ()
  //  - pause (toggles pause on player.)
  private void handleCommand(String command) {
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
      default:
        log("ignoring command: " + command);
        break;
    }
  }

  private void log(String message) {
    System.out.println("[ServerSocketController] " + message);
  }
}
