package com.kusa.service;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

public class LocalService
{

  public static Set<String> getDownloadedVideoNames()
  {
    String downloadDir = "/home/kusa/UwU/jinzo/src/main/resources/drive/";
    File dir = new File(downloadDir);
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
