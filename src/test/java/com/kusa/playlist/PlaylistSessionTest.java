package com.kusa.playlist;

import static org.junit.jupiter.api.Assertions.*;

import com.kusa.util.PlaylistFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Basic tests for the PlaylistSession object.
 *
 * we need more 'sad' test cases.
 *
 * also more test cases involving the INDEX
 * and playlist controls like next and back.
 */
public class PlaylistSessionTest {

  private File createTempConfig(List<String> content) throws IOException {
    File file = File.createTempFile("test-", ".playlist");
    Files.write(Paths.get(file.getAbsolutePath()), content);
    file.deleteOnExit();
    return file;
  }

  /**
   * Tests that a session can be created from a config.
   */
  @Test
  public void testPlaylistSessionCreation() throws IOException {
    String day = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    List<String> configContent = List.of(
      String.format("[%s]", day),
      "https://realconfigfilevid.mp4",
      "https://realconfigfilevid2.mp4"
    );
    File testConfig = createTempConfig(configContent);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    PlaylistSession session = new PlaylistSession(playlistFile);

    //testing grabing section tracks.
    List<String> sectionTracks = session.sessionTrackList();
    assertEquals(session.size(), 2);
    assertEquals(
      sectionTracks,
      List.of("https://realconfigfilevid.mp4", "https://realconfigfilevid2.mp4")
    );
    assertEquals(sectionTracks, session.configTrackList());
    assertEquals(session.getIndex(), 0);
    assertEquals(session.current(), "https://realconfigfilevid.mp4");
  }

  @Test
  public void testPlaylistSessionAdding() throws IOException {
    String day = LocalDateTime.now().getDayOfWeek().name().toLowerCase();
    List<String> configContent = List.of(
      String.format("[%s]", day),
      "https://realconfigfilevid.mp4",
      "https://realconfigfilevid2.mp4"
    );
    File testConfig = createTempConfig(configContent);
    PlaylistFile playlistFile = new PlaylistFile(testConfig);

    PlaylistSession session = new PlaylistSession(playlistFile);

    //testing grabing section tracks.
    List<String> sectionTracks = session.sessionTrackList();
    assertEquals(session.size(), 2);

    //testing the default grab.
    session.add("https://newfile.mp4");
    assertEquals(session.size(), 3);

    assertEquals(
      session.sessionTrackList(),
      List.of(
        "https://realconfigfilevid.mp4",
        "https://realconfigfilevid2.mp4",
        "https://newfile.mp4"
      )
    );

    //test adding at specific index
    session.add("https://NEWNEWfile.mp4", 2);
    assertEquals(session.size(), 4);
    assertEquals(
      session.sessionTrackList(),
      List.of(
        "https://realconfigfilevid.mp4",
        "https://realconfigfilevid2.mp4",
        "https://NEWNEWfile.mp4",
        "https://newfile.mp4"
      )
    );
  }
}
