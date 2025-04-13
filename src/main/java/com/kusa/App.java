package com.kusa;

import com.kusa.service.GDriveService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Main class.
 *
 * there is sort of a "hidden" startup that happens in Config.java
 * where the internal properties are loaded in.
 *
 * In this class we just initialize drive service, build
 * the engagement frame and run it.
 */
public class App {

  public static void main(String args[]) {
    //init deps
    GDriveService gds = new GDriveService();

    //initial downloads.
    if (gds.isValid()) gds.downloadMedia();

    VlcjApp.exec(gds);

    try (ServerSocket server = new ServerSocket(9999)) {
      server.setSoTimeout(1000); //1 sec timeout.
      System.out.println("[SERVER] starting local server...");
      while (VlcjApp.running) {
        try {
          Socket client = server.accept();
          BufferedReader br = new BufferedReader(
            new InputStreamReader(client.getInputStream())
          );
          String command = br.readLine();
          System.out.println("command: " + command);
          if (command.equals("pause")) VlcjApp.pause();
          client.close();
        } catch (SocketTimeoutException ste) {}
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
