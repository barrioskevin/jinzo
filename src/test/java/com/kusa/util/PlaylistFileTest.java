package com.kusa.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.kusa.playlist.Playlist;
import com.kusa.util.PlaylistFile;
import java.util.List;
import java.io.IOException;

public class PlaylistFileTest {
  @Test
  public void testPlaylistFile() throws IOException { 
    PlaylistFile playlistFile = new PlaylistFile("/home/kusa/UwU/jinzo/src/main/resources/test.playlist");
    Playlist latest = playlistFile.latest();

    assertEquals(playlistFile.sectionNames().size(), playlistFile.sectionCount());
    for(String track : latest.trackList())
    {
      System.out.println("track: " + track);
    }
  }
}
