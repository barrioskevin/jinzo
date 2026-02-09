package com.kusa;

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
    new JinzoApp().run();
  }
}
