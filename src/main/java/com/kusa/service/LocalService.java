package com.kusa.service;
import com.kusa.Config;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class LocalService
{

  public static Set<String> getDownloadedVideoNames()
  {
    File dir = new File(Config.getProperty("downloadPath") + "videos/");
    File[] files = dir.listFiles();
    Set<String> names = new HashSet<>();
    if (files != null)
    {
      for(File file : files)
      {
        names.add(file.getName());
      }
    }
    return names;
  }

}
