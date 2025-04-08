package com.kusa;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
 *  maybe we can have some sort of specification of playlist inside the config file?
 */
public class Config {

  // this is platform specific. it should work fine on unix
  private static final String appPath =
    System.getProperty("user.home") + "/.jinzo/";
  private static final String cachePath =
    System.getProperty("user.home") + "/.cache/jinzo/";
  private static final String configPath =
    System.getProperty("user.home") + "/.config/jinzo/";
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
        props.load(Config.class.getResourceAsStream("/config"));
        propsLoaded = true;
        System.out.println("[SUCSSES] (local) classpath/config loaded");
      } catch (Exception e) {
        System.out.println("[DEBUG] NO local properties found");
      }

      File configFolder = new File(configPath);
      if (!configFolder.exists()) if (
        !configFolder.mkdirs()
      ) System.out.println(
        "[ERROR] FAILED TO FIND OR CREATE APP'S CONFIG FOLDER."
      );

      if (!propsLoaded) {
        File propertyFile = new File(configPath + "config");
        if (!propertyFile.exists()) {
          System.out.println("creating default config file.");
          Files.createFile(Paths.get(propertyFile.getAbsolutePath()));
          writeDefaultAppProperties(propertyFile);
        }
        props.load(new FileReader(propertyFile));
        System.out.printf(
          "[SUCSSES] %s loaded\n",
          propertyFile.getAbsolutePath()
        );
      }

      System.out.println(
        "[DEBUG] Google credentials found at:" +
        getProperty("googleCredentialsPath")
      );
      System.out.println(
        "[DEBUG] Reading tokens from:" + getProperty("tokenStoragePath")
      );
      System.out.println(
        "[DEBUG] Saving drive downloads to:" + getProperty("downloadPath")
      );

      File driveFolder = new File(getProperty("downloadPath"));
      if (!driveFolder.exists()) if (!driveFolder.mkdirs()) System.out.println(
        "FAILED TO FIND OR CREATE DRIVE FOLDER."
      );

      File tokenFolder = new File(getProperty("tokenStoragePath"));
      if (!tokenFolder.exists()) if (!tokenFolder.mkdirs()) System.out.println(
        "FAILED TO FIND OR CREATE TOKEN FOLDER."
      );

      File appCreds = new File(props.getProperty("googleCredentialsPath"));
      if (!appCreds.exists()) {
        System.out.println(
          "WARNING!!! NO APP CREDENTIALS FOUND: " +
          props.getProperty("googleCredentialsPath") +
          " (DRIVE SERVICES WILL NOT WORK)!"
        );
      }

      String full = props.getProperty("playlists");
      String[] playlistFileNames = full.split(",");
      if (playlistFileNames.length > 0) System.out.printf(
        "[DEBUG] Found %d playlists.\n",
        playlistFileNames.length
      );
      else System.out.printf("[ERROR] NO PLAYLIST FILES FOUND!\n");

      for (String name : playlistFileNames) {
        final String playlistPath = name.replace("'", "").trim();
        File playlistFile = new File(playlistPath);
        if (!playlistFile.exists()) {
          if (!playlistFile.createNewFile()) System.out.println(
            "Failed to find or create playlist " + playlistPath
          );
          else {
            if (name.contains("video")) writeDefaultPlaylist(playlistFile, 0);
            else writeDefaultPlaylist(playlistFile, 1);
          }
        }
      }
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

  /**
   * Returns array of all the loaded playlist files.
   *
   */
  public static String[] playlistFiles() {
    if (
      props == null || props.getProperty("playlists") == null
    ) return new String[0];

    String full = props.getProperty("playlists");
    String[] playlistFileNames = full.split(",");
    return Arrays.stream(playlistFileNames)
      .map(fileName -> fileName.replace("'", "").trim())
      .toArray(String[]::new);
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
    final String tokenStorage = "tokenStoragePath=" + cachePath + "tokens/";
    final String googleCreds =
      "googleCredentialsPath=" + configPath + "credentials.json";
    final String downloadPath = "downloadPath=" + cachePath + "drive/";
    final String defP1 = String.format(
      "'%splaylists/videopanel.playlist'",
      configPath
    );
    final String defP2 = String.format(
      "'%splaylists/leftpanel.playlist'",
      configPath
    );
    final String defP3 = String.format(
      "'%splaylists/rightpanel.playlist'",
      configPath
    );
    final String playlists = String.format(
      "playlists=%s, %s, %s",
      defP1,
      defP2,
      defP3
    );
    List<String> properties = List.of(
      tokenStorage,
      googleCreds,
      downloadPath,
      playlists
    );
    try {
      Files.write(Paths.get(file.getAbsolutePath()), properties);
    } catch (IOException ioException) {
      throw ioException;
    }
  }

  //defId
  //0 = default to all videos
  //1 = default to all photos
  private static void writeDefaultPlaylist(File file, int defId) {
    String include = "";
    switch (defId) {
      case 0:
        include = "$videos";
        break;
      case 1:
        include = "$photos";
        break;
      default:
        include = "";
        break;
    }
    List<String> sections = List.of(
      "[videos]",
      System.getProperty("user.home") + "/Videos/*",
      "[photos]",
      System.getProperty("user.home") + "/Photos/*",
      String.format("[monday]\n%s", include),
      String.format("[tuesday]\n%s", include),
      String.format("[wednesday]\n%s", include),
      String.format("[thursday]\n%s", include),
      String.format("[friday]\n%s", include),
      String.format("[saturday]\n%s", include),
      String.format("[sunday]\n%s", include)
    );
    try {
      Files.write(Paths.get(file.getAbsolutePath()), sections);
    } catch (IOException ioException) {
      System.out.println("couldn't write default playlist to file.");
      ioException.printStackTrace();
    }
  }
}
