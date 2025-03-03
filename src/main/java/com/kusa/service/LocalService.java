package com.kusa.service;
import com.kusa.Config;
import java.io.File;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;


public class LocalService
{

  //aliases
  public static final String videosPath = Config.getProperty("downloadPath") + "videos/";
  public static final String photosPath = Config.getProperty("downloadPath") + "photos/";

  /**
   * Returns true if file exists on the local machine.
   *
   * @param fullName full path of file being checked.
   */
  public static boolean fileExists(String fullName)
  {
    File file = new File(fullName);
    return file.exists();
  } 

  /** returns true if directory is valid.
   * this method will attempt to create the directory if
   * it doesn't exist.
   * returns false if directory isn't usable.
   */
  public static boolean checkDir(String path)
  {
    File dir = new File(path);
    if(!dir.exists())
      return dir.mkdirs();
    return true;
  }

  /**
   * Returns set of paths for files in app folder to be used as MRLS in vlc.
   *
   * remember, the root is always the download path specified in the properties file.
   *
   * if no parameters are passed it will get all files recursivley starting from root.
   *
   * @param nestedFolders a nested path you wanna narrow the search too. i.e "videos/" will give you all files in videos folder.
   * @param recursive get all the files even from nested directories (DEFAULT IS TRUE).
   * @return set of absolute paths for each file in app folder.
   */
  public static Set<String> getLocalMRLS() { return getLocalMRLS("", true); }
  public static Set<String> getLocalMRLS(String nestedFolders, boolean recursive)
  {
    final String searchPath = Config.getProperty("downloadPath") + nestedFolders;
    Queue<File> folders = new LinkedList<>(List.of(new File(searchPath)));
    Set<String> mrls = new HashSet<>();
    while(!folders.isEmpty())
    {
      File dir = folders.poll();
      if(!dir.exists())
        continue;
      File[] files = dir.listFiles();
      for(File file : files)
      {
        if(file.isDirectory() && recursive)
          folders.add(file);

        if(!file.isDirectory())
          mrls.add(file.getAbsolutePath());
      }
    }
    return mrls;
  }
}
