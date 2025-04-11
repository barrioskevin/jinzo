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
 * <p> An attempt to manage the internal directories seamlessly.
 *
 * <p>.cache/jinzo/ - where the app will place google drive downloads by default.
 *                        auth tokens are also stored here.
 *
 * <p>.config/jinzo/ - where the app will place configuration file by default.
 *                        ".playlist" files are also stored here.
 *
 * -- roadmap ---
 *  I need to make sure that the properites file loaded includes all the needed properites.
 *
 *    Service related hm...
 *    # token storage path - (if using google drive api)
 *      - (.cache/jinzo/tokens/)
 *    # app credentials.json - (if using google drive api)
 *      - (.config/jinzo/credentials.json)
 *    # local map of google drive path - (where downloaded drive files go)
 *      - (.cache/jinzo/drive/)
 */
public class Config {

  // this is platform specific. it should work fine on unix-like systems.
  private static final String cachePath =
    System.getProperty("user.home") + "/.cache/jinzo/";
  private static final String configPath =
    System.getProperty("user.home") + "/.config/jinzo/";
  private static Properties props;

  /*
   * This method will fire at an early stage of programs execution.
   *
   * we try to load in the application properties as well as verify that all needed directories
   * exist.
   *
   * the app first attempts to read a config from the classpath,
   *   this is for development purposes (and i should probably remove).
   *
   * when it doesn't find anything in cp the app looks for a config file in /.config/jinzo/
   *
   * if any exceptions occur when trying to read or create files/directories the app should crash.
   *
   * ---features---
   * if certain properties for services are not found a warning should appear with steps on how
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
      File playlistsFolder = new File(configPath + "playlists");
      if (!playlistsFolder.exists()) if (
        !playlistsFolder.mkdirs()
      ) System.out.println(
        "[ERROR] FAILED TO FIND OR CREATE APP'S PLAYLISTS FOLDER."
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
        "[ERROR] FAILED TO FIND OR CREATE DRIVE FOLDER."
      );

      File tokenFolder = new File(getProperty("tokenStoragePath"));
      if (!tokenFolder.exists()) if (!tokenFolder.mkdirs()) System.out.println(
        "[ERROR] FAILED TO FIND OR CREATE TOKEN FOLDER."
      );

      File appCreds = new File(props.getProperty("googleCredentialsPath"));
      if (!appCreds.exists()) {
        System.out.println(
          "[WARNING] NO APP CREDENTIALS.json FOUND: " +
          props.getProperty("googleCredentialsPath") +
          " (DRIVE SERVICES WILL NOT WORK)!"
        );
      }

      String full = props.getProperty("playlists");
      String[] playlistFileNames = full.split(",");
      if (playlistFileNames.length > 0) System.out.printf(
        "[DEBUG] Found %d playlists to load.\n",
        playlistFileNames.length
      );
      else System.out.printf("[ERROR] NO PLAYLIST FILES FOUND!\n");

      for (String name : playlistFileNames) {
        final String playlistPath = name.replace("'", "").trim();
        File playlistFile = new File(playlistPath);
        if (!playlistFile.exists()) {
          if (!playlistFile.createNewFile()) System.out.println(
            "[ERROR] Failed to find or create playlist " + playlistPath
          );
          else {
            if (name.contains("video")) writeDefaultPlaylist(playlistFile, 0);
            else writeDefaultPlaylist(playlistFile, 1);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("[ERROR] STARTUP FAILED!" + e);
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Returns the value of the property given the property name.
   *
   * <p>currently the app only has 4 valid properties
   *  - tokenStoragePath
   *  - googleCredentialsPath
   *  - downloadPath
   *  - playlists
   *
   * <p> these properties are mostly related to google drive and that class
   *      already handles getting the properties it needs.
   *
   * <p> if you want to get a list of the playlist files you playlistFiles()
   *      instead. if you call getProperty("playlists") you will get a single string,
   *      that needs further processing to identify each playlist.
   */
  public static String getProperty(String propName) {
    return ((props == null) ? "" : props.getProperty(propName));
  }

  /**
   * Returns array of all the loaded playlist files.
   *
   * the files are on the system
   * (/home/user/.config/jinzo/playlists/)
   *
   * @return string array of all playlist file's absolute paths.
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
   * writing the default config file when none is found. (usually on first install)
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
        include = String.format("$local-videos\n$drive-videos");
        break;
      case 1:
        include = String.format("$local-photos\n$drive-photos");
        break;
      default:
        include = "";
        break;
    }
    List<String> sections = List.of(
      "[local-videos]",
      System.getProperty("user.home") + "/Videos/**",
      "[drive-videos]",
      cachePath + "drive/videos/**",
      "[local-photos]",
      System.getProperty("user.home") + "/Photos/**",
      "[drive-photos]",
      cachePath + "drive/photos/**",
      "",
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
