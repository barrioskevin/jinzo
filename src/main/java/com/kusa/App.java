package com.kusa;
import com.kusa.players.AppFrame;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class App 
{
  private static final String appName = "Jinzo";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static final String TOKEN_STORAGE_PATH = "/home/kusa/UwU/jinzo/tokens/";
  private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException
  {
    InputStream in = App.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if(in == null)
      throw new FileNotFoundException("Resouce not found: " + CREDENTIALS_FILE_PATH); 

    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                                                                      .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKEN_STORAGE_PATH)))
                                                                      .setAccessType("offline")
                                                                      .build();

    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    return credential;
  }

  public static void main(String args[]) throws IOException, GeneralSecurityException
  {
    //new AppFrame();
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(appName).build();
    FileList result = service.files().list().setPageSize(10).setFields("nextPageToken, files(id, name)").execute();
    List<File> files = result.getFiles();
    if(files == null || files.isEmpty())
    {
      System.out.println("NO FILES FOUND...");
    }
    else
    {
      System.out.println("FILES ...");
      for (File file : files)
      {
        System.out.println(file.getName());
      }
    }
  }
}
