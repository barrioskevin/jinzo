package com.kusa.service;
import com.kusa.Config;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class LocalService
{

  //aliases
  public static final String mediaPath = Config.getProperty("downloadPath");
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

  /**
   * Returns list of paths for all the downloaded videos in our applications media folder.
   *
   * @return set of absolute paths for each file in apps media/videos folder.
   */
  public static Set<String> getVideoMRLS()
  {
    File dir = new File(videosPath);
    File[] files = dir.listFiles();
    Set<String> names = new HashSet<>();
    if (files != null)
    {
      for(File file : files)
      {
        names.add(file.getAbsolutePath());
      }
    }
    return names;
  }

  public static Set<String> getPhotoMRLS()
  {
    File dir = new File(photosPath);
    File[] files = dir.listFiles();
    Set<String> names = new HashSet<>();
    if (files != null)
    {
      for(File file : files)
      {
        names.add(file.getAbsolutePath());
      }
    }
    return names;
  }


}
