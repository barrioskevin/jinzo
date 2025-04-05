package com.kusa;

import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Class for managing the applications configuration.
 *
 * <p>an attempt to manage the internal directories seamlessly
 *
 * <p>appPath - where the app will "install" currently platform specific might not work on windows.
 *
 * -- roadmap ---
 *  I need to make sure that the properites file loaded includes all the needed properites.
 *
 *    Service related hm...
 *    # token storage path - (if using google drive api)
 *    # app credentials.json - (if using google drive api)
 *    # local map of google drive path - (where downloaded drive files go)
 *
 *    Panels related. (telling what media to grab)
 *    # location - sub directory to search when grabbing photos.
 *
 *  maybe we can have some sort of specification of playlist inside the config file?
 */
public class Config {
  // this is platform specific. it should work fine on unix
  private static final String appPath = System.getProperty("user.home") + "/.jinzo/";
  private static Properties props;

  /**
   * Hidden "Start Up".
   *
   * <p>this method will fire at the start of programs execution.
   *
   * <p>we try to load in the application properties as well as verify that all needed directories
   * exist.
   *
   * <p>we check for a relative application.properties first meaning if one exists in the
   * src/main/resources folder it will be used. However, the main appPath will still be created!
   *
   * <p>if any exceptions occur when trying to read or create files/directories that app will crash!
   *
   * <p>if certain properties for services are not found a warning should appear with steps on how
   * to resolve it, and what service cannot be used.
   */
  static {
    try {
      boolean propsLoaded = false;

      props = new Properties();
      try {
        // when developing you can have a defined properties file in the resources folder.
        // it requires recompilation upon editing.
        props.load(Config.class.getResourceAsStream("/application.properties"));
        propsLoaded = true;
        System.out.println("Loaded local properties.");
      } catch (Exception e) {
        System.out.println("Couldnt find local properties");
      }

      File appFolder = new File(appPath);
      if (!appFolder.exists())
        if (!appFolder.mkdirs()) System.out.println("FAILED TO FIND OR CREATE APP FOLDER.");

      if (!propsLoaded) {
        File propertyFile = new File(appPath + "application.properties");
        if (!propertyFile.exists()) {
          System.out.println("writing a default properties file.");
          Files.createFile(Paths.get(propertyFile.getAbsolutePath()));
          writeDefaultAppProperties(propertyFile);
        }
        props.load(new FileReader(propertyFile));
        System.out.printf(
            "[SUCSSESS] loaded properties file: %s\n", propertyFile.getAbsolutePath());
      }

      System.out.println("Using download path:" + getProperty("downloadPath"));
      System.out.println("Using token path:" + getProperty("tokenStoragePath"));
      System.out.println("Using google credential path:" + getProperty("googleCredentialsPath"));
      System.out.println("Using location:" + getProperty("location"));

      File driveFolder = new File(getProperty("downloadPath"));
      if (!driveFolder.exists())
        if (!driveFolder.mkdirs()) System.out.println("FAILED TO FIND OR CREATE DRIVE FOLDER.");

      File tokenFolder = new File(getProperty("tokenStoragePath"));
      if (!tokenFolder.exists())
        if (!tokenFolder.mkdirs()) System.out.println("FAILED TO FIND OR CREATE TOKEN FOLDER.");

      File appCreds = new File(props.getProperty("googleCredentialsPath"));
      if (!appCreds.exists()) {
        System.out.println(
            "WARNING!!! NO APP CREDENTIALS FOUND: "
                + props.getProperty("googleCredentialsPath")
                + " (DRIVE SERVICES WILL NOT WORK)!");
      }

      File playlist1 = new File(props.getProperty("playlist1"));
      if (!playlist1.exists())
        if (!playlist1.createNewFile())
          System.out.println("Failed to find or create playlist 1");

      File playlist2 = new File(props.getProperty("playlist2"));
      if (!playlist2.exists())
        if (!playlist2.createNewFile())
          System.out.println("Failed to find or create playlist 2");

      File playlist3 = new File(props.getProperty("playlist3"));
      if (!playlist3.exists())
        if (!playlist3.createNewFile())
          System.out.println("Failed to find or create playlist 3");


    } catch (Exception e) {
      System.out.println("STARTUP FAILED!" + e);
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Returns the value of the property given the property name.
   *
   * <p>currently the app only has 3 valid properties - tokenStoragePath - googleCredentialsPath -
   * downloadPath
   *
   * <p>if the amount of properties remains small we can consider making them all getters instead of
   * a generic get property.
   */
  public static String getProperty(String propName) {
    return ((props == null) ? "" : props.getProperty(propName));
  }

  /*
   * When the app is first installing it will look for appliaction.properties in the
   * class path. if its not found it will look in our main install folder .jinzo/
   * if its not found there the app will attempt to create it and fill it with the
   * default properties specified below.
   *
   * ./jinzo
   *  - /drive/ <- main media folder.
   *  - /tokens/ <- drive users access tokens.
   *  - credentials.json <- client credentials needed to connect to google services.
   *
   * credentials.json can NOT be created for you. it will only define the property
   * with the value of it's default location.
   */
  private static void writeDefaultAppProperties(File file) throws IOException {
    final String tokenStorage = "tokenStoragePath=" + appPath + "tokens/";
    final String googleCreds = "googleCredentialsPath=" + appPath + "credentials.json";
    final String downloadPath = "downloadPath=" + appPath + "drive/";
    final String locationDir = "location=";
    final String p1 = "playlist1=" + appPath + "videopanel.playlist";
    final String p2 = "playlist2=" + appPath + "leftpanel.playlist";
    final String p3 = "playlist3=" + appPath + "rightpanel.playlist";
    List<String> properties = List.of(
        tokenStorage,
        googleCreds,
        downloadPath,
        locationDir,
        p1, p2, p3
    );
    try {
      Files.write(Paths.get(file.getAbsolutePath()), properties);
    } catch (IOException ioException) {
      throw ioException;
    }
  }
}
