package com.kusa;

import com.kusa.service.GDriveService;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

    try(ServerSocket server = new ServerSocket(9999))
    {
      System.out.println("[SERVER] starting local server...");
      while(true) {
        Socket client = server.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String command = br.readLine();
        System.out.println("command: " + command);
        if(command.equals("pause"))
          VlcjApp.pause();
        client.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
