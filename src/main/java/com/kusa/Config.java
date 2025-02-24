package com.kusa;

import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Config
{
  //this is platform specific. it should work fine on unix 
  private static final String appPath = System.getProperty("user.home") + "/.jinzo/";
  private static Properties props;

  static {
    try{
      boolean propsLoaded = false;

      props = new Properties();
      try{
        //when developing you can have a defined properties file in the resources folder.
        //it requires recompilation upon editing.
        props.load(Config.class.getResourceAsStream("/application.properties"));
        propsLoaded = true;
      }catch (Exception e){
        System.out.println("Couldnt find local properties");
      }

      File appFolder = new File(appPath);
      if(!appFolder.exists())
        if(!appFolder.mkdirs())
          System.out.println("FAILED TO FIND OR CREATE APP FOLDER.");

      if(!propsLoaded)
      {
        File propertyFile = new File(appPath + "application.properties");
        if(!propertyFile.exists())
        {
          Files.createFile(Paths.get(propertyFile.getAbsolutePath()));
          writeDefaultAppProperties(propertyFile);
          props.load(new FileReader(propertyFile));
        }
      }

      File driveFolder = new File(getProperty("downloadPath"));
      if(!driveFolder.exists())
        if(!driveFolder.mkdirs())
          System.out.println("FAILED TO FIND OR CREATE DRIVE FOLDER.");

      File videoFolder = new File(getProperty("downloadPath") +  "videos/");
      if(!videoFolder.exists())
        if(!videoFolder.mkdirs())
          System.out.println("FAILED TO FIND OR CREATE VIDEO FOLDER.");

      File photoFolder = new File(getProperty("downloadPath") + "photos/");
      if(!photoFolder.exists())
        if(!photoFolder.mkdirs())
          System.out.println("FAILED TO FIND OR CREATE PHOTOS FOLDER.");

      File tokenFolder = new File(getProperty("tokenStoragePath"));
      if(!tokenFolder.exists())
        if(!tokenFolder.mkdirs())
          System.out.println("FAILED TO FIND OR CREATE TOKEN FOLDER.");


      File appCreds = new File(props.getProperty("googleCredentialsPath"));
      if(!appCreds.exists())
      {
        System.out.println("WARNING!!! NO APP CREDENTIALS FOUND: " + props.getProperty("googleCredentialsPath") + " (DRIVE SERVICES WILL NOT WORK)!");
      }

    } catch (Exception e)
    {
      System.out.println("STARTUP FAILED!" + e);
      System.exit(1);
    }
  }

  public static String getProperty(String propName) 
  { 
    return props == null ? "" : props.getProperty(propName); 
  }

  private static void writeDefaultAppProperties(File file) throws IOException
  {
    final String tokenStorage = "tokenStoragePath=" + appPath + "tokens/";
    final String googleCreds = "googleCredentialsPath=" + appPath + "credentials.json";
    final String downloadPath = "downloadPath=" + appPath + "drive/";
    List<String> properties = List.of(tokenStorage, googleCreds, downloadPath);
    try{
      Files.write(Paths.get(file.getAbsolutePath()), properties); 
    }
    catch(IOException ioException)
    {
      throw ioException;
    }
  }
}
